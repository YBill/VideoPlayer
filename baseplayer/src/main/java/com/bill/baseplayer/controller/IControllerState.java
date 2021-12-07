package com.bill.baseplayer.controller;

import com.bill.baseplayer.base.VideoView;

/**
 * author ywb
 * date 2021/11/30
 * desc
 */
public interface IControllerState {

    /**
     * 回调控制器显示和隐藏状态，
     * 此方法可用于控制 ControlComponent 中的控件的跟随手指点击显示和隐藏
     *
     * @param isVisible true 代表要显示， false 代表要隐藏
     */
    void onVisibilityChanged(boolean isVisible);

    /**
     * 回调播放器的播放器状态，如果你只是单纯的想监听此状态，建议使用
     * {@link VideoView#addVideoStateChangeListener(OnVideoStateChangeListener)}
     *
     * @param playState 播放状态
     */
    void onPlayStateChanged(int playState);

    /**
     * 回调播放器的状态，如果你只是单纯的想监听此状态，建议使用
     * {@link VideoView#addVideoStateChangeListener(OnVideoStateChangeListener)}
     *
     * @param playerState 播放器状态
     */
    void onPlayerStateChanged(int playerState);

    /**
     * 回调播放进度，1秒回调一次
     *
     * @param duration 视频总时长
     * @param position 播放进度
     */
    void setProgress(long duration, long position);

    /**
     * 回调控制器是否被锁定，锁定后会产生如下影响：
     * 无法响应滑动手势，双击事件，点击显示和隐藏控制UI，跟随重力感应切换横竖屏
     *
     * @param isLocked 是否锁定
     */
    void onLockStateChanged(boolean isLocked);

    /**
     * 单击
     */
    void onSingleTapConfirmed();

    /**
     * 双击
     */
    void onDoubleTap();
}
