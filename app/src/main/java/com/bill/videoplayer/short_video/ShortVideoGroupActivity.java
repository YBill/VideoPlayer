package com.bill.videoplayer.short_video;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bill.videoplayer.R;

/**
 * author ywb
 * date 2022/12/24
 * desc 短视频聚合
 */
public class ShortVideoGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_video_group);
    }

    public void handleManualControl(View view) {
        Intent intent = new Intent(this, ShortVideoListActivity.class);
        startActivity(intent);
    }

    public void handleAutoPlay(View view) {
        Intent intent = new Intent(this, ShortVideoListActivity.class);
        startActivity(intent);
    }

    public void handleSeamlessJump(View view) {
        Intent intent = new Intent(this, ShortVideoListActivity.class);
        startActivity(intent);
    }
}