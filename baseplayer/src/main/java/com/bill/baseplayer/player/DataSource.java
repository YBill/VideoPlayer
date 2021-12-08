package com.bill.baseplayer.player;

import androidx.annotation.RawRes;

import java.util.Map;

/**
 * author ywb
 * date 2021/12/3
 * desc
 */
public class DataSource {

    public String mUrl; // 当前播放视频的地址

    public Map<String, String> mHeaders; // 当前视频地址的请求头

    public String mAssetsPath; // assets下视频文件名

    public @RawRes
    int mRawId; // raw下视频文件ID

    public String title; // 视频的title

    public DataSource(String url) {
        this(url, null);
    }

    public DataSource(String url, Map<String, String> headers) {
        mUrl = url;
        mHeaders = headers;
    }

}
