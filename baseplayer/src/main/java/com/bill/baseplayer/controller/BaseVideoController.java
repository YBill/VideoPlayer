package com.bill.baseplayer.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bill.baseplayer.config.VideoViewManager;
import com.bill.baseplayer.player.VideoView;
import com.bill.baseplayer.util.CutoutScreenUtil;
import com.bill.baseplayer.util.MLog;
import com.bill.baseplayer.util.Utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * author ywb
 * date 2021/11/24
 * desc
 */
public abstract class BaseVideoController extends FrameLayout implements
        IVideoController, OrientationHelper.OnOrientationChangeListener {

    // 播放器包装类，集合了MediaPlayerControl的api和IVideoController的api
    protected ControlWrapper mControlWrapper;

    protected Activity mActivity;

    protected boolean mIsShowing; // 控制器是否处于显示状态
    protected boolean mIsLocked; // 是否处于锁定状态
    private boolean mIsStartProgress; // 是否开始刷新进度
    private int mOrientation = 0;

    private boolean mEnableOrientation; // 是否开启根据屏幕方向进入/退出全屏
    protected OrientationHelper mOrientationHelper; // 屏幕方向监听辅助类

    private boolean mIsAdaptCutout; // 用户设置是否适配刘海屏
    private Boolean mHasCutout; // 是否有刘海
    private int mCutoutHeight; // 刘海的高度

    protected int mDefaultTimeout = 4000; // 播放视图隐藏超时

    //保存了所有的控制组件
    protected LinkedHashMap<IControlComponent, Boolean> mControlComponents = new LinkedHashMap<>();

    private Animation mShowAnim;
    private Animation mHideAnim;

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
        if (getLayoutId() != 0) {
            LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        }

        // 读取全局配置
        mOrientationHelper = new OrientationHelper(getContext().getApplicationContext());
        mEnableOrientation = VideoViewManager.getInstance().getConfig().mEnableOrientation;
        mIsAdaptCutout = VideoViewManager.getInstance().getConfig().mAdaptCutout;

        mActivity = Utils.scanForActivity(getContext());
    }

    /**
     * 设置控制器布局文件
     */
    protected abstract int getLayoutId();

    //////// System Start /////////

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkCutout();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mControlWrapper.isPlaying() && (mEnableOrientation || mControlWrapper.isFullScreen())) {
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
     * 添加控制组件，最后面添加的在最下面，合理组织添加顺序，可让ControlComponent位于不同的层级
     */
    public void addControlComponent(IControlComponent... component) {
        if (component == null) return;
        for (IControlComponent item : component) {
            addControlComponent(item, false);
        }
    }

    public void addControlComponent(IControlComponent component, boolean isDissociate) {
        mControlComponents.put(component, isDissociate);
        if (mControlWrapper != null) {
            component.attach(mControlWrapper);
        }
        View view = component.getView();
        if (view != null && !isDissociate) {
            addView(view, 0);
        }
    }

    /**
     * 移除控制组件
     */
    public void removeControlComponent(IControlComponent component) {
        removeView(component.getView());
        mControlComponents.remove(component);
    }

    /**
     * 移除所有控制组件
     */
    public void removeAllControlComponent() {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            removeView(next.getKey().getView());
        }
        mControlComponents.clear();
    }

    /**
     * 移除所有的游离控制组件
     * 关于游离控制组件的定义请看 {@link #addControlComponent(IControlComponent, boolean)} 关于 isDissociate 的解释
     */
    public void removeAllDissociateComponents() {
        Iterator<Map.Entry<IControlComponent, Boolean>> it = mControlComponents.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<IControlComponent, Boolean> next = it.next();
            if (next.getValue()) {
                it.remove();
            }
        }
    }

    /**
     * 设置播放视图自动隐藏超时
     */
    public void setDismissTimeout(int timeout) {
        if (timeout > 0) {
            mDefaultTimeout = timeout;
        }
    }

    /**
     * 设置是否适配刘海屏
     */
    public void setAdaptCutout(boolean adaptCutout) {
        mIsAdaptCutout = adaptCutout;
    }

    /**
     * 是否自动旋转， 默认不自动旋转
     */
    public void setEnableOrientation(boolean enableOrientation) {
        mEnableOrientation = enableOrientation;
    }

    //////// 向外部暴露的方法 End /////////

    //////// IVideoController Start /////////

    /**
     * 开始计时
     */
    @Override
    public void startFadeOut() {
        stopFadeOut();
        postDelayed(mFadeOutRunnable, mDefaultTimeout);
    }

    /**
     * 取消计时
     */
    @Override
    public void stopFadeOut() {
        removeCallbacks(mFadeOutRunnable);
    }

    @Override
    public void setLocked(boolean locked) {
        mIsLocked = locked;
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
            if (mShowAnim == null) {
                mShowAnim = new AlphaAnimation(0f, 1f);
                mShowAnim.setDuration(300);
            }
            handleVisibilityChanged(true, mShowAnim);
            startFadeOut();
            mIsShowing = true;
        }
    }

    /**
     * 隐藏播放视图
     */
    @Override
    public void hide() {
        if (mIsShowing) {
            stopFadeOut();
            if (mHideAnim == null) {
                mHideAnim = new AlphaAnimation(1f, 0f);
                mHideAnim.setDuration(300);
            }
            handleVisibilityChanged(false, mHideAnim);
            mIsShowing = false;
        }
    }

    /**
     * 是否有刘海屏
     */
    @Override
    public boolean hasCutout() {
        return mHasCutout != null && mHasCutout;
    }

    /**
     * 刘海的高度
     */
    @Override
    public int getCutoutHeight() {
        return mCutoutHeight;
    }

    //////// IVideoController End /////////

    //////// OrientationHelper#OnOrientationChangeListener Start /////////

    @CallSuper
    @Override
    public void onOrientationChanged(int orientation) {
        if (mActivity == null || mActivity.isFinishing()) return;

        //记录用户手机上一次放置的位置
        int lastOrientation = mOrientation;

        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            //手机平放时，检测不到有效的角度
            //重置为原始位置 -1
            mOrientation = -1;
            return;
        }

        if (orientation > 350 || orientation < 10) {
            int o = mActivity.getRequestedOrientation();
            //手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && lastOrientation == 0) return;
            if (mOrientation == 0) return;
            //0度，用户竖直拿着手机
            mOrientation = 0;
            onOrientationPortrait(mActivity);
        } else if (orientation > 80 && orientation < 100) {

            int o = mActivity.getRequestedOrientation();
            //手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 90) return;
            if (mOrientation == 90) return;
            //90度，用户右侧横屏拿着手机
            mOrientation = 90;
            onOrientationReverseLandscape(mActivity);
        } else if (orientation > 260 && orientation < 280) {
            int o = mActivity.getRequestedOrientation();
            //手动切换横竖屏
            if (o == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && lastOrientation == 270) return;
            if (mOrientation == 270) return;
            //270度，用户左侧横屏拿着手机
            mOrientation = 270;
            onOrientationLandscape(mActivity);
        }
    }

    //////// OrientationHelper#OnOrientationChangeListener Start /////////


    //////// 子类可接收父类状态 Start /////////

    /**
     * 子类中请使用此方法来进入全屏
     *
     * @return 是否成功进入全屏
     */
    protected boolean startFullScreen() {
        if (mActivity == null || mActivity.isFinishing()) return false;
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mControlWrapper.startFullScreen();
        return true;
    }

    /**
     * 子类中请使用此方法来退出全屏
     *
     * @return 是否成功退出全屏
     */
    protected boolean stopFullScreen() {
        if (mActivity == null || mActivity.isFinishing()) return false;
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mControlWrapper.stopFullScreen();
        return true;
    }

    /**
     * 播放和暂停
     */
    protected void togglePlay() {
        mControlWrapper.togglePlay();
    }

    /**
     * 横竖屏切换
     */
    protected void toggleFullScreen() {
        mControlWrapper.toggleFullScreen(mActivity);
    }

    /**
     * 子类重写此方法并在其中更新控制器在不同播放状态下的ui
     */
    @CallSuper
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
                mOrientationHelper.disable();
                mOrientation = 0;
                mIsLocked = false;
                mIsShowing = false;
                //由于游离组件是独立于控制器存在的，
                //所以在播放器release的时候需要移除
                removeAllDissociateComponents();
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                mIsLocked = false;
                mIsShowing = false;
                break;
            case VideoView.STATE_ERROR:
                mIsShowing = false;
                break;
        }
    }

    /**
     * 子类重写此方法并在其中更新控制器在不同播放器状态下的ui
     */
    @CallSuper
    protected void onPlayerStateChanged(int playerState) {
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                if (mEnableOrientation) {
                    mOrientationHelper.enable();
                } else {
                    mOrientationHelper.disable();
                }
                if (hasCutout()) {
                    CutoutScreenUtil.adaptCutoutAboveAndroidP(getContext(), false);
                }
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                // 在全屏时强制监听设备方向
                mOrientationHelper.enable();
                if (hasCutout()) {
                    CutoutScreenUtil.adaptCutoutAboveAndroidP(getContext(), true);
                }
                break;
            case VideoView.PLAYER_TINY_SCREEN:
                mOrientationHelper.disable();
                break;
        }
    }

    /**
     * 子类可重写此方法监听锁定状态发生改变，然后更新ui
     */
    protected void onLockStateChanged(boolean isLocked) {

    }

    /**
     * 刷新进度回调，子类可在此方法监听进度刷新，然后更新ui
     *
     * @param duration 视频总时长
     * @param position 视频当前时长
     */
    protected void setProgress(long duration, long position) {

    }

    /**
     * 子类重写此方法监听控制的显示和隐藏
     *
     * @param isVisible 是否可见
     * @param anim      显示/隐藏动画
     */
    protected void onVisibilityChanged(boolean isVisible, Animation anim) {

    }

    //////// 子类可接收父类状态 End /////////

    //////// 和VideoView关联方法 Start /////////

    /**
     * 重要：此方法用于将{@link VideoView} 和控制器绑定
     */
    @CallSuper
    public void setMediaPlayer(PlayerControl mediaPlayer) {
        mControlWrapper = new ControlWrapper(mediaPlayer, this);
        // 绑定ControlComponent和Controller
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.attach(mControlWrapper);
        }
        // 开始监听设备方向
        mOrientationHelper.setOnOrientationChangeListener(this);
    }


    /**
     * {@link VideoView}调用此方法向控制器设置播放状态
     */
    @CallSuper
    public void setPlayState(int playState) {
        handlePlayStateChanged(playState);
    }

    /**
     * {@link VideoView}调用此方法向控制器设置播放器状态
     */
    @CallSuper
    public void setPlayerState(final int playerState) {
        handlePlayerStateChanged(playerState);
    }

    /**
     * 显示移动网络播放提示
     *
     * @return 返回显示移动网络播放提示的条件，false:不显示, true显示
     * 此处默认根据手机网络类型来决定是否显示，开发者可以重写相关逻辑
     */
    public boolean showNetWarning() {
        return Utils.isMobileNet(getContext())
                && !VideoViewManager.getInstance().playOnMobileNetwork();
    }

    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return false;
    }

    //////// 和VideoView关联方法 End /////////

    /**
     * 检查是否需要适配刘海
     */
    private void checkCutout() {
        if (!mIsAdaptCutout) return;
        if (mActivity != null && mHasCutout == null) {
            mHasCutout = CutoutScreenUtil.allowDisplayToCutout(mActivity);
            if (mHasCutout) {
                //竖屏下的状态栏高度可认为是刘海的高度
                mCutoutHeight = (int) Utils.getStatusBarHeightPortrait(mActivity);
            }
        }
        MLog.d("hasCutout: " + mHasCutout + " cutout height: " + mCutoutHeight);
    }

    /**
     * 隐藏播放视图Runnable
     */
    protected final Runnable mFadeOutRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private void handleLockStateChanged(boolean isLocked) {
        for (Map.Entry<IControlComponent, Boolean> next
                : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.onLockStateChanged(isLocked);
        }
        onLockStateChanged(isLocked);
    }

    /**
     * 刷新进度Runnable
     */
    protected Runnable mShowProgressRunnable = new Runnable() {
        @Override
        public void run() {
            long pos = setProgress();
            if (mControlWrapper.isPlaying()) {
                float speed = mControlWrapper.getSpeed() == 0 ? 1f : mControlWrapper.getSpeed();
                postDelayed(this, (long) ((1000 - pos % 1000) / speed));
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
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.setProgress(duration, position);
        }
        setProgress(duration, position);
    }

    private void handleVisibilityChanged(boolean isVisible, Animation anim) {
        // 没锁住时才向ControlComponent下发此事件
        if (!mIsLocked) {
            for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
                IControlComponent component = next.getKey();
                component.onVisibilityChanged(isVisible, anim);
            }
        }
        onVisibilityChanged(isVisible, anim);
    }

    private void handlePlayStateChanged(int playState) {
        for (Map.Entry<IControlComponent, Boolean> next : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.onPlayStateChanged(playState);
        }
        onPlayStateChanged(playState);
    }

    private void handlePlayerStateChanged(int playerState) {
        for (Map.Entry<IControlComponent, Boolean> next
                : mControlComponents.entrySet()) {
            IControlComponent component = next.getKey();
            component.onPlayerStateChanged(playerState);
        }
        onPlayerStateChanged(playerState);
    }

    /**
     * 竖屏
     */
    protected void onOrientationPortrait(Activity activity) {
        //屏幕锁定的情况
        if (mIsLocked) return;
        //没有开启设备方向监听的情况
        if (!mEnableOrientation) return;

        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mControlWrapper.stopFullScreen();
    }

    /**
     * 横屏
     */
    protected void onOrientationLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (mControlWrapper.isFullScreen()) {
            handlePlayerStateChanged(VideoView.PLAYER_FULL_SCREEN);
        } else {
            mControlWrapper.startFullScreen();
        }
    }

    /**
     * 反向横屏
     */
    protected void onOrientationReverseLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        if (mControlWrapper.isFullScreen()) {
            handlePlayerStateChanged(VideoView.PLAYER_FULL_SCREEN);
        } else {
            mControlWrapper.startFullScreen();
        }
    }


}
