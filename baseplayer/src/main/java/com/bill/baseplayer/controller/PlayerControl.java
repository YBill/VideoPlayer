package com.bill.baseplayer.controller;

import android.graphics.Bitmap;

import com.bill.baseplayer.player.DataSource;

/**
 * author ywb
 * date 2021/11/24
 * desc 播放器方法
 */
public interface PlayerControl {

    void start();

    void pause();

    void release();

    void replay(boolean resetPosition);

    long getDuration();

    long getCurrentPosition();

    void seekTo(long pos);

    boolean isPlaying();

    int getBufferedPercentage();

    void startFullScreen();

    void stopFullScreen();

    boolean isFullScreen();

    void setMute(boolean isMute);

    boolean isMute();

    void setScreenScaleType(int screenScaleType);

    void setSpeed(float speed);

    float getSpeed();

    long getTcpSpeed();

    void setMirrorRotation(boolean enable);

    Bitmap doScreenShot();

    int[] getVideoSize();

    void setRotation(float rotation);

    void startTinyScreen();

    void stopTinyScreen();

    boolean isTinyScreen();

    DataSource getDataSource();

}
