package com.bill.baseplayer.controller;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * author ywb
 * date 2021/11/26
 * desc 可继承此接口实现自己的控制ui，以及监听播放器的状态
 */
public interface IControlComponent extends IComponentState {

    /**
     * 将 ControlWrapper 传递到当前 ControlComponent 中
     */
    void attach(@NonNull ControlWrapper controlWrapper);

    /**
     * 如果 ControlComponent 是 View，返回当前控件（this）即可
     * 如果不是，返回null
     */
    @Nullable
    View getView();

    /**
     * 组件是否是游离的
     * 游离的组件不会被添加到控制器视图上，但是确拥有组件的功能
     */
    boolean isDissociate();

}
