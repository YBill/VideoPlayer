package com.bill.baseplayer.config;

import android.app.Application;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.util.MLog;

import java.util.LinkedHashMap;

/**
 * author ywb
 * date 2021/11/26
 * desc 视频播放器管理器，管理当前正在播放的 VideoView，以及播放器配置
 * 也可以用来保存常驻内存的VideoView，但是要注意通过Application Context创建，以免内存泄漏
 */
public class VideoViewManager {

    public static class SingletonHolder {
        private static final VideoViewManager instance = new VideoViewManager();
    }

    public static VideoViewManager getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 全局配置
     */
    private VideoViewConfig mConfig;

    /**
     * 在移动环境下调用start()后是否继续播放，默认继续播放(true)
     */
    private boolean mPlayOnMobileNetwork;

    /**
     * 保存VideoView的容器
     */
    private final LinkedHashMap<String, VideoView> mVideoViews = new LinkedHashMap<>();

    private VideoViewManager() {
        init(getConfig());
    }

    private void init(VideoViewConfig config) {
        mPlayOnMobileNetwork = config.mPlayOnMobileNetwork;
    }

    public void setConfig(VideoViewConfig config) {
        this.mConfig = config;
        init(config);
    }

    public VideoViewConfig getConfig() {
        if (mConfig == null)
            setConfig(VideoViewConfig.create().build());
        return mConfig;
    }

    /**
     * 在移动环境下调用start()后是否继续播放，默认继续播放(true)
     */
    public boolean isPlayOnMobileNetwork() {
        return mPlayOnMobileNetwork;
    }

    /**
     * 在移动环境下调用start()后是否继续播放 false:不播放，此时应该考虑监听STATE_START_ABORT状态处理UI
     */
    public void setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
        mPlayOnMobileNetwork = playOnMobileNetwork;
    }

    /**
     * 添加VideoView
     *
     * @param tag 相同tag的VideoView只会保存一个，如果tag相同则会release并移除前一个
     */
    public void add(VideoView videoView, String tag) {
        if (!(videoView.getContext() instanceof Application)) {
            MLog.w("The Context of this VideoView is not an Application Context," +
                    "you must remove it after release,or it will lead to memory leek.");
        }
        VideoView old = get(tag);
        if (old != null) {
            old.release();
            remove(tag);
        }
        mVideoViews.put(tag, videoView);
    }

    public VideoView get(String tag) {
        return mVideoViews.get(tag);
    }

    public void remove(String tag) {
        mVideoViews.remove(tag);
    }

    public void removeAll() {
        mVideoViews.clear();
    }

    /**
     * 释放掉和tag关联的VideoView，并将其从VideoViewManager中移除
     */
    public void releaseByTag(String tag) {
        releaseByTag(tag, true);
    }

    public void releaseByTag(String tag, boolean isRemove) {
        VideoView videoView = get(tag);
        if (videoView != null) {
            videoView.release();
            if (isRemove) {
                remove(tag);
            }
        }
    }

    /*public boolean onBackPress(String tag) {
        VideoView videoView = get(tag);
        if (videoView == null) return false;
        return videoView.onBackPressed();
    }*/

}
