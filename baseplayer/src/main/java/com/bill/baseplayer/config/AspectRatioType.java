package com.bill.baseplayer.config;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * author ywb
 * date 2021/12/11
 * desc 视频比例
 */
@IntDef({
        AspectRatioType.AR_ASPECT_FIT_PARENT,
        AspectRatioType.AR_ASPECT_FILL_PARENT,
        AspectRatioType.AR_ASPECT_WRAP_CONTENT,
        AspectRatioType.AR_MATCH_PARENT,
        AspectRatioType.AR_16_9_FIT_PARENT,
        AspectRatioType.AR_4_3_FIT_PARENT
})
@Retention(RetentionPolicy.SOURCE)
public @interface AspectRatioType {
    int AR_ASPECT_FIT_PARENT = 0; // fitCenter (without clip)
    int AR_ASPECT_FILL_PARENT = 1; // centerCrop (may clip)
    int AR_ASPECT_WRAP_CONTENT = 2; // wrap content (without stretch)
    int AR_MATCH_PARENT = 3; // match parent (may stretch)
    int AR_16_9_FIT_PARENT = 4; // 16:9 (without stretch and clip)
    int AR_4_3_FIT_PARENT = 5; // 4:3 (without stretch and clip)

}