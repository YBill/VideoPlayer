package com.bill.baseplayer.controller;

/**
 * author ywb
 * date 2021/11/24
 * desc 控制器封装的功能
 */
public interface IVideoController {

    /**
     * 控制视图是否处于显示状态
     */
    boolean isShowing();

    /**
     * 显示控制视图
     */
    void show();

    /**
     * 隐藏控制视图
     */
    void hide();

    /**
     * 开始控制视图自动隐藏倒计时
     * 设置倒计时时间使用 {@link BaseVideoController#setAutoHideCountdown(int) }
     */
    void autoHideCountdown();

    /**
     * 取消控制视图自动隐藏倒计时
     */
    void cancelHideCountdown();

    /**
     * 设置锁定状态
     *
     * @param locked 是否锁定
     */
    void setLocked(boolean locked);

    /**
     * 是否处于锁定状态
     */
    boolean isLocked();

    /**
     * 开始刷新进度
     */
    void startProgress();

    /**
     * 停止刷新进度
     */
    void stopProgress();

    /**
     * 是否需要适配刘海
     */
    boolean hasCutout();

    /**
     * 获取刘海的高度
     */
    int getCutoutHeight();

}
