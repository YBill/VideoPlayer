package com.bill.baseplayer.render;

import android.content.Context;

import com.bill.baseplayer.util.CreateClsFactory;

/**
 * author ywb
 * date 2021/12/11
 * desc 无渲染器
 */
public class NullRenderViewFactory extends CreateClsFactory<IRenderView> {

    public static NullRenderViewFactory create() {
        return new NullRenderViewFactory();
    }

    @Override
    public IRenderView create(Context context) {
        return null;
    }
}
