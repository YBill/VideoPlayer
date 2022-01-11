package com.bill.videoplayer.cache;

import android.text.TextUtils;

import com.danikula.videocache.ProxyCacheUtils;
import com.danikula.videocache.file.FileNameGenerator;

/**
 * author ywb
 * date 2021/1/11
 * desc 自定义文件名
 */
public class VideoFileNameGenerator implements FileNameGenerator {

    private static final int MAX_EXTENSION_LENGTH = 4;

    public String generate(String url) {
        try {
            return url.substring(url.lastIndexOf("/"));
        } catch (Exception e) {
            e.printStackTrace();
            String extension = getExtension(url);
            String name = ProxyCacheUtils.computeMD5(url);
            return TextUtils.isEmpty(extension) ? name : name + "." + extension;
        }
    }

    private String getExtension(String url) {
        int dotIndex = url.lastIndexOf('.');
        int slashIndex = url.lastIndexOf('/');
        return dotIndex != -1 && dotIndex > slashIndex && dotIndex + 2 + MAX_EXTENSION_LENGTH > url.length() ?
                url.substring(dotIndex + 1, url.length()) : "";
    }
}
