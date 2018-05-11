package net.confide.common.app;

import android.os.SystemClock;

import java.io.File;

/**
 *
 */
public class Application extends android.app.Application{

    //单例模式
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 获取缓存文件夹地址
     * @return 当前App缓存文件夹地址
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    /**
     * 获取缓存头像文件的地址
     * @return
     */
    public static File getPortraitTmpFile() {
        //获取头像目录的缓存地址
        File dir = new File(getCacheDirFile(), "portrait");
        //创建对应文件夹
        dir.mkdirs();
        //删除以前缓存的文件
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
        //返回以当前时间戳为名的文件
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }
}
