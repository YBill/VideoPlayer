package com.bill.player.controller;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bill.baseplayer.base.BaseVideoController;
import com.bill.player.controller.component.DebugInfoComponent;

/**
 * author ywb
 * date 2021/12/1
 * desc 通用控制器
 */
public class StandardVideoController extends BaseVideoController {

    public StandardVideoController(@NonNull Context context) {
        super(context);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addDefaultControlComponent() {
        addControlComponent(new DebugInfoComponent(getContext()));
    }
}
