package gavin.lovemusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gavin.lovemusic.constant.R;
import gavin.lovemusic.localmusic.presenter.IMusicListPresenter;
import gavin.lovemusic.localmusic.presenter.MusicListPresenter;
import gavin.lovemusic.localmusic.view.IMusicListView;
import gavin.lovemusic.localmusic.view.LocalMusicFragment;
import gavin.lovemusic.networkmusic.NetworkMusicFragment;
import gavin.lovemusic.playdetail.view.PlayDetailActivity;
import gavin.lovemusic.service.ActivityCommand;
import gavin.lovemusic.service.IServiceListener;
import gavin.lovemusic.service.PlayService;

/**
 * Created by GavinLi
 * on 16-9-18.
 */
public class MainActivity extends AppCompatActivity implements IMusicListView {
    @BindView(R.id.musicAlbum) ImageView mMusicAlbum;
    @BindView(R.id.playButton) ImageButton mPlayButton;
    @BindView(R.id.musicName) TextView mMusicName;
    @BindView(R.id.artist) TextView mArtist;

    private IMusicListPresenter musicListPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        ButterKnife.bind(this);

        musicListPresenter = new MusicListPresenter(this);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new exPhoneCallListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_music_list) {
            musicListPresenter.refreshMusicList(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.playColumn) void onPlayColumnClick() {
        Intent intent = new Intent();
        intent.setClass(this, PlayDetailActivity.class);
        startActivityForResult(intent, 0);
    }

    @OnClick(R.id.playButton) void onPlayButtonClick() {
        musicListPresenter.onPlayButtonClick(this);
    }

    @OnClick(R.id.nextButton) void onNextButtonClick() {
        musicListPresenter.changeMusicStatus(this, ActivityCommand.NEXT_MUSIC);
    }

    /**
     * UI视图更新
     */
    @Override
    public void updateUI() {
        switch (PlayService.musicState) {
            case PlayService.PLAYING:
                mMusicName.setText(PlayService.currentMusic.getMusicName());
                mPlayButton.setBackgroundResource
                        (R.drawable.img_button_notification_play_pause_grey);
                break;
            case PlayService.PAUSE:
            case PlayService.STOP:
                mPlayButton.setBackgroundResource
                        (R.drawable.img_button_notification_play_play_grey);
                break;
        }
        if(PlayService.currentMusic != null) {
            mMusicName.setText(PlayService.currentMusic.getMusicName());
            mArtist.setText(PlayService.currentMusic.getArtist());
            mMusicAlbum.setImageBitmap(PlayService.currentMusic.getAlbum());
        }
    }

    /**
     * 来电监听，当播放音乐时，如果有来电则暂停音乐，当通话结束时继续播放
     */
    private class exPhoneCallListener extends PhoneStateListener {
        boolean musicWaitPlay = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (musicWaitPlay) {
                        musicListPresenter.changeMusicStatus(MainActivity.this, ActivityCommand.RESUME_MUSIC);
                        musicWaitPlay = false;
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    if (PlayService.musicState == PlayService.PLAYING) {
                        musicListPresenter.changeMusicStatus(MainActivity.this, ActivityCommand.PAUSE_MUSIC);
                        musicWaitPlay = true;
                    }
                    break;
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    //用户点击返回键后不会销毁Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }

    private ServiceConnection conn  = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ((PlayService.ServiceBinder) iBinder).getService()
                    .setServiceLinstener((IServiceListener) musicListPresenter);
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    };

    public class SectionPagerAdapter extends FragmentPagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new NetworkMusicFragment();
                case 1: return new LocalMusicFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "下载";
                case 1: return "本地";
            }
            return null;
        }
    }
}