package com.bill.baseplayer.config;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author ywb
 * date 2021/12/11
 * desc 视频播放类型
 */
@IntDef({
        VideoPlayType.STATE_IDLE,
        VideoPlayType.STATE_PREPARING,
        VideoPlayType.STATE_PREPARED,
        VideoPlayType.STATE_PLAYING,
        VideoPlayType.STATE_PAUSED,
        VideoPlayType.STATE_COMPLETED,
        VideoPlayType.STATE_START_ABORT,
        VideoPlayType.STATE_ERROR
})
@Retention(RetentionPolicy.SOURCE)
public @interface VideoPlayType {
    int STATE_IDLE = 0; // 未初始化状态
    int STATE_PREPARING = 1; // 准备中（调用prepareAsync方法）
    int STATE_PREPARED = 2; // 准备完成（onPrepare回调）
    int STATE_PLAYING = 3; // 播放状态
    int STATE_PAUSED = 4; // 暂停状态
    int STATE_COMPLETED = 5; // 播放完成
    int STATE_START_ABORT = 6; // 开始播放中止
    int STATE_ERROR = 10; // 错误

}