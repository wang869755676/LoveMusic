package gavin.lovemusic.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by Gavin on 2015/8/23.
 * MP3歌曲模型
 */
@Entity
public class Music {
    @Id
    private long id;
    @NotNull
    private String title;   //歌曲名称
    @NotNull
    private String artist;   //歌手名称
    @NotNull
    private String album;    //歌曲专辑名称
    @NotNull
    private long duration;        //歌曲时长
    @NotNull
    private String image;  //专辑封面路径
    private String path;

    private String lyricPath;

    @Generated(hash = 1263212761)
    public Music() {
    }

    @Generated(hash = 1990994493)
    public Music(long id, @NotNull String title, @NotNull String artist, @NotNull String album,
            long duration, @NotNull String image, String path, String lyricPath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.image = image;
        this.path = path;
        this.lyricPath = lyricPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getDuration() {
        return duration;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Music)) return false;

        Music music = (Music) o;

        if (title != null ? !title.equals(music.title) : music.title != null) return false;
        //noinspection SimplifiableIfStatement
        if (artist != null ? !artist.equals(music.artist) : music.artist != null) return false;
        return album != null ? album.equals(music.album) : music.album == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (artist != null ? artist.hashCode() : 0);
        result = 31 * result + (album != null ? album.hashCode() : 0);
        return result;
    }

    public String getLyricPath() {
        return this.lyricPath;
    }

    public void setLyricPath(String lyricPath) {
        this.lyricPath = lyricPath;
    }
}
