package com.bill.videoplayer;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.player.DataSource;
import com.bill.player.controller.StandardVideoController;
import com.bill.videoplayer.component.DebugInfoComponent;

public class PlayerTestAct extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_test);

        videoView = findViewById(R.id.video_player);
        StandardVideoController mController = new StandardVideoController(this);
        mController.addDefaultControlComponent();
        mController.addControlComponent(new DebugInfoComponent(this));
        videoView.setVideoController(mController);

        // http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4
        // https://rmrbtest-image.peopleapp.com/upload/video/201809/1537349021125fcfb438615c1b.mp4
        DataSource dataSource = new DataSource("https://rmrbtest-image.peopleapp.com/upload/video/201809/1537349021125fcfb438615c1b.mp4");
//        dataSource.mAssetsPath = "avengers.mp4";
//        dataSource.mRawId = R.raw.avengers;
        dataSource.title = "复仇者联盟4";
        videoView.setDataSource(dataSource);
        videoView.start();

    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        if (videoView != null && videoView.isFullScreen()) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            videoView.exitFullScreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
    }
}