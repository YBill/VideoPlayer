package com.bill.player.controller.component;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.controller.BaseComponent;
import com.bill.baseplayer.util.Utils;
import com.bill.player.controller.R;

import java.util.Locale;

/**
 * author ywb
 * date 2021/12/3
 * desc
 */
public class ControllerView extends BaseComponent implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private View topView;
    private View backView;
    private AppCompatTextView titleTv;
    private View bottomView;
    private AppCompatTextView curTimeTv;
    private AppCompatTextView totalTimeTv;
    private AppCompatImageView playIv;
    private AppCompatImageView fullscreenIv;
    private SeekBar seekBar;
    private ProgressBar progressBar;

    private boolean mIsDragging;

    private Animation mShowAnim;
    private Animation mHideAnim;

    public ControllerView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        initView();
        this.setVisibility(GONE);

        mShowAnim = new AlphaAnimation(0f, 1f);
        mShowAnim.setDuration(300);
        mHideAnim = new AlphaAnimation(1f, 0f);
        mHideAnim.setDuration(300);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_component_controller, this, true);
        topView = this.findViewById(R.id.fl_v_ctr_top);
        backView = this.findViewById(R.id.iv_c_ctr_back);
        titleTv = this.findViewById(R.id.tv_c_ctr_title);
        bottomView = this.findViewById(R.id.fl_v_ctr_bottom);
        curTimeTv = this.findViewById(R.id.tv_v_ctr_cur_time);
        totalTimeTv = this.findViewById(R.id.tv_v_ctr_total_time);
        playIv = this.findViewById(R.id.iv_v_ctr_play);
        fullscreenIv = this.findViewById(R.id.tv_v_ctr_fullscreen);
        seekBar = this.findViewById(R.id.tv_v_ctr_seekbar);
        progressBar = this.findViewById(R.id.tv_v_ctr_progress_bar);

        backView.setOnClickListener(this);
        playIv.setOnClickListener(this);
        fullscreenIv.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onVisibilityChanged(boolean isVisible) {
        super.onVisibilityChanged(isVisible);
        if (isVisible) {
            topView.setVisibility(VISIBLE);
            bottomView.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);

            topView.startAnimation(mShowAnim);
            bottomView.startAnimation(mShowAnim);
            progressBar.startAnimation(mHideAnim);

        } else {
            topView.setVisibility(GONE);
            bottomView.setVisibility(GONE);
            progressBar.setVisibility(VISIBLE);

            topView.startAnimation(mHideAnim);
            bottomView.startAnimation(mHideAnim);
            progressBar.startAnimation(mShowAnim);

        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);

        if (playState == VideoView.STATE_PREPARING) {
            if (!TextUtils.isEmpty(mControlWrapper.getDataSource().title))
                titleTv.setText(mControlWrapper.getDataSource().title);
            mControlWrapper.show();
        }

        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                this.setVisibility(GONE);
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
                seekBar.setProgress(0);
                seekBar.setSecondaryProgress(0);
                break;
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
                this.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                this.setVisibility(VISIBLE);
                playIv.setSelected(true);
                mControlWrapper.startProgress();
                break;
            case VideoView.STATE_PAUSED:
                playIv.setSelected(false);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                fullscreenIv.setSelected(false);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                fullscreenIv.setSelected(true);
                break;
        }

    }

    @Override
    public void setProgress(long duration, long position) {
        super.setProgress(duration, position);
        if (mIsDragging) {
            return;
        }

        if (seekBar != null) {
            if (duration > 0) {
                seekBar.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * seekBar.getMax());
                seekBar.setProgress(pos);
                progressBar.setProgress(pos);
            } else {
                seekBar.setEnabled(false);
            }
            int percent = mControlWrapper.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                seekBar.setSecondaryProgress(seekBar.getMax());
                progressBar.setSecondaryProgress(progressBar.getMax());
            } else {
                seekBar.setSecondaryProgress(percent * 10);
                progressBar.setSecondaryProgress(percent * 10);
            }
        }

        if (totalTimeTv != null)
            totalTimeTv.setText(stringForTime(duration));
        if (curTimeTv != null)
            curTimeTv.setText(stringForTime(position));

    }

    /**
     * 格式化时间
     */
    private static String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    private void toggleFullScreen() {
        Activity activity = Utils.scanForActivity(getContext());
        mControlWrapper.toggleFullScreen(activity);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_c_ctr_back) {
            if (mControlWrapper.isFullScreen()) {
                toggleFullScreen();
            } else {
                Activity activity = Utils.scanForActivity(getContext());
                activity.finish();
            }
        } else if (v.getId() == R.id.iv_v_ctr_play) {
            mControlWrapper.togglePlay();
        } else if (v.getId() == R.id.tv_v_ctr_fullscreen) {
            toggleFullScreen();
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * progress) / this.seekBar.getMax();
        if (curTimeTv != null)
            curTimeTv.setText(stringForTime((int) newPosition));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        mControlWrapper.stopProgress();
        mControlWrapper.cancelHideCountdown();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mIsDragging = false;
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / this.seekBar.getMax();
        mControlWrapper.seekTo((int) newPosition);
        mControlWrapper.startProgress();
        mControlWrapper.autoHideCountdown();
    }
}
