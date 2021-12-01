package com.bill.baseplayer.config;

import androidx.annotation.Nullable;

import com.bill.baseplayer.player.AndroidMediaPlayerFactory;
import com.bill.baseplayer.player.IProgressManager;
import com.bill.baseplayer.player.PlayerFactory;
import com.bill.baseplayer.render.RenderViewFactory;
import com.bill.baseplayer.render.TextureRenderViewFactory;

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
    public final boolean mAdaptCutout;
    public final boolean mPlayOnMobileNetwork;
    public final boolean mEnableAudioFocus;
    public final IProgressManager mProgressManager;
    public final int mScreenScaleType;
    public PlayerFactory mPlayerFactory;
    public RenderViewFactory mRenderViewFactory;

    VideoViewConfig(Builder builder) {
        mIsEnableLog = builder.mIsEnableLog;
        mEnableOrientation = builder.mEnableOrientation;
        mPlayOnMobileNetwork = builder.mPlayOnMobileNetwork;
        mEnableAudioFocus = builder.mEnableAudioFocus;
        mProgressManager = builder.mProgressManager;
        mScreenScaleType = builder.mScreenScaleType;
        mPlayerFactory = builder.mPlayerFactory;
        mRenderViewFactory = builder.mRenderViewFactory;
        mAdaptCutout = builder.mAdaptCutout;
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
        private boolean mAdaptCutout = true;
        private IProgressManager mProgressManager;
        private PlayerFactory mPlayerFactory;
        private RenderViewFactory mRenderViewFactory;
        private int mScreenScaleType;

        /**
         * 是否打印日志，默认不打印
         */
        public Builder setLogEnabled(boolean enableLog) {
            mIsEnableLog = enableLog;
            return this;
        }

        /**
         * 在移动环境下调用start()后是否继续播放，默认继续播放
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
         * 是否开启AudioFocus监听，默认开启
         */
        public Builder setEnableAudioFocus(boolean enableAudioFocus) {
            mEnableAudioFocus = enableAudioFocus;
            return this;
        }

        /**
         * 是否适配刘海屏，默认适配
         */
        public Builder setAdaptCutout(boolean adaptCutout) {
            mAdaptCutout = adaptCutout;
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
         * 设置解码器
         */
        public Builder setPlayerFactory(PlayerFactory playerFactory) {
            mPlayerFactory = playerFactory;
            return this;
        }

        /**
         * 设置渲染器
         */
        public Builder setRenderViewFactory(RenderViewFactory renderViewFactory) {
            mRenderViewFactory = renderViewFactory;
            return this;
        }

        /**
         * 设置视频比例
         */
        public Builder setScreenScaleType(int screenScaleType) {
            mScreenScaleType = screenScaleType;
            return this;
        }

        public VideoViewConfig build() {
            return new VideoViewConfig(this);
        }

    }

}
