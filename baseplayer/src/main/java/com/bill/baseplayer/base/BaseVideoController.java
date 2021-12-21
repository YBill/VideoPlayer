package com.bill.baseplayer.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.config.VideoPlayerType;
import com.bill.baseplayer.config.VideoViewManager;
import com.bill.baseplayer.controller.ControlWrapper;
import com.bill.baseplayer.controller.IControlComponent;
import com.bill.baseplayer.controller.IVideoController;
import com.bill.baseplayer.controller.OrientationHelper;
import com.bill.baseplayer.controller.PlayerControl;
import com.bill.baseplayer.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * author ywb
 * date 2021/11/24
 * desc 控制器基类
 */
public class BaseVideoController extends FrameLayout implements
        IVideoController, OrientationHelper.OnOrientationChangeListener, GestureDetector.OnDoubleTapListener {

    // 播放器包装类，集合了PlayerControl的api和IVideoController的api
    protected ControlWrapper mControlWrapper;

    protected Activity mActivity; // 当前Activity

    private boolean mIsShowing; // 控制器是否处于显示状态
    private boolean mIsLocked; // 是否处于锁定状态
    private boolean mIsStartProgress; // 是否开始刷新进度

    private boolean mEnableOrientation; // 是否开启根据屏幕方向进入/退出全屏，默认false
    private OrientationHelper mOrientationHelper; // 屏幕方向监听辅助类
    private int mOrientation;

    private int mCurPlayState; // 当前的播放状态

    private int mAutoHideCountdown = 5000; // 视图自动隐藏倒计时

    // 所有的组件
    protected ArrayList<IControlComponent> mControlComponents = new ArrayList<>();

    public BaseVideoController(@NonNull Context context) {
        this(context, null);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseVideoController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        // 读取全局配置
        mOrientationHelper = new OrientationHelper(getContext().getApplicationContext());
        mEnableOrientation = VideoViewManager.getInstance().getConfig().mEnableOrientation;

        mActivity = Utils.scanForActivity(getContext());

        // 开始监听设备方向
        mOrientationHelper.setOnOrientationChangeListener(this);
    }

    protected boolean isInPlayState() {
        return mControlWrapper != null
                && mCurPlayState != VideoPlayType.STATE_ERROR
                && mCurPlayState != VideoPlayType.STATE_IDLE
                && mCurPlayState != VideoPlayType.STATE_PREPARING
                && mCurPlayState != VideoPlayType.STATE_PREPARED
                && mCurPlayState != VideoPlayType.STATE_START_ABORT
                && mCurPlayState != VideoPlayType.STATE_COMPLETED;
    }

    //////// System Start /////////

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!mEnableOrientation)
            return;
        if (!mIsLocked && isInPlayState()) {
            if (hasWindowFocus) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOrientationHelper.enable();
                    }
                }, 800);
            } else {
                mOrientationHelper.disable();
            }
        }
    }

    //////// System End /////////


    //////// 向外部暴露的方法 Start /////////

    /**
     * 添加控制组件，最后面添加的在最上面，合理组织添加顺序，可让ControlComponent位于不同的层级
     */
    public void addControlComponent(IControlComponent... components) {
        if (components == null) return;
        for (IControlComponent component : components) {
            if (component == null) continue;
            mControlComponents.remove(component);
            mControlComponents.add(component);
            if (mControlWrapper != null) {
                component.attach(mControlWrapper);
            }
            if (!component.isDissociate()) {
                View view = component.getView();
                if (view != null) {
                    addView(view);
                }
            }

        }
    }

    /**
     * 获取控制器中的某个组件
     *
     * @param key 根据 {@link IControlComponent#getKey()} 获取
     */
    public IControlComponent getControlComponent(String key) {
        if (TextUtils.isEmpty(key))
            return null;
        for (IControlComponent component : mControlComponents) {
            if (component == null) continue;
            if (key.equals(component.getKey()))
                return component;
        }
        return null;
    }

    /**
     * 移除控制组件
     */
    public void removeControlComponent(IControlComponent component) {
        if (component == null) return;
        if (!component.isDissociate())
            removeView(component.getView());
        mControlComponents.remove(component);
    }

    /**
     * 移除所有控制组件
     */
    public void clearControlComponents() {
        for (IControlComponent component : mControlComponents) {
            if (component == null) continue;
            if (!component.isDissociate())
                removeView(component.getView());
        }
        mControlComponents.clear();
    }

    /**
     * 移除所有游离组件
     */
    public void clearDissociateComponents() {
        Iterator<IControlComponent> iterator = mControlComponents.iterator();
        while (iterator.hasNext()) {
            IControlComponent component = iterator.next();
            if (component.isDissociate())
                iterator.remove();
        }
    }

    /**
     * 设置自动隐藏时间，默认5s
     */
    public void setAutoHideCountdown(int autoHideCountdown) {
        if (autoHideCountdown > 0) {
            mAutoHideCountdown = autoHideCountdown;
        }
    }

    /**
     * 是否自动旋转，默认不自动旋转
     */
    public void setEnableOrientation(boolean enableOrientation) {
        mEnableOrientation = enableOrientation;
    }

    //////// 向外部暴露的方法 End /////////

    //////// IVideoController Start /////////

    @Override
    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * 显示播放视图
     */
    @Override
    public void show() {
        if (!mIsShowing) {
            handleVisibilityChanged(true);
            autoHideCountdown();
            mIsShowing = true;
        }
    }

    /**
     * 隐藏播放视图
     */
    @Override
    public void hide() {
        if (mIsShowing) {
            cancelHideCountdown();
            handleVisibilityChanged(false);
            mIsShowing = false;
        }
    }

    /**
     * 开始控制视图自动隐藏倒计时
     */
    @Override
    public void autoHideCountdown() {
        cancelHideCountdown();
        postDelayed(mAutoHideRunnable, mAutoHideCountdown);
    }

    /**
     * 取消控制视图自动隐藏倒计时
     */
    @Override
    public void cancelHideCountdown() {
        removeCallbacks(mAutoHideRunnable);
    }

    @Override
    public void setLocked(boolean locked) {
        mIsLocked = locked;
        orientationSensing();
        handleLockStateChanged(locked);
    }

    @Override
    public boolean isLocked() {
        return mIsLocked;
    }

    /**
     * 开始刷新进度，注意：需在STATE_PLAYING时调用才会开始刷新进度
     */
    @Override
    public void startProgress() {
        if (mIsStartProgress) return;
        post(mShowProgressRunnable);
        mIsStartProgress = true;
    }

    /**
     * 停止刷新进度
     */
    @Override
    public void stopProgress() {
        if (!mIsStartProgress) return;
        removeCallbacks(mShowProgressRunnable);
        mIsStartProgress = false;
    }

    //////// IVideoController End /////////

    //////// OrientationHelper#OnOrientationChangeListener Start /////////

    @CallSuper
    @Override
    public void onOrientationChanged(int orientation) {
        if (mActivity == null || mActivity.isFinishing()) return;

        // 记录用户手机上一次放置的位置
        int lastOrientation = mOrientation;

        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            // 手机平放时，检测不到有效的角度
            // 重置为原始位置 -1
            mOrientation = -1;
            return;
        }

        if (orientation > 350 || orientation < 10) {
            int o = mActivity.getRequestedOrientation();
            // 手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && lastOrientation == 0) return;
            if (mOrientation == 0) return;
            // 0度，用户竖直拿着手机
            mOrientation = 0;
            onOrientationPortrait(mActivity);
        } else if (orientation > 80 && orientation < 100) {

            int o = mActivity.getRequestedOrientation();
            // 手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 90) return;
            if (mOrientation == 90) return;
            // 90度，用户右侧横屏拿着手机
            mOrientation = 90;
            onOrientationReverseLandscape(mActivity);
        } else if (orientation > 260 && orientation < 280) {
            int o = mActivity.getRequestedOrientation();
            // 手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 270) return;
            if (mOrientation == 270) return;
            // 270度，用户左侧横屏拿着手机
            mOrientation = 270;
            onOrientationLandscape(mActivity);
        }
    }

    //////// OrientationHelper#OnOrientationChangeListener Start /////////

    //////// GestureDetector.OnDoubleTapListener Start /////////

    /**
     * 单击
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (isInPlayState())
            handleSingleTap();
        return true;
    }

    /**
     * 双击
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (isInPlayState())
            handleDoubleTap();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    //////// GestureDetector.OnDoubleTapListener End /////////


    //////// 和VideoView关联方法 Start /////////

    /**
     * 重要：此方法用于将{@link VideoView} 和 {@link BaseVideoController} 绑定
     */
    @CallSuper
    protected void setMediaPlayer(PlayerControl mediaPlayer) {
        mControlWrapper = new ControlWrapper(mediaPlayer, this);
        // 绑定ControlComponent和Controller
        for (IControlComponent component : mControlComponents) {
            component.attach(mControlWrapper);
        }
    }

    /**
     * {@link VideoView}调用此方法向控制器更新缓存进度
     */
    @CallSuper
    protected void onBufferingUpdate(int percent) {
        for (IControlComponent component : mControlComponents) {
            component.setBufferingProgress(percent);
        }
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放状态
     */
    @CallSuper
    protected void setPlayState(@VideoPlayType int playState) {
        handlePlayStateChanged(playState);
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放器状态
     */
    @CallSuper
    protected void setPlayerState(@VideoPlayerType int playerState) {
        handlePlayerStateChanged(playerState);
    }

    //////// 和VideoView关联方法 End /////////

    /**
     * 隐藏播放视图Runnable
     */
    private final Runnable mAutoHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private void handleLockStateChanged(boolean isLocked) {
        for (IControlComponent component : mControlComponents) {
            component.onLockStateChanged(isLocked);
        }
    }

    /**
     * 刷新进度Runnable
     */
    private final Runnable mShowProgressRunnable = new Runnable() {
        @Override
        public void run() {
            long curPos = setProgress();
            if (mControlWrapper.isPlaying()) {
                float speed = mControlWrapper.getSpeed() == 0 ? 1f : mControlWrapper.getSpeed();
                postDelayed(this, (long) ((1000 - curPos % 1000) / speed));
            } else {
                mIsStartProgress = false;
            }
        }
    };

    private long setProgress() {
        long position = mControlWrapper.getCurrentPosition();
        long duration = mControlWrapper.getDuration();
        handleSetProgress(duration, position);
        return position;
    }

    private void handleSetProgress(long duration, long position) {
        for (IControlComponent component : mControlComponents) {
            component.setProgress(duration, position);
        }
    }

    private void handleVisibilityChanged(boolean isVisible) {
        for (IControlComponent component : mControlComponents) {
            component.onVisibilityChanged(isVisible);
        }
    }

    private void handlePlayStateChanged(@VideoPlayType int playState) {
        onPlayStateChanged(playState);
        for (IControlComponent component : mControlComponents) {
            component.onPlayStateChanged(playState);
        }
    }

    private void handlePlayerStateChanged(@VideoPlayerType int playerState) {
        onPlayerStateChanged(playerState);
        for (IControlComponent component : mControlComponents) {
            component.onPlayerStateChanged(playerState);
        }
    }

    private void handleSingleTap() {
        for (IControlComponent component : mControlComponents) {
            component.onSingleTapConfirmed();
        }
    }

    private void handleDoubleTap() {
        for (IControlComponent component : mControlComponents) {
            component.onDoubleTap();
        }
    }

    /**
     * 处理播放状态
     */
    private void onPlayStateChanged(int playState) {
        mCurPlayState = playState;
        switch (playState) {
            case VideoPlayType.STATE_IDLE:
                mOrientationHelper.disable();
                mOrientation = 0;
                mIsLocked = false;
                mIsShowing = false;
                //由于游离组件是独立于控制器存在的，
                //所以在播放器release的时候需要移除
                clearDissociateComponents();
                break;
            case VideoPlayType.STATE_COMPLETED:
                mIsLocked = false;
                mIsShowing = false;
                break;
            case VideoPlayType.STATE_ERROR:
                mIsShowing = false;
                break;
        }
    }

    /**
     * 处理播放器状态
     */
    private void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoPlayerType.PLAYER_NORMAL:
                orientationSensing();
                break;
            case VideoPlayerType.PLAYER_FULL_SCREEN:
                orientationSensing();
                break;
            case VideoPlayerType.PLAYER_TINY_SCREEN:
                if (mEnableOrientation)
                    mOrientationHelper.disable();
                break;
        }
    }

    /**
     * 竖屏
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private void onOrientationPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mControlWrapper.exitFullScreen();
    }

    /**
     * 横屏
     */
    private void onOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (mControlWrapper.isFullScreen()) {
            handlePlayerStateChanged(VideoPlayerType.PLAYER_FULL_SCREEN);
        } else {
            mControlWrapper.enterFullScreen();
        }
    }

    /**
     * 反向横屏
     */
    private void onOrientationReverseLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        if (mControlWrapper.isFullScreen()) {
            handlePlayerStateChanged(VideoPlayerType.PLAYER_FULL_SCREEN);
        } else {
            mControlWrapper.enterFullScreen();
        }
    }

    /**
     * 处理方向感应
     */
    private void orientationSensing() {
        if (!mEnableOrientation)
            return;
        if (mIsLocked) {
            mOrientationHelper.disable();
        } else {
            mOrientationHelper.enable();
        }
    }

}
