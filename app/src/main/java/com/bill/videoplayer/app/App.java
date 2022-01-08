package com.bill.videoplayer.app;

import android.app.Application;

import com.bill.baseplayer.config.AspectRatioType;
import com.bill.baseplayer.config.VideoViewConfig;
import com.bill.baseplayer.config.VideoViewManager;
import com.bill.baseplayer.player.AndroidMediaPlayerFactory;
import com.bill.baseplayer.render.SurfaceRenderViewFactory;
import com.bill.baseplayer.render.TextureRenderViewFactory;
import com.bill.player.exo.ExoPlayerFactory;
import com.bill.player.ijk.IjkPlayerFactory;
import com.bill.videoplayer.BuildConfig;

/**
 * author ywb
 * date 2021/12/1
 * desc Application
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppAuxiliary.getInstance().install(this);

        VideoViewManager.getInstance().setConfig(VideoViewConfig.create()
                .setLogEnabled(BuildConfig.DEBUG)
                .setPlayerFactory(IjkPlayerFactory.create())
                .setRenderViewFactory(TextureRenderViewFactory.create())
                .setEnableOrientation(true)
                .setPlayOnMobileNetwork(false)
                .setScreenScaleType(AspectRatioType.AR_ASPECT_FIT_PARENT)
                .build());
    }
}
