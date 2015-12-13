package gavin.utils;

import gavin.model.MusicInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtils {
    private static final String mSDCardRoot = "/storage/sdcard1";
    private static String mExternalStorage;
    private Context context;

    public FileUtils(Context context) {
        mExternalStorage = Environment.getExternalStorageDirectory().getPath();
        this.context = context;
    }

    /**
     * 在SD卡上创建文件
     */
    public static File createFileOnSDCard(String dir, String fileName)
            throws IOException {
        if (!isFileExist(dir)){
            createDirOnSDCard(dir);
        }
        File file = new File(mExternalStorage + File.separator + dir + File.separator + fileName);
        file.createNewFile();
        return file;
    }

    /**
     * 在SD卡上创建目录
     */
    public static boolean createDirOnSDCard(String dir) throws IOException {
        File dirFile = new File(mExternalStorage + dir);
        return dirFile.mkdirs();
    }

    /**
     * 将文本文件转换为String
     */
    public String parseFile2String(File file) {
        if (file == null) {
            return "";
        }

        BufferedReader reader;
        String line;
        String buffer = "";
        try {
            reader = new BufferedReader
                    (new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while ((line = reader.readLine()) != null) {
                buffer = buffer + line + "\n";
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer;
    }


    /**
     * 判断SD卡上的文件或目录是否存在
     */
    public static boolean isFileExist(String path) {
        File file = new File(mSDCardRoot + path);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public static File write2SDCard(String path, String fileName, InputStream inputStream) {
        File file = null;
        OutputStream outputStream;
        try {
            if (isFileExist(path)) {
                createDirOnSDCard(path);
            }
            file = createFileOnSDCard(path, fileName);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 4];
            int temp;
            while ((temp = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, temp);
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 获取path目录里面的所有mp3文件
     */
    public ArrayList<MusicInfo> getSongFiles(String path) {
        ArrayList<MusicInfo> musicInfoList = new ArrayList<>();
        File filePath = new File(mSDCardRoot + path);
        if (filePath.listFiles().length != 0) {
            File[] files = filePath.listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".mp3")) {
                    MusicInfo mp3Info = new MusicInfo(file, context);
                    musicInfoList.add(mp3Info);
                }
            }
        }
        return musicInfoList;
    }
}
