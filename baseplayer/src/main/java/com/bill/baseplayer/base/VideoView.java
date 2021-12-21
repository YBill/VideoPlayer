package com.bill.baseplayer.base;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bill.baseplayer.config.AspectRatioType;
import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.config.VideoPlayerType;
import com.bill.baseplayer.config.VideoViewConfig;
import com.bill.baseplayer.config.VideoViewManager;
import com.bill.baseplayer.controller.OnVideoStateChangeListener;
import com.bill.baseplayer.controller.PlayerControl;
import com.bill.baseplayer.player.AbstractPlayer;
import com.bill.baseplayer.player.AudioFocusHelper;
import com.bill.baseplayer.player.DataSource;
import com.bill.baseplayer.player.IProgressManager;
import com.bill.baseplayer.render.IRenderView;
import com.bill.baseplayer.util.CreateClsFactory;
import com.bill.baseplayer.util.MLog;
import com.bill.baseplayer.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * author ywb
 * date 2021/11/26
 * desc
 */
public class VideoView extends FrameLayout implements PlayerControl, AbstractPlayer.PlayerEventListener {

    private @VideoPlayType
    int mCurrentPlayState = VideoPlayType.STATE_IDLE; // 当前的播放状态
    private @AspectRatioType
    int mScreenScaleType; // 视频比例

    private AbstractPlayer mMediaPlayer; // 解码器
    private CreateClsFactory<AbstractPlayer> mPlayerFactory; // 用于实例化解码器

    private IRenderView mRenderView; // 渲染器
    private CreateClsFactory<IRenderView> mRenderViewFactory; // 用于实例化渲染器

    private BaseVideoController mVideoController; // 控制器

    private FrameLayout mPlayerContainer; // 播放器总容器

    private DataSource mDataSource; // data source

    private List<OnVideoStateChangeListener> mOnStateChangeListeners; // 保存了所有监听器

    /**
     * 监听系统中音频焦点改变，见{@link #setEnableAudioFocus(boolean)}
     */
    private boolean mEnableAudioFocus;
    private AudioFocusHelper mAudioFocusHelper;

    private IProgressManager mProgressManager; // 进度管理器，设置之后播放器会记录播放进度，以便下次播放恢复进度

    private boolean mIsLooping; // 是否循环播放
    private boolean mIsMute; // 是否静音

    private final int[] mVideoSize = {0, 0}; // 视频宽高
    private int[] mTinyScreenSize = {0, 0}; // 小窗宽高

    private boolean mIsFullScreen; // 是否处于全屏状态
    private boolean mIsTinyScreen; // 是否处于小窗状态

    private long mCurrentPosition; // 当前正在播放视频的位置

    public VideoView(@NonNull Context context) {
        this(context, null);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 读取全局配置
        VideoViewConfig config = VideoViewManager.getInstance().getConfig();
        mEnableAudioFocus = config.mEnableAudioFocus;
        mProgressManager = config.mProgressManager;
        mScreenScaleType = config.mScreenScaleType;
        mPlayerFactory = config.mPlayerFactory;
        mRenderViewFactory = config.mRenderViewFactory;

        initView();
    }

    private void initView() {
        mPlayerContainer = new FrameLayout(getContext());
        setPlayerBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);
    }

    /**
     * 是否处于播放状态
     */
    private boolean isInPlayState() {
        return mMediaPlayer != null
                && mCurrentPlayState != VideoPlayType.STATE_ERROR
                && mCurrentPlayState != VideoPlayType.STATE_IDLE
                && mCurrentPlayState != VideoPlayType.STATE_PREPARING
                && mCurrentPlayState != VideoPlayType.STATE_START_ABORT
                && mCurrentPlayState != VideoPlayType.STATE_COMPLETED;
    }

    /**
     * 是否处于初始状态
     */
    private boolean isInIdleState() {
        return mCurrentPlayState == VideoPlayType.STATE_IDLE;
    }

    /**
     * 播放中止状态
     */
    private boolean isInStartAbortState() {
        return mCurrentPlayState == VideoPlayType.STATE_START_ABORT;
    }

    /**
     * 是否显示移动网络提示，可在Controller中配置
     */
    private boolean showNetWarning() {
        // 播放本地数据源时不检测网络
        if (isLocalDataSource()) {
            MLog.d("play local data");
            return false;
        }
        return Utils.isMobileNet(getContext()) && !VideoViewManager.getInstance().isPlayOnMobileNetwork();
    }

    /**
     * 判断是否为本地数据源，包括 本地文件、Asset、raw
     */
    private boolean isLocalDataSource() {
        if (mDataSource == null) return false;
        if (mDataSource.mRawId != 0) {
            return true;
        } else if (!TextUtils.isEmpty(mDataSource.mAssetsPath)) {
            return true;
        } else if (!TextUtils.isEmpty(mDataSource.mUrl)) {
            Uri uri = Uri.parse(mDataSource.mUrl);
            return ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())
                    || ContentResolver.SCHEME_FILE.equals(uri.getScheme())
                    || "rawresource".equals(uri.getScheme());
        }
        return false;
    }

    /**
     * 设置播放器状态，并向Controller通知播放器状态
     */
    private void setPlayerState(@VideoPlayerType int playerState) {
        if (mVideoController != null) {
            mVideoController.setPlayerState(playerState);
        }
        if (mOnStateChangeListeners != null) {
            for (OnVideoStateChangeListener l : mOnStateChangeListeners) {
                if (l != null) {
                    l.onPlayerStateChanged(playerState);
                }
            }
        }
    }

    /**
     * 设置播放状态，并向Controller通知播放状态，用于控制Controller的ui展示
     */
    private void setPlayState(@VideoPlayType int playState) {
        mCurrentPlayState = playState;
        if (mVideoController != null) {
            mVideoController.setPlayState(playState);
        }
        if (mOnStateChangeListeners != null) {
            for (OnVideoStateChangeListener l : mOnStateChangeListeners) {
                if (l != null) {
                    l.onPlayStateChanged(playState);
                }
            }
        }
    }

    /**
     * 第一次播放
     *
     * @return 是否成功开始播放
     */
    private boolean startPlay() {
        // 如果要显示移动网络提示则不继续播放
        if (showNetWarning()) {
            // 中止播放
            setPlayState(VideoPlayType.STATE_START_ABORT);
            return false;
        }
        // 监听音频焦点改变，初始化mAudioFocusHelper
        if (mEnableAudioFocus) {
            mAudioFocusHelper = new AudioFocusHelper(this);
        }
        // 读取播放进度
        if (mProgressManager != null && mDataSource != null) {
            mCurrentPosition = mProgressManager.getProgress(mDataSource.mUrl);
        }
        initPlayer();
        initRenderView();
        startPrepare(false);
        return true;
    }

    /**
     * 初始化播放器
     */
    private void initPlayer() {
        mMediaPlayer = mPlayerFactory.create(getContext());
        mMediaPlayer.setPlayerEventListener(this);
        mMediaPlayer.initPlayer();
        setOptions();
    }

    /**
     * 初始化视频渲染View
     */
    private void initRenderView() {
        if (mRenderView != null) {
            mPlayerContainer.removeView(mRenderView.getView());
            mRenderView.release();
        }
        mRenderView = mRenderViewFactory.create(getContext());
        if (mRenderView != null) {
            mRenderView.attachToPlayer(mMediaPlayer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER);
            mPlayerContainer.addView(mRenderView.getView(), 0, params);
        }
    }

    /**
     * 开始准备播放（直接播放）
     */
    private void startPrepare(boolean reset) {
        if (reset) {
            mMediaPlayer.reset();
            // 重新设置option，MediaPlayer reset之后，option会失效
            setOptions();
        }
        if (prepareDataSource()) {
            mMediaPlayer.prepareAsync();
            setPlayState(VideoPlayType.STATE_PREPARING);
            setPlayerState(isFullScreen() ? VideoPlayerType.PLAYER_FULL_SCREEN :
                    isTinyScreen() ? VideoPlayerType.PLAYER_TINY_SCREEN : VideoPlayerType.PLAYER_NORMAL);
        }
    }

    /**
     * 设置播放数据
     *
     * @return 播放数据是否设置成功
     */
    private boolean prepareDataSource() {
        if (mDataSource == null) return false;
        mMediaPlayer.setDataSource(mDataSource);
        return true;
    }

    /**
     * 播放状态下开始播放
     */
    private void startInPlaybackState() {
        mMediaPlayer.start();
        setPlayState(VideoPlayType.STATE_PLAYING);
        if (mAudioFocusHelper != null && !isMute()) {
            mAudioFocusHelper.requestFocus();
        }
        mPlayerContainer.setKeepScreenOn(true);
    }

    /**
     * 初始化之后的配置项
     */
    private void setOptions() {
        mMediaPlayer.setLooping(mIsLooping);
        float volume = mIsMute ? 0.0f : 1.0f;
        mMediaPlayer.setVolume(volume, volume);
    }

    /**
     * 保存播放进度
     */
    private void saveProgress() {
        if (mProgressManager != null && mDataSource != null && mCurrentPosition > 0) {
            MLog.d("saveProgress: " + mCurrentPosition);
            mProgressManager.saveProgress(mDataSource.mUrl, mCurrentPosition);
        }
    }

    /**
     * 获取Activity
     */
    private Activity getActivity() {
        Activity activity;
        if (mVideoController != null) {
            activity = Utils.scanForActivity(mVideoController.getContext());
            if (activity == null) {
                activity = Utils.scanForActivity(getContext());
            }
        } else {
            activity = Utils.scanForActivity(getContext());
        }
        return activity;
    }

    /**
     * 获取DecorView
     */
    private ViewGroup getDecorView() {
        Activity activity = getActivity();
        if (activity == null) return null;
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    /**
     * 获取activity中的contentView
     */
    private ViewGroup getContentView() {
        Activity activity = getActivity();
        if (activity == null) return null;
        return activity.findViewById(android.R.id.content);
    }

    /**
     * 显示NavigationBar和StatusBar
     */
    private void showSysBar(ViewGroup decorView) {
        int uiOptions = decorView.getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 隐藏NavigationBar和StatusBar
     */
    private void hideSysBar(ViewGroup decorView) {
        int uiOptions = decorView.getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            uiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    ////////////// 外部暴露方法 ////////////

    /**
     * 设置播放器背景底色
     *
     * @param color
     */
    public void setPlayerBackgroundColor(@ColorInt int color) {
        mPlayerContainer.setBackgroundColor(color);
    }

    /**
     * 设置视频地址
     */
    public void setDataSource(DataSource dataSource) {
        this.mDataSource = dataSource;
    }

    /**
     * 设置音量 [0.0f, 1.0f]
     *
     * @param v1 左声道音量
     * @param v2 右声道音量
     */
    public void setVolume(float v1, float v2) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(v1, v2);
        }
    }

    /**
     * 是否开启AudioFocus监听，默认开启，用于监听其它地方是否获取音频焦点，如果有其它地方获取了
     * 音频焦点，此播放器将做出相应反应，具体实现见{@link AudioFocusHelper}
     */
    public void setEnableAudioFocus(boolean enableAudioFocus) {
        mEnableAudioFocus = enableAudioFocus;
    }

    /**
     * 循环播放，默认不循环播放
     */
    public void setLooping(boolean looping) {
        mIsLooping = looping;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(looping);
        }
    }

    /**
     * 设置进度管理器，用于保存播放进度
     */
    public void setProgressManager(@Nullable IProgressManager progressManager) {
        this.mProgressManager = progressManager;
    }

    /**
     * 一开始播放就seek到预先设置好的位置
     */
    public void seekPositionWhenPlay(int position) {
        this.mCurrentPosition = position;
    }

    /**
     * 自定义解码器，继承{@link CreateClsFactory<AbstractPlayer>}实现自己的解码器
     */
    public void setPlayerFactory(CreateClsFactory<AbstractPlayer> playerFactory) {
        if (playerFactory == null) {
            throw new IllegalArgumentException("PlayerFactory can not be null!");
        }
        mPlayerFactory = playerFactory;
    }

    /**
     * 自定义渲染器，继承{@link CreateClsFactory<IRenderView>}实现自己的渲染器
     */
    public void setRenderViewFactory(CreateClsFactory<IRenderView> renderViewFactory) {
        if (renderViewFactory == null) {
            throw new IllegalArgumentException("RenderViewFactory can not be null!");
        }
        mRenderViewFactory = renderViewFactory;
    }

    /**
     * 设置控制器，传null表示移除控制器
     */
    public void setVideoController(@Nullable BaseVideoController mediaController) {
        mPlayerContainer.removeView(mVideoController);
        mVideoController = mediaController;
        if (mediaController != null) {
            mediaController.setMediaPlayer(this);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mPlayerContainer.addView(mVideoController, params);
        }
    }

    /**
     * 获取控制器
     */
    public BaseVideoController getVideoController() {
        return mVideoController;
    }

    /**
     * 设置小窗的宽高
     *
     * @param tinyScreenSize tinyScreenSize[0]是宽，tinyScreenSize[1]是高
     */
    public void setTinyScreenSize(int[] tinyScreenSize) {
        this.mTinyScreenSize = tinyScreenSize;
    }

    /**
     * 添加播放器监听
     */
    public void addVideoStateChangeListener(@NonNull OnVideoStateChangeListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        }
        if (!mOnStateChangeListeners.contains(listener))
            mOnStateChangeListeners.add(listener);
    }

    /**
     * 移除播放器监听
     */
    public void removeVideoStateChangeListener(@NonNull OnVideoStateChangeListener listener) {
        if (mOnStateChangeListeners != null) {
            mOnStateChangeListeners.remove(listener);
        }
    }

    /**
     * 设置播放器监听
     */
    public void setVideoStateChangeListener(@NonNull OnVideoStateChangeListener listener) {
        if (mOnStateChangeListeners == null) {
            mOnStateChangeListeners = new ArrayList<>();
        } else {
            mOnStateChangeListeners.clear();
        }
        mOnStateChangeListeners.add(listener);
    }

    /**
     * 移除所有播放器监听
     */
    public void clearVideoStateChangeListeners() {
        if (mOnStateChangeListeners != null) {
            mOnStateChangeListeners.clear();
        }
    }

    ////////////// 外部暴露方法 END ////////////


    //// PlayerControl Start ////

    /**
     * 开始播放，注意：调用此方法后必须调用{@link #release()}释放播放器，否则会导致内存泄漏
     */
    @Override
    public void start() {
        if (isInIdleState() || isInStartAbortState()) {
            startPlay();
        } else if (isInPlayState()) {
            startInPlaybackState();
        }
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (isInPlayState() && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setPlayState(VideoPlayType.STATE_PAUSED);
            if (mAudioFocusHelper != null && !isMute()) {
                mAudioFocusHelper.abandonFocus();
            }
            mPlayerContainer.setKeepScreenOn(false);
        }
    }

    /**
     * 继续播放
     */
    public void resume() {
        if (isInPlayState() && !mMediaPlayer.isPlaying()) {
            startInPlaybackState();
        }
    }

    /**
     * 释放播放器
     */
    @Override
    public void release() {
        if (!(isInIdleState() || isInStartAbortState())) {
            // 释放解码器
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            // 释放渲染器
            if (mRenderView != null) {
                mPlayerContainer.removeView(mRenderView.getView());
                mRenderView.release();
                mRenderView = null;
            }
            // 关闭AudioFocus监听
            if (mAudioFocusHelper != null) {
                mAudioFocusHelper.abandonFocus();
                mAudioFocusHelper = null;
            }
            // 关闭屏幕常亮
            mPlayerContainer.setKeepScreenOn(false);
            // 保存播放进度
            saveProgress();
            // 重置播放进度
            mCurrentPosition = 0;
            // 切换转态
            setPlayState(VideoPlayType.STATE_IDLE);
        }
    }

    /**
     * 重新播放
     *
     * @param resetPosition 是否从头开始播放
     */
    @Override
    public void replay(boolean resetPosition) {
        if (resetPosition) {
            mCurrentPosition = 0;
        }
        initRenderView();
        startPrepare(true);
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (isInPlayState()) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getCurrentPosition() {
        if (isInPlayState()) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
            return mCurrentPosition;
        }
        return 0;
    }

    /**
     * 调整播放进度
     */
    @Override
    public void seekTo(long pos) {
        if (isInPlayState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isPlaying() {
        return isInPlayState() && mMediaPlayer.isPlaying();
    }

    /**
     * 获取当前缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPercentage() : 0;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute(boolean isMute) {
        this.mIsMute = isMute;
        if (mMediaPlayer != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 是否处于静音状态
     */
    @Override
    public boolean isMute() {
        return mIsMute;
    }

    /**
     * 设置视频比例
     */
    @Override
    public void setScreenScaleType(@AspectRatioType int screenScaleType) {
        this.mScreenScaleType = screenScaleType;
        if (mRenderView != null) {
            mRenderView.setScaleType(screenScaleType);
        }
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        if (isInPlayState()) {
            mMediaPlayer.setSpeed(speed);
        }
    }

    /**
     * 获取播放速度
     */
    @Override
    public float getSpeed() {
        if (isInPlayState()) {
            return mMediaPlayer.getSpeed();
        }
        return 1f;
    }

    /**
     * 获取缓冲速度，只有IjkPlayer支持
     */
    @Override
    public long getTcpSpeed() {
        return mMediaPlayer != null ? mMediaPlayer.getTcpSpeed() : 0;
    }

    /**
     * 设置镜像旋转，暂不支持SurfaceView
     */
    @Override
    public void setMirrorRotation(boolean enable) {
        if (mRenderView != null) {
            mRenderView.getView().setScaleX(enable ? -1 : 1);
        }
    }

    /**
     * 截图，暂不支持SurfaceView
     */
    @Override
    public Bitmap doScreenShot() {
        if (mRenderView != null) {
            return mRenderView.doScreenShot();
        }
        return null;
    }

    /**
     * 获取视频宽高,其中width: mVideoSize[0], height: mVideoSize[1]
     */
    @Override
    public int[] getVideoSize() {
        return mVideoSize;
    }

    /**
     * 进入全屏
     */
    @Override
    public void enterFullScreen() {
        if (mIsFullScreen)
            return;

        ViewGroup decorView = getDecorView();
        if (decorView == null)
            return;

        mIsFullScreen = true;

        hideSysBar(decorView);
        this.removeView(mPlayerContainer);
        decorView.addView(mPlayerContainer);

        setPlayerState(VideoPlayerType.PLAYER_FULL_SCREEN);
    }

    /**
     * 退出全屏
     */
    @Override
    public void exitFullScreen() {
        if (!mIsFullScreen)
            return;

        ViewGroup decorView = getDecorView();
        if (decorView == null)
            return;

        mIsFullScreen = false;

        showSysBar(decorView);
        decorView.removeView(mPlayerContainer);
        this.addView(mPlayerContainer);

        setPlayerState(VideoPlayerType.PLAYER_NORMAL);
    }

    /**
     * 判断是否处于全屏状态
     */
    @Override
    public boolean isFullScreen() {
        return mIsFullScreen;
    }

    /**
     * 进入小窗
     */
    @Override
    public void enterTinyScreen() {
        if (mIsTinyScreen) return;
        ViewGroup contentView = getContentView();
        if (contentView == null)
            return;

        mIsTinyScreen = true;

        int width = mTinyScreenSize[0];
        if (width <= 0) {
            width = (int) (Utils.getScreenWidth(getContext(), false) / 2f);
        }
        int height = mTinyScreenSize[1];
        if (height <= 0) {
            height = (int) (width * 9f / 16);
        }

        this.removeView(mPlayerContainer);
        LayoutParams params = new LayoutParams(width, height);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        contentView.addView(mPlayerContainer, params);
        setPlayerState(VideoPlayerType.PLAYER_TINY_SCREEN);
    }

    /**
     * 退出小窗
     */
    @Override
    public void exitTinyScreen() {
        if (!mIsTinyScreen) return;
        ViewGroup contentView = getContentView();
        if (contentView == null)
            return;

        mIsTinyScreen = false;

        contentView.removeView(mPlayerContainer);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mPlayerContainer, params);

        setPlayerState(VideoPlayerType.PLAYER_NORMAL);
    }

    /**
     * 是否小窗
     *
     * @return
     */
    @Override
    public boolean isTinyScreen() {
        return mIsTinyScreen;
    }

    @Override
    public DataSource getDataSource() {
        return mDataSource;
    }

    //// PlayerControl End ////

    //// PlayerEventListener Start ////

    /**
     * 视频播放出错回调
     */
    @Override
    public void onError() {
        mPlayerContainer.setKeepScreenOn(false);
        setPlayState(VideoPlayType.STATE_ERROR);
    }

    /**
     * 视频播放完成回调
     */
    @Override
    public void onCompletion() {
        mPlayerContainer.setKeepScreenOn(false);
        mCurrentPosition = 0;
        if (mProgressManager != null && mDataSource != null) {
            mProgressManager.saveProgress(mDataSource.mUrl, 0); // 播放完成，清除进度
        }
        setPlayState(VideoPlayType.STATE_COMPLETED);
    }

    /**
     * 播放信息回调
     */
    @Override
    public void onInfo(int what, int extra) {
        switch (what) {
            case AbstractPlayer.MEDIA_INFO_RENDERING_START: // 视频/音频开始渲染
                setPlayState(VideoPlayType.STATE_PLAYING);
                mPlayerContainer.setKeepScreenOn(true);
                break;
            case AbstractPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                if (mRenderView != null)
                    mRenderView.setVideoRotation(extra);
                break;
        }
    }

    /**
     * 视频缓冲完毕，准备开始播放时回调
     */
    @Override
    public void onPrepared() {
        setPlayState(VideoPlayType.STATE_PREPARED);
        if (!isMute() && mAudioFocusHelper != null) {
            mAudioFocusHelper.requestFocus();
        }
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition);
        }
    }

    /**
     * 视频宽高改变
     */
    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight) {
        mVideoSize[0] = videoWidth;
        mVideoSize[1] = videoHeight;

        if (mRenderView != null) {
            mRenderView.setScaleType(mScreenScaleType);
            mRenderView.setVideoSize(videoWidth, videoHeight);
        }
    }

    /**
     * 缓存进度回调
     */
    @Override
    public void onBufferingUpdate(int percent) {
        if (mVideoController != null)
            mVideoController.onBufferingUpdate(percent);
    }

    //// PlayerEventListener End ////

    //////////// System Start //////////////

    /**
     * 旋转视频画面
     *
     * @param rotation 角度
     */
    @Override
    public void setRotation(float rotation) {
        if (mRenderView != null) {
            mRenderView.setVideoRotation((int) rotation);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        MLog.d("onSaveInstanceState: " + mCurrentPosition);
        saveProgress(); // activity切到后台后可能被系统回收，故在此处进行进度保存
        return super.onSaveInstanceState();
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && mIsFullScreen) {
            //重新获得焦点时保持全屏状态
            hideSysBar(getDecorView());
        }
    }

    //////////// System End //////////////

}
