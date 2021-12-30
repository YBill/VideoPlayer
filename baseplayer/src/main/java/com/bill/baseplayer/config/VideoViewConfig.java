package com.bill.baseplayer.config;

import androidx.annotation.Nullable;

import com.bill.baseplayer.player.AbstractPlayer;
import com.bill.baseplayer.player.AndroidMediaPlayerFactory;
import com.bill.baseplayer.player.IProgressManager;
import com.bill.baseplayer.render.IRenderView;
import com.bill.baseplayer.render.TextureRenderViewFactory;
import com.bill.baseplayer.util.CreateClsFactory;

/**
 * author ywb
 * date 2021/11/26
 * desc 播放器全局配置
 */
public class VideoViewConfig {

    public static Builder create() {
        return new Builder();
    }

    public final boolean mIsEnableLog;
    public final boolean mEnableOrientation;
    public final boolean mPlayOnMobileNetwork;
    public final boolean mEnableAudioFocus;
    public final IProgressManager mProgressManager;
    public final @AspectRatioType
    int mScreenScaleType;
    public CreateClsFactory<AbstractPlayer> mPlayerFactory;
    public CreateClsFactory<IRenderView> mRenderViewFactory;

    VideoViewConfig(Builder builder) {
        mIsEnableLog = builder.mIsEnableLog;
        mEnableOrientation = builder.mEnableOrientation;
        mPlayOnMobileNetwork = builder.mPlayOnMobileNetwork;
        mEnableAudioFocus = builder.mEnableAudioFocus;
        mProgressManager = builder.mProgressManager;
        mScreenScaleType = builder.mScreenScaleType;
        mPlayerFactory = builder.mPlayerFactory;
        mRenderViewFactory = builder.mRenderViewFactory;
        initialValue();
    }

    private void initialValue() {
        if (mPlayerFactory == null)
            mPlayerFactory = AndroidMediaPlayerFactory.create();
        if (mRenderViewFactory == null)
            mRenderViewFactory = TextureRenderViewFactory.create();
    }

    public final static class Builder {

        private boolean mIsEnableLog = false;
        private boolean mPlayOnMobileNetwork = true;
        private boolean mEnableOrientation = false;
        private boolean mEnableAudioFocus = true;
        private IProgressManager mProgressManager;
        private CreateClsFactory<AbstractPlayer> mPlayerFactory;
        private CreateClsFactory<IRenderView> mRenderViewFactory;
        private int mScreenScaleType = AspectRatioType.AR_ASPECT_FIT_PARENT;

        /**
         * 是否打印日志，默认不打印
         */
        public Builder setLogEnabled(boolean enableLog) {
            mIsEnableLog = enableLog;
            return this;
        }

        /**
         * 在移动环境下调用start()后是否继续播放，默认继续播放
         * 配合 VideoViewManager.getInstance().setPlayOnMobileNetwork(true) 使用（用户继续后应该如果不再需要提示）
         */
        public Builder setPlayOnMobileNetwork(boolean playOnMobileNetwork) {
            mPlayOnMobileNetwork = playOnMobileNetwork;
            return this;
        }

        /**
         * 是否监听设备方向来切换全屏/半屏，默认不开启
         */
        public Builder setEnableOrientation(boolean enableOrientation) {
            mEnableOrientation = enableOrientation;
            return this;
        }

        /**
         * 设置进度管理器，用于保存播放进度
         */
        public Builder setProgressManager(@Nullable IProgressManager progressManager) {
            mProgressManager = progressManager;
            return this;
        }

        /**
         * 设置解码器，默认使用MediaPlayer
         */
        public Builder setPlayerFactory(CreateClsFactory<AbstractPlayer> playerFactory) {
            mPlayerFactory = playerFactory;
            return this;
        }

        /**
         * 设置渲染器，默认使用TextureView
         */
        public Builder setRenderViewFactory(CreateClsFactory<IRenderView> renderViewFactory) {
            mRenderViewFactory = renderViewFactory;
            return this;
        }

        /**
         * 设置视频比例 {@link AspectRatioType}
         */
        public Builder setScreenScaleType(@AspectRatioType int screenScaleType) {
            mScreenScaleType = screenScaleType;
            return this;
        }

        public VideoViewConfig build() {
            return new VideoViewConfig(this);
        }

    }

}
