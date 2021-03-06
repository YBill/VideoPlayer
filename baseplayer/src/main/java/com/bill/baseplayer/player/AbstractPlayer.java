package com.bill.baseplayer.player;

import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Map;

/**
 * author ywb
 * date 2021/11/24
 * desc
 */
public abstract class AbstractPlayer {

    /**
     * 视频/音频开始渲染
     */
    public static final int MEDIA_INFO_RENDERING_START = 3;

    /**
     * 视频旋转信息
     */
    public static final int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;

    /**
     * 播放器事件回调
     */
    protected PlayerEventListener mPlayerEventListener;

    /**
     * 初始化播放器
     */
    public abstract void initPlayer();

    /**
     * 设置播放地址
     *
     * @param dataSource 视频资源
     */
    public abstract void setDataSource(DataSource dataSource);

    /**
     * 播放
     */
    public abstract void start();

    /**
     * 暂停
     */
    public abstract void pause();

    /**
     * 停止
     */
    public abstract void stop();

    /**
     * 准备开始播放（异步）
     */
    public abstract void prepareAsync();

    /**
     * 重置播放器
     */
    public abstract void reset();

    /**
     * 是否正在播放
     */
    public abstract boolean isPlaying();

    /**
     * 调整进度
     */
    public abstract void seekTo(long time);

    /**
     * 释放播放器
     */
    public abstract void release();

    /**
     * 获取当前播放的位置
     */
    public abstract long getCurrentPosition();

    /**
     * 获取视频总时长
     */
    public abstract long getDuration();

    /**
     * 获取缓冲百分比
     */
    public abstract int getBufferedPercentage();

    /**
     * 设置渲染视频的View,主要用于TextureView
     */
    public abstract void setSurface(Surface surface);

    /**
     * 设置渲染视频的View,主要用于SurfaceView
     */
    public abstract void setDisplay(SurfaceHolder holder);

    /**
     * 设置音量
     */
    public abstract void setVolume(float v1, float v2);

    /**
     * 设置是否循环播放
     */
    public abstract void setLooping(boolean isLooping);

    /**
     * 设置其他播放配置
     */
    public abstract void setOptions();

    /**
     * 设置播放速度
     */
    public abstract void setSpeed(float speed);

    /**
     * 获取播放速度
     */
    public abstract float getSpeed();

    /**
     * 获取当前缓冲的网速
     */
    public abstract long getTcpSpeed();

    public void setPlayerEventListener(PlayerEventListener playerEventListener) {
        this.mPlayerEventListener = playerEventListener;
    }

    public interface PlayerEventListener {

        void onError();

        void onCompletion();

        void onInfo(int what, int extra);

        void onPrepared();

        void onVideoSizeChanged(int width, int height);

        void onBufferingUpdate(int percent);

    }

}
