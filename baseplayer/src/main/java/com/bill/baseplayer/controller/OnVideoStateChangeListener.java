package com.bill.baseplayer.controller;

/**
 * author ywb
 * date 2021/11/30
 * desc 状态改变监听器
 */
public interface OnVideoStateChangeListener {

    /**
     * 播放器状态（全屏、小窗等）
     *
     * @param playerState 播放器状态
     */
    void onPlayerStateChanged(int playerState);

    /**
     * 播放状态
     *
     * @param playState 播放状态
     */
    void onPlayStateChanged(int playState);

}
