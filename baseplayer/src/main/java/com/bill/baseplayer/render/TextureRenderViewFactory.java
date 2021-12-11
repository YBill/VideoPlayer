package com.bill.baseplayer.render;

import android.content.Context;

import com.bill.baseplayer.util.CreateClsFactory;

/**
 * author ywb
 * date 2021/11/24
 * desc TextureView
 */
public class TextureRenderViewFactory extends CreateClsFactory<IRenderView> {

    public static TextureRenderViewFactory create() {
        return new TextureRenderViewFactory();
    }

    @Override
    public IRenderView create(Context context) {
        return new TextureRenderView(context);
    }
}
