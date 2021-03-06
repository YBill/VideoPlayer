package com.bill.player.controller;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bill.baseplayer.controller.GestureVideoController;
import com.bill.player.controller.component.CompleteComponent;
import com.bill.player.controller.component.ControllerComponent;
import com.bill.player.controller.component.ErrorComponent;
import com.bill.player.controller.component.GestureComponent;
import com.bill.player.controller.component.PrepareComponent;

/**
 * author ywb
 * date 2021/12/1
 * desc 通用控制器
 */
public class StandardVideoController extends GestureVideoController {

    public StandardVideoController(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setGestureEnabled(true);
    }

    public void addDefaultControlComponent() {
        addControlComponent(new ControllerComponent(getContext()));
        addControlComponent(new GestureComponent(getContext()));
        addControlComponent(new PrepareComponent(getContext()));
        addControlComponent(new CompleteComponent(getContext()));
        addControlComponent(new ErrorComponent(getContext()));
    }
}
