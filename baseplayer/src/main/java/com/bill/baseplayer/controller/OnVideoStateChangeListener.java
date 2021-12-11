package com.bill.baseplayer.controller;

import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.config.VideoPlayerType;

/**
 * author ywb
 * date 2021/11/30
 * desc 状态改变监听器
 */
public interface OnVideoStateChangeListener {

    /**
     * 播放器状态 {@link VideoPlayerType}
     *
     * @param playerState 播放器状态
     */
    void onPlayerStateChanged(@VideoPlayerType int playerState);

    /**
     * 播放状态 {@link VideoPlayType}
     *
     * @param playState 播放状态
     */
    void onPlayStateChanged(@VideoPlayType int playState);

}
