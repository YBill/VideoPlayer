package com.bill.videoplayer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bill.baseplayer.base.VideoView;
import com.bill.player.controller.StandardVideoController;

public class PlayerTestAct extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_test);

        videoView = findViewById(R.id.video_player);
        StandardVideoController mController = new StandardVideoController(this);
        mController.addDefaultControlComponent();
        videoView.setVideoController(mController);

//        videoView.setUrl("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4");
        videoView.setUrl("https://rmrbtest-image.peopleapp.com/upload/video/201809/1537349021125fcfb438615c1b.mp4");
        videoView.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }
}