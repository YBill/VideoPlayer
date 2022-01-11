package com.bill.videoplayer.cache;

import android.content.Context;

import com.bill.videoplayer.BuildConfig;
import com.bill.videoplayer.app.AppAuxiliary;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.Logger;

import java.io.File;

/**
 * author ywb
 * date 2021/1/11
 * desc
 */
public class VideoCacheManager {

    public static class SingletonHolder {
        private static final VideoCacheManager instance = new VideoCacheManager();
    }

    public static VideoCacheManager getInstance() {
        return SingletonHolder.instance;
    }

    private final HttpProxyCacheServer proxyCacheServer;

    private VideoCacheManager() {
        Context context = AppAuxiliary.getInstance().getAppContext();
        proxyCacheServer = newProxy(context);
    }

    public HttpProxyCacheServer getProxy() {
        return proxyCacheServer;
    }

    private HttpProxyCacheServer newProxy(Context context) {
        Logger.setDebug(BuildConfig.DEBUG);
        return new HttpProxyCacheServer.Builder(context)
//                .maxCacheSize(512 * 1024 * 1024) // 512MB for cache
                .maxCacheFilesCount(20)
                .fileNameGenerator(new VideoFileNameGenerator())
                .build();
    }


    /**
     * 删除所有缓存文件
     */
    public boolean clearAllCache() {
        return deleteAll(proxyCacheServer.getCacheRoot());
    }

    /**
     * 删除url对应默认缓存文件
     */
    public boolean clearCache(String url) {
        File cacheFile = proxyCacheServer.getCacheFile(url);
        File cacheTempFile = proxyCacheServer.getTempCacheFile(url);
        return deleteFile(cacheFile) && deleteFile(cacheTempFile);
    }

    private boolean deleteFile(File file) {
        if (file == null || !file.exists())
            return true;
        return file.delete();
    }

    private boolean deleteAll(File file) {
        if (file == null || !file.exists())
            return true;
        if (!file.isFile()) {
            String[] childFilePath = file.list();
            if (childFilePath == null)
                return true;
            for (String path : childFilePath) {
                File childFile = new File(file.getAbsoluteFile() + File.separator + path);
                deleteAll(childFile);
            }
        }
        return file.delete();
    }

}
