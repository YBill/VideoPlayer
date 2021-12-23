package com.bill.videoplayer.act;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.config.AspectRatioType;
import com.bill.baseplayer.player.DataSource;
import com.bill.baseplayer.render.SurfaceRenderViewFactory;
import com.bill.baseplayer.render.TextureRenderViewFactory;
import com.bill.player.controller.StandardVideoController;
import com.bill.player.exo.ExoPlayerFactory;
import com.bill.player.ijk.IjkPlayerFactory;
import com.bill.videoplayer.R;
import com.bill.videoplayer.component.DebugInfoComponent;

public class ParallelPlayerActivity extends AppCompatActivity {

    private static final String URL_1 = "https://rmrbtest-image.peopleapp.com/upload/video/201809/1537349021125fcfb438615c1b.mp4";

    private VideoView videoView1;
    private VideoView videoView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallel_player);

        videoView1 = findViewById(R.id.video_player_1);
        videoView2 = findViewById(R.id.video_player_2);

        videoView1.setEnableAudioFocus(false);
        videoView1.setPlayerFactory(IjkPlayerFactory.create());
        videoView1.setRenderViewFactory(TextureRenderViewFactory.create());
        videoView1.setScreenScaleType(AspectRatioType.AR_16_9_FIT_PARENT);

        videoView2.setEnableAudioFocus(false);
        videoView2.setPlayerFactory(ExoPlayerFactory.create());
        videoView2.setRenderViewFactory(SurfaceRenderViewFactory.create());
        videoView2.setScreenScaleType(AspectRatioType.AR_ASPECT_FIT_PARENT);

        StandardVideoController videoController1 = new StandardVideoController(this);
        videoController1.addDefaultControlComponent();
        videoController1.addControlComponent(new DebugInfoComponent(this));
        videoView1.setVideoController(videoController1);
        DataSource dataSource1 = new DataSource();
        dataSource1.mUrl = URL_1;
        dataSource1.mTitle = "喜欢一个人";
        videoView1.setDataSource(dataSource1);
        videoView1.start();

        StandardVideoController videoController2 = new StandardVideoController(this);
        videoController2.addDefaultControlComponent();
        videoController2.addControlComponent(new DebugInfoComponent(this));
        videoView2.setVideoController(videoController2);
        DataSource dataSource2 = new DataSource();
        dataSource2.mUrl = URL_1;
        dataSource2.mTitle = "喜欢一个人";
        videoView2.setDataSource(dataSource2);
        videoView2.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView1 != null)
            videoView1.release();
        if (videoView2 != null)
            videoView2.release();
    }
}