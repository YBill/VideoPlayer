package com.bill.player.controller.component;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.config.VideoPlayerType;
import com.bill.baseplayer.util.Utils;
import com.bill.player.controller.R;
import com.bill.player.controller.util.ComponentUtils;

/**
 * author ywb
 * date 2021/12/3
 * desc 控制组件
 */
public class ControllerComponent extends BaseComponent implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

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
    private View lockBtn;

    private boolean mIsDragging;

    private Animation mShowAnim;
    private Animation mHideAnim;

    public ControllerComponent(@NonNull Context context) {
        super(context);
        initView();
        this.setVisibility(GONE);

        mShowAnim = new AlphaAnimation(0f, 1f);
        mShowAnim.setDuration(300);
        mHideAnim = new AlphaAnimation(1f, 0f);
        mHideAnim.setDuration(300);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_component_controller;
    }

    private void initView() {
        topView = this.findViewById(R.id.fl_component_ctr_top);
        backView = this.findViewById(R.id.iv_component_ctr_back);
        titleTv = this.findViewById(R.id.tv_component_ctr_title);
        bottomView = this.findViewById(R.id.fl_component_ctr_bottom);
        curTimeTv = this.findViewById(R.id.tv_component_ctr_cur_time);
        totalTimeTv = this.findViewById(R.id.tv_component_ctr_total_time);
        playIv = this.findViewById(R.id.iv_component_ctr_play);
        fullscreenIv = this.findViewById(R.id.tv_component_ctr_fullscreen);
        seekBar = this.findViewById(R.id.tv_component_ctr_seekbar);
        progressBar = this.findViewById(R.id.pb_component_ctr_progress_bar);
        lockBtn = this.findViewById(R.id.iv_component_ctr_lock);

        lockBtn.setOnClickListener(this);
        backView.setOnClickListener(this);
        playIv.setOnClickListener(this);
        fullscreenIv.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onSingleTapConfirmed() {
        if (mControlWrapper != null)
            mControlWrapper.toggleShowState();
    }

    @Override
    public void onDoubleTap() {
        if (mControlWrapper != null && !mControlWrapper.isLocked())
            mControlWrapper.togglePlay();
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        super.onLockStateChanged(isLocked);
        lockBtn.setEnabled(true);
        if (isLocked) {
            lockBtn.setSelected(true);
            Toast.makeText(getContext(), R.string.player_ui_locked, Toast.LENGTH_SHORT).show();
        } else {
            lockBtn.setSelected(false);
            Toast.makeText(getContext(), R.string.player_ui_unlocked, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onVisibilityChanged(boolean isVisible) {
        super.onVisibilityChanged(isVisible);
        if (isVisible) {
            lockBtn.setVisibility(VISIBLE);
            lockBtn.startAnimation(mShowAnim);
        } else {
            lockBtn.setVisibility(GONE);
            lockBtn.startAnimation(mHideAnim);
        }

        if (mControlWrapper != null && !mControlWrapper.isLocked()) {
            controllerVisible(isVisible);
        }
    }

    private void controllerVisible(boolean isVisible) {
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
    public void onPlayStateChanged(@VideoPlayType int playState) {
        super.onPlayStateChanged(playState);

        if (playState == VideoPlayType.STATE_PREPARING) {
            titleTv.setText("");
            if (mControlWrapper != null) {
                if (!TextUtils.isEmpty(mControlWrapper.getDataSource().title))
                    titleTv.setText(mControlWrapper.getDataSource().title);
                mControlWrapper.show();
            }
        }

        switch (playState) {
            case VideoPlayType.STATE_IDLE:
            case VideoPlayType.STATE_COMPLETED:
                this.setVisibility(GONE);
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
                seekBar.setProgress(0);
                seekBar.setSecondaryProgress(0);
                break;
            case VideoPlayType.STATE_START_ABORT:
            case VideoPlayType.STATE_PREPARING:
            case VideoPlayType.STATE_PREPARED:
            case VideoPlayType.STATE_ERROR:
                this.setVisibility(GONE);
                break;
            case VideoPlayType.STATE_PLAYING:
                this.setVisibility(VISIBLE);
                playIv.setSelected(true);
                if (mControlWrapper != null)
                    mControlWrapper.startProgress();
                break;
            case VideoPlayType.STATE_PAUSED:
                playIv.setSelected(false);
                break;
        }
    }

    @Override
    public void onPlayerStateChanged(@VideoPlayerType int playerState) {
        switch (playerState) {
            case VideoPlayerType.PLAYER_NORMAL:
                fullscreenIv.setSelected(false);
                break;
            case VideoPlayerType.PLAYER_FULL_SCREEN:
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
        }

        if (totalTimeTv != null)
            totalTimeTv.setText(ComponentUtils.stringForTime(duration));
        if (curTimeTv != null)
            curTimeTv.setText(ComponentUtils.stringForTime(position));

    }

    @Override
    public void setBufferingProgress(int percent) {
        if (percent >= 95) { // 解决缓冲进度不能100%问题
            seekBar.setSecondaryProgress(seekBar.getMax());
            progressBar.setSecondaryProgress(progressBar.getMax());
        } else {
            seekBar.setSecondaryProgress(percent * 10);
            progressBar.setSecondaryProgress(percent * 10);
        }
    }

    private void toggleFullScreen() {
        if (mControlWrapper != null) {
            Activity activity = Utils.scanForActivity(getContext());
            mControlWrapper.toggleFullScreen(activity);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_component_ctr_back) {
            if (mControlWrapper != null && mControlWrapper.isFullScreen()) {
                toggleFullScreen();
            } else {
                Activity activity = Utils.scanForActivity(getContext());
                activity.finish();
            }
        } else if (v.getId() == R.id.iv_component_ctr_play) {
            if (mControlWrapper != null)
                mControlWrapper.togglePlay();
        } else if (v.getId() == R.id.tv_component_ctr_fullscreen) {
            toggleFullScreen();
        } else if (v.getId() == R.id.iv_component_ctr_lock) {
            if (mControlWrapper != null) {
                lockBtn.setEnabled(false);
                mControlWrapper.toggleLockState();
                controllerVisible(!mControlWrapper.isLocked());
            }
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        if (mControlWrapper == null) return;

        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * progress) / this.seekBar.getMax();
        if (curTimeTv != null)
            curTimeTv.setText(ComponentUtils.stringForTime((int) newPosition));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mControlWrapper == null) return;
        mIsDragging = true;
        mControlWrapper.stopProgress();
        mControlWrapper.cancelHideCountdown();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mControlWrapper == null) return;
        mIsDragging = false;
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * seekBar.getProgress()) / this.seekBar.getMax();
        mControlWrapper.seekTo((int) newPosition);
        mControlWrapper.startProgress();
        mControlWrapper.autoHideCountdown();
    }
}
