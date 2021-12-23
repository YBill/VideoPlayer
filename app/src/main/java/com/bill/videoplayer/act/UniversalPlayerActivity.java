package com.bill.videoplayer.act;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.config.AspectRatioType;
import com.bill.baseplayer.player.DataSource;
import com.bill.player.controller.StandardVideoController;
import com.bill.player.controller.component.PrepareComponent;
import com.bill.videoplayer.R;
import com.bill.videoplayer.component.DebugInfoComponent;
import com.bill.videoplayer.util.KeyboardHelper;
import com.bill.videoplayer.util.Utils;
import com.bumptech.glide.Glide;

public class UniversalPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String URL_1 = "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4";
    private static final String URL_2 = "https://rmrbtest-image.peopleapp.com/upload/video/201809/1537349021125fcfb438615c1b.mp4";

    private VideoView videoView;
    private StandardVideoController videoController;

    private AppCompatEditText inputUrlEt;
    private AppCompatButton tinyBtn;
    private AppCompatButton loopBtn;
    private AppCompatButton muteBtn;
    private AppCompatButton mirrorRotateBtn;

    private boolean isTiny = false;
    private boolean isLoop = false;
    private boolean isMute = false;
    private boolean isMirrorRotate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal_player);
        initView();
        initPlayer();
    }

    private void initPlayer() {
        videoView = findViewById(R.id.video_player);

        // set controller
        videoController = new StandardVideoController(this);
        videoController.addDefaultControlComponent();
        videoController.addControlComponent(new DebugInfoComponent(this));
        videoView.setVideoController(videoController);

        // 设置封面图
        ImageView thumbIv = ((PrepareComponent) videoController.getControlComponent(
                PrepareComponent.class.getSimpleName())).getThumbIv();
        thumbIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(this).load(URL_2).into(thumbIv);

        // set url
        DataSource dataSource = new DataSource();
        dataSource.mUrl = URL_2;
        dataSource.mTitle = "喜欢一个人";
        videoView.setDataSource(dataSource);

        // play
        videoView.start();

    }

    private void initView() {
        inputUrlEt = findViewById(R.id.et_input_url);
        tinyBtn = findViewById(R.id.btn_tiny_screen);
        loopBtn = findViewById(R.id.btn_loop);
        muteBtn = findViewById(R.id.btn_mute);
        mirrorRotateBtn = findViewById(R.id.btn_mirror_rotate);

        findViewById(R.id.btn_fit_center).setOnClickListener(this);
        findViewById(R.id.btn_center_crop).setOnClickListener(this);
        findViewById(R.id.btn_center).setOnClickListener(this);
        findViewById(R.id.btn_fit_xy).setOnClickListener(this);
        findViewById(R.id.btn_4_3).setOnClickListener(this);
        findViewById(R.id.btn_16_9).setOnClickListener(this);

        findViewById(R.id.btn_speed_0_5).setOnClickListener(this);
        findViewById(R.id.btn_speed_1_0).setOnClickListener(this);
        findViewById(R.id.btn_speed_1_5).setOnClickListener(this);
        findViewById(R.id.btn_speed_2_0).setOnClickListener(this);

        tinyBtn.setOnClickListener(this);
        loopBtn.setOnClickListener(this);
        muteBtn.setOnClickListener(this);
        mirrorRotateBtn.setOnClickListener(this);

        findViewById(R.id.btn_clear_focus).setOnClickListener(this);
        findViewById(R.id.btn_play_url).setOnClickListener(this);
        findViewById(R.id.btn_assets_file).setOnClickListener(this);
        findViewById(R.id.btn_raw_file).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fit_center:
                switchScreenScaleType(AspectRatioType.AR_ASPECT_FIT_PARENT);
                break;
            case R.id.btn_center_crop:
                switchScreenScaleType(AspectRatioType.AR_ASPECT_FILL_PARENT);
                break;
            case R.id.btn_center:
                switchScreenScaleType(AspectRatioType.AR_ASPECT_WRAP_CONTENT);
                break;
            case R.id.btn_fit_xy:
                switchScreenScaleType(AspectRatioType.AR_MATCH_PARENT);
                break;
            case R.id.btn_4_3:
                switchScreenScaleType(AspectRatioType.AR_16_9_FIT_PARENT);
                break;
            case R.id.btn_16_9:
                switchScreenScaleType(AspectRatioType.AR_4_3_FIT_PARENT);
                break;

            case R.id.btn_speed_0_5:
                switchSpeed(0.5f);
                break;
            case R.id.btn_speed_1_0:
                switchSpeed(1.0f);
                break;
            case R.id.btn_speed_1_5:
                switchSpeed(1.5f);
                break;
            case R.id.btn_speed_2_0:
                switchSpeed(2.0f);
                break;

            case R.id.btn_tiny_screen:
                if (isTiny) {
                    videoView.setVideoController(videoController);
                    videoView.exitTinyScreen();
                    tinyBtn.setText("小窗");
                } else {
                    videoView.setVideoController(null);
                    videoView.enterTinyScreen();
                    tinyBtn.setText("关小窗");
                }
                isTiny = !isTiny;
                break;
            case R.id.btn_loop:
                if (isLoop) {
                    videoView.setLooping(false);
                    loopBtn.setText("循环");
                } else {
                    videoView.setLooping(true);
                    loopBtn.setText("不循环");
                }
                isLoop = !isLoop;
                break;
            case R.id.btn_mute:
                if (isMute) {
                    videoView.setMute(false);
                    muteBtn.setText("静音");
                } else {
                    videoView.setMute(true);
                    muteBtn.setText("不静音");
                }
                isMute = !isMute;
                break;
            case R.id.btn_mirror_rotate:
                if (isMirrorRotate) {
                    videoView.setMirrorRotation(false);
                    mirrorRotateBtn.setText("镜像旋转");
                } else {
                    videoView.setMirrorRotation(true);
                    mirrorRotateBtn.setText("还原");
                }
                isMirrorRotate = !isMirrorRotate;
                break;

            case R.id.btn_assets_file:
                inputUrlEt.clearFocus(); // 取消输入框焦点
                KeyboardHelper.hideSoftInput(inputUrlEt); // 关闭软键盘
                videoView.release();
                DataSource assetsDs = new DataSource();
                assetsDs.mAssetsPath = "avengers.mp4";
                assetsDs.mTitle = "/assets/avengers.mp4";
                videoView.setDataSource(assetsDs);
                videoView.start();
                break;
            case R.id.btn_raw_file:
                inputUrlEt.clearFocus(); // 取消输入框焦点
                KeyboardHelper.hideSoftInput(inputUrlEt); // 关闭软键盘
                videoView.release();
                DataSource rawDs = new DataSource();
                rawDs.mRawId = R.raw.avengers;
                rawDs.mTitle = "/res/raw/avengers.mp4";
                videoView.setDataSource(rawDs);
                videoView.start();
                break;
            case R.id.btn_clear_focus:
                inputUrlEt.clearFocus(); // 取消输入框焦点
                KeyboardHelper.hideSoftInput(inputUrlEt); // 关闭软键盘
                break;
            case R.id.btn_play_url:
                inputUrlEt.clearFocus(); // 取消输入框焦点
                KeyboardHelper.hideSoftInput(inputUrlEt); // 关闭软键盘
                videoView.release();
                String url = inputUrlEt.getText().toString().trim();
                if (!TextUtils.isEmpty(url)) {
                    DataSource urlDs = new DataSource(url);
                    urlDs.mTitle = "Input Url";
                    videoView.setDataSource(urlDs);
                    videoView.start();
                }
                break;
        }
    }

    private void switchSpeed(float speed) {
        videoView.setSpeed(speed);
        ((DebugInfoComponent) videoView.getVideoController().getControlComponent(
                DebugInfoComponent.class.getSimpleName())).refreshUI();
    }

    private void switchScreenScaleType(@AspectRatioType int screenScaleType) {
        videoView.setScreenScaleType(screenScaleType);
        ((DebugInfoComponent) videoView.getVideoController().getControlComponent(
                DebugInfoComponent.class.getSimpleName())).refreshUI();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        if (videoView != null && videoView.isFullScreen()) {
            if (videoView.getVideoController() != null && videoView.getVideoController().isLocked()) {
                Utils.toast("请先解锁屏幕");
                return;
            }
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            videoView.exitFullScreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null)
            videoView.release();
    }
}