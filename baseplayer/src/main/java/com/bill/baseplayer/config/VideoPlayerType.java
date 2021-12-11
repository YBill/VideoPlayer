package com.bill.baseplayer.config;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author ywb
 * date 2021/12/11
 * desc 播放器类型
 */
@IntDef({
        VideoPlayerType.PLAYER_NORMAL,
        VideoPlayerType.PLAYER_FULL_SCREEN,
        VideoPlayerType.PLAYER_TINY_SCREEN
})
@Retention(RetentionPolicy.SOURCE)
public @interface VideoPlayerType {
    int PLAYER_NORMAL = 0;
    int PLAYER_FULL_SCREEN = 1; // 全屏
    int PLAYER_TINY_SCREEN = 2; // 小窗

}