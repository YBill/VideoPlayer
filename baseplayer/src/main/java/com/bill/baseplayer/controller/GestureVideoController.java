package com.bill.baseplayer.controller;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bill.baseplayer.base.BaseVideoController;
import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.util.Utils;

/**
 * author ywb
 * date 2021/11/29
 * desc 包含手势功能的控制器
 * 包括：滑动调整进度、音量、亮度
 */
public class GestureVideoController extends BaseVideoController implements
        GestureDetector.OnGestureListener, View.OnTouchListener {

    private GestureDetector mGestureDetector;
    private AudioManager mAudioManager;

    private boolean mEnabledGesture = false; // 是否开启手势功能,默认不开启手势功能

    private boolean mFirstTouch; // 第一次触摸

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

    @CallSuper
    @Override
    protected void initView() {
        super.initView();
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);
    }

    //////// 向外部暴露的方法 Start /////////

    /**
     * 是否开启手势控制，默认关闭
     * 关闭之后，手势调节和点击功能将关闭
     */
    public void setGestureEnabled(boolean gestureEnabled) {
        mEnabledGesture = gestureEnabled;
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
                || isLocked() // 锁住了屏幕
                || Utils.isEdge(getContext(), e)) // 处于屏幕边沿
            return true;
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Activity activity = mActivity;
        if (activity == null) {
            mBrightness = 0;
        } else {
            mBrightness = activity.getWindow().getAttributes().screenBrightness;
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
                || isLocked() // 锁住了屏幕
                || Utils.isEdge(getContext(), e1)) //处于屏幕边沿
            return true;
        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();
        if (mFirstTouch) {
            mFirstTouch = false;
            mChangePosition = Math.abs(distanceX) >= Math.abs(distanceY);
            if (!mChangePosition) {
                // 半屏宽度
                int halfScreen = Utils.getScreenWidth(getContext(), true) / 2;
                if (e2.getX() > halfScreen) {
                    mChangeVolume = true;
                } else {
                    mChangeBrightness = true;
                }
            }

            for (IControlComponent component : mControlComponents) {
                if (component instanceof IGestureComponent) {
                    ((IGestureComponent) component).onStartSlide();
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

    //////// System Start /////////

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //滑动结束时事件处理
        if (mEnabledGesture
                && !isLocked()
                && !mGestureDetector.onTouchEvent(event)) {
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
        for (IControlComponent component : mControlComponents) {
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onStopSlide();
            }
        }
    }

    // 调整进度
    private void slideToChangePosition(float deltaX) {
        deltaX = -deltaX;
        int width = getMeasuredWidth();
        long duration = mControlWrapper.getDuration();
        long currentPosition = mControlWrapper.getCurrentPosition();
        long position = (long) (deltaX / width * 120000 + currentPosition);
        if (position > duration) position = duration;
        if (position < 0) position = 0;
        for (IControlComponent component : mControlComponents) {
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onPositionChange(position, currentPosition, duration);
            }
        }
        mSeekPosition = position;
    }

    // 调整亮度
    private void slideToChangeBrightness(float deltaY) {
        Activity activity = mActivity;
        if (activity == null) return;
        Window window = activity.getWindow();
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
        for (IControlComponent component : mControlComponents) {
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onBrightnessChange(percent);
            }
        }
    }

    // 调整音量
    private void slideToChangeVolume(float deltaY) {
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int height = getMeasuredHeight();
        float deltaV = deltaY * 2 / height * streamMaxVolume;
        float index = mStreamVolume + deltaV;
        if (index > streamMaxVolume) index = streamMaxVolume;
        if (index < 0) index = 0;
        int percent = (int) (index / streamMaxVolume * 100);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) index, 0);
        for (IControlComponent component : mControlComponents) {
            if (component instanceof IGestureComponent) {
                ((IGestureComponent) component).onVolumeChange(percent);
            }
        }
    }
}
