package com.bill.baseplayer.render;

import android.content.Context;

import com.bill.baseplayer.util.CreateClsFactory;

/**
 * author ywb
 * date 2021/11/24
 * desc SurfaceView
 */
public class SurfaceRenderViewFactory extends CreateClsFactory<IRenderView> {

    public static SurfaceRenderViewFactory create() {
        return new SurfaceRenderViewFactory();
    }

    @Override
    public IRenderView create(Context context) {
        return new SurfaceRenderView(context);
    }
}