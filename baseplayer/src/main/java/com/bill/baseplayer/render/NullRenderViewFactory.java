package com.bill.baseplayer.render;

import android.content.Context;

/**
 * author ywb
 * date 2021/12/11
 * desc 无渲染器
 */
public class NullRenderViewFactory extends RenderViewFactory {

    public static NullRenderViewFactory create() {
        return new NullRenderViewFactory();
    }

    @Override
    public IRenderView createRenderView(Context context) {
        return null;
    }
}
