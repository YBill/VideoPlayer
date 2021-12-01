package com.bill.videoplayer;

import android.app.Application;

import com.bill.baseplayer.config.VideoViewConfig;
import com.bill.baseplayer.config.VideoViewManager;
import com.bill.player.ijk.IjkPlayerFactory;

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
                .setPlayerFactory(IjkPlayerFactory.create())
                .build());
    }
}
