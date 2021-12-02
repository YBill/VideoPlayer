package com.bill.videoplayer;

import android.app.Application;

import com.bill.baseplayer.config.VideoViewConfig;
import com.bill.baseplayer.config.VideoViewManager;
import com.bill.baseplayer.render.SurfaceRenderViewFactory;
import com.bill.player.exo.ExoPlayerFactory;

/**
 * author ywb
 * date 2021/12/1
 * desc
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VideoViewManager.getInstance().setConfig(VideoViewConfig.create()
                .setLogEnabled(BuildConfig.DEBUG)
                .setPlayerFactory(ExoPlayerFactory.create())
                .setRenderViewFactory(SurfaceRenderViewFactory.create())
                .build());
    }
}
