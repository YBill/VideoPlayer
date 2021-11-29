package com.bill.baseplayer.controller;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bill.baseplayer.player.VideoView;
import com.bill.baseplayer.util.Utils;

import java.util.Map;

/**
 * author ywb
 * date 2021/11/29
 * desc 包含手势功能的控制器
 * 包括：滑动调整进度、音量、亮度 和 双击播放/暂停
 */
public abstract class GestureVideoController extends BaseVideoController implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;

    private boolean mEnabledGesture = true; // 是否开启手势功能
    private boolean mCanChangePosByScroll = true; // 是否可以滑动调整进度
    private boolean mEnabledDoubleTapTogglePlay = true; // 双击控制播放暂停

    private boolean mFirstTouch; // 第一次触摸
    private int mCurPlayState; // 当前的播放状态
    protected boolean mCanScroll = false; // 是否支持滑动

    private int mStreamVolume; // 音量
    private float mBrightness; // 亮度
    private long mSeekPosition; // 进度
    private boolean mChangePosition; // 改变进度
    private boolean mChangeBrightness; // 改变亮度
    private boolean mChangeVolume; // 改变音量

    public GestureVideoController(@NonNull Context context) {
        super(context);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        // 默认小屏模式下不可以滑动手势，可以重写setPlayerState设置
        if (playerState == VideoView.PLAYER_TINY_SCREEN) {
            mCanScroll = false;
        } else {
            mCanScroll = true;
        }
    }

    @Override
    public void setPlayState(int playState) {
        super.setPlayState(playState);
        mCurPlayState = playState;
    }

    //////// 向外部暴露的方法 Start /////////

    /**
     * 设置是否可以滑动调节进度，默认可以
     */
    public void setCanChangePosByScroll(boolean canChangePosByScroll) {
        mCanChangePosByScroll = canChangePosByScroll;
    }

    /**
     * 是否开启手势控制，默认开启
     * 关闭之后，手势调节进度，音量，亮度功能将关闭
     */
    public void setGestureEnabled(boolean gestureEnabled) {
        mEnabledGesture = gestureEnabled;
    }

    /**
     * 是否开启双击播放/暂停，默认开启
     */
    public void setDoubleTapTogglePlayEnabled(boolean enabledDoubleTapTogglePlay) {
        mEnabledDoubleTapTogglePlay = enabledDoubleTapTogglePlay;
    }

    //////// 向外部暴露的方法 End /////////

    //////// View.OnTouchListener Start /////////

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    //////// View.OnTouchListener End /////////

    //////// GestureDetector.OnGestureListener Start /////////

    /**
     * 手指按下的瞬间
     */
    @Override
    public boolean onDown(MotionEvent e) {
        if (!isInPlayState() // 不处于播放状态
                || !mEnabledGesture // 关闭了手势
                || Utils.isEdge(getContext(), e)) // 处于屏幕边沿
            return true;
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mActivity == null) {
            mBrightness = 0;
        } else {
            mBrightness = mActivity.getWindow().getAttributes().screenBrightness;
        }
        mFirstTouch = true;
        mChangePosition = false;
        mChangeBrightness = false;
        mChangeVolume = false;
        return true;
    }

    /**
     * 在屏幕上滑动
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isInPlayState() // 不处于播放状态
                || !mEnabledGesture // 关闭了手势
                || !mCanScroll // 关闭了滑动手势
                || isLocked() // 锁住了屏幕
                || Utils.isEdge(getContext(), e1)) //处于屏幕边沿
            return true;
        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();
        if (mFirstTouch) {
            mFirstTouch = false;
            mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
            if (mChangePosition) {
                // 根据用户设置是否可以滑动调节进度来决定最终是否可以滑动调节进度
                mChangePosition = mCanChangePosByScroll;
            } else {
                // 半屏宽度
                int halfScreen = Utils.getScreenWidth(getContext(), true) / 2;
                if (e2.getX() > halfScreen) {
                    mChangeVolume = true;
                } else {
                    mChangeBrightness = true;
                }
            }

            if (mChangePosition || mChangeBrightness || mChangeVolume) {
                for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
                    IControlComponent component = next.getKey();
                    if (component instanceof IGestureComponent) {
                        ((IGestureComponent) component).onStartSlide();
                    }
                }
            }
        }
        if (mChangePosition) {
            slideToChangePosition(deltaX);
        } else if (mChangeBrightness) {
            slideToChangeBrightness(deltaY);
        } else if (mChangeVolume) {
            slideToChangeVolume(deltaY);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    //////// GestureDetector.OnGestureListener End /////////

    //////// GestureDetector.OnDoubleTapListener Start /////////

    /**
     * 单击
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (isInPlayState()) {
            mControlWrapper.toggleShowState();
        }
        return true;
    }

    /**
     * 双击
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (mEnabledDoubleTapTogglePlay && !isLocked() && isInPlayState()) togglePlay();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }


    //////// GestureDetector.OnDoubleTapListener End /////////

    //////// System Start /////////

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //滑动结束时事件处理
        if (!mGestureDetector.onTouchEvent(event)) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    stopSlide();
                    if (mSeekPosition > 0) {
                        mControlWrapper.seekTo(mSeekPosition);
                        mSeekPosition = 0;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    stopSlide();
                    mSeekPosition = 0;
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    //////// System End /////////

    private void stopSlide() {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onStopSlide();
            }
        }
    }

    private boolean isInPlayState() {
        return mControlWrapper != null
                && mCurPlayState != VideoView.STATE_ERROR
                && mCurPlayState != VideoView.STATE_IDLE
                && mCurPlayState != VideoView.STATE_PREPARING
                && mCurPlayState != VideoView.STATE_PREPARED
                && mCurPlayState != VideoView.STATE_START_ABORT
                && mCurPlayState != VideoView.STATE_PLAYBACK_COMPLETED;
    }

    // 调整进度
    protected void slideToChangePosition(float deltaX) {
        deltaX = -deltaX;
        int width = getMeasuredWidth();
        long duration = mControlWrapper.getDuration();
        long currentPosition = mControlWrapper.getCurrentPosition();
        long position = (long) (deltaX / width * 120000 + currentPosition);
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onPositionChange(position, currentPosition, duration);
            }
        }
        mSeekPosition = position;
    }

    // 调整亮度
    protected void slideToChangeBrightness(float deltaY) {
        if (mActivity == null) return;
        Window window = mActivity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        int height = getMeasuredHeight();
        if (mBrightness == -1.0f) mBrightness = 0.5f;
        float brightness = deltaY * 2 / height + mBrightness;
        if (brightness < 0) {
            brightness = 0f;
        }
        if (brightness > 1.0f) brightness = 1.0f;
        int percent = (int) (brightness * 100);
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onBrightnessChange(percent);
            }
        }
    }

    // 调整音量
    protected void slideToChangeVolume(float deltaY) {
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mStreamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) index = 0;
        int percent = (int) (index / streamMaxVolume * 100);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onVolumeChange(percent);
            }
        }
    }
}
