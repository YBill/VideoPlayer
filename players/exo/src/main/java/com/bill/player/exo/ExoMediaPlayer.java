package com.bill.player.exo;

import android.content.Context;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.bill.baseplayer.config.VideoViewManager;
import com.bill.baseplayer.player.AbstractPlayer;
import com.bill.baseplayer.player.DataSource;
import com.bill.baseplayer.util.DataSourceUtil;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.video.VideoSize;

/**
 * author ywb
 * date 2021/12/2
 * desc
 */
public class ExoMediaPlayer extends AbstractPlayer implements Player.Listener {

    private final Context mAppContext;
    private ExoPlayer mInternalPlayer;
    private MediaSource mMediaSource;
    private final ExoMediaSourceHelper mMediaSourceHelper;

    private RenderersFactory mRenderersFactory;
    private TrackSelector mTrackSelector;
    private LoadControl mLoadControl;

    private PlaybackParameters mSpeedPlaybackParameters;

    private boolean mIsPreparing; // 判断seekTo后重复准备问题

    public ExoMediaPlayer(Context context) {
        mAppContext = context.getApplicationContext();
        mMediaSourceHelper = ExoMediaSourceHelper.getInstance(context);
    }

    @Override
    public void initPlayer() {
        mInternalPlayer = new ExoPlayer.Builder(
                mAppContext,
                mRenderersFactory == null ? mRenderersFactory = new DefaultRenderersFactory(mAppContext) : mRenderersFactory,
                new DefaultMediaSourceFactory(mAppContext),
                mTrackSelector == null ? mTrackSelector = new DefaultTrackSelector(mAppContext) : mTrackSelector,
                mLoadControl == null ? mLoadControl = new DefaultLoadControl() : mLoadControl,
                DefaultBandwidthMeter.getSingletonInstance(mAppContext),
                new AnalyticsCollector(Clock.DEFAULT))
                .build();
        setOptions();

        //播放器日志
        if (VideoViewManager.getInstance().getConfig().mIsEnableLog
                && mTrackSelector instanceof MappingTrackSelector) {
            mInternalPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) mTrackSelector, "ExoPlayer"));
        }

        mInternalPlayer.addListener(this);
    }

    public void setTrackSelector(TrackSelector trackSelector) {
        mTrackSelector = trackSelector;
    }

    public void setRenderersFactory(RenderersFactory renderersFactory) {
        mRenderersFactory = renderersFactory;
    }

    public void setLoadControl(LoadControl loadControl) {
        mLoadControl = loadControl;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        try {
            if (dataSource.mRawId != 0) {
                String rawUri = DataSourceUtil.buildRawPath(mAppContext.getPackageName(), dataSource.mRawId);
                mMediaSource = mMediaSourceHelper.getMediaSource(rawUri, null);
            } else if (!TextUtils.isEmpty(dataSource.mAssetsPath)) {
                mMediaSource = mMediaSourceHelper.getMediaSource(DataSourceUtil.buildAssets(dataSource.mAssetsPath), null);
            } else {
                mMediaSource = mMediaSourceHelper.getMediaSource(dataSource.mUrl, dataSource.mHeaders);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mPlayerEventListener != null)
                mPlayerEventListener.onError();
        }
    }

    @Override
    public void setOptions() {
        mInternalPlayer.setPlayWhenReady(true);
    }

    private boolean isAvailable() {
        return mInternalPlayer != null;
    }

    @Override
    public void start() {
        if (!isAvailable())
            return;
        mInternalPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        if (!isAvailable())
            return;
        mInternalPlayer.setPlayWhenReady(false);
    }

    @Override
    public void stop() {
        if (!isAvailable())
            return;
        mInternalPlayer.stop();
    }

    @Override
    public void prepareAsync() {
        if (!isAvailable())
            return;
        if (mMediaSource == null) return;
        if (mSpeedPlaybackParameters != null) {
            mInternalPlayer.setPlaybackParameters(mSpeedPlaybackParameters);
        }
        mIsPreparing = true;
        mInternalPlayer.setMediaSource(mMediaSource);
        mInternalPlayer.prepare();
    }

    @Override
    public void reset() {
        if (!isAvailable())
            return;
        mInternalPlayer.stop();
        mInternalPlayer.clearMediaItems();
        mInternalPlayer.setVideoSurface(null);
        mIsPreparing = false;
    }

    @Override
    public boolean isPlaying() {
        if (!isAvailable())
            return false;
        int state = mInternalPlayer.getPlaybackState();
        switch (state) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                return mInternalPlayer.getPlayWhenReady();
            case Player.STATE_IDLE:
            case Player.STATE_ENDED:
            default:
                return false;
        }
    }

    @Override
    public void seekTo(long time) {
        if (!isAvailable())
            return;
        mInternalPlayer.seekTo(time);
    }

    @Override
    public void release() {
        if (mInternalPlayer != null) {
            mInternalPlayer.removeListener(this);
            mInternalPlayer.release();
            mInternalPlayer = null;
        }

        mIsPreparing = false;
        mSpeedPlaybackParameters = null;
    }

    @Override
    public long getCurrentPosition() {
        if (!isAvailable())
            return 0;
        return mInternalPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        if (!isAvailable())
            return 0;
        return mInternalPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        if (!isAvailable())
            return 0;
        return mInternalPlayer.getBufferedPercentage();
    }

    @Override
    public void setSurface(Surface surface) {
        if (!isAvailable())
            return;
        mInternalPlayer.setVideoSurface(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        if (!isAvailable())
            return;
        mInternalPlayer.setVideoSurfaceHolder(holder);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (!isAvailable())
            return;
        mInternalPlayer.setVolume((leftVolume + rightVolume) / 2);
    }

    @Override
    public void setLooping(boolean isLooping) {
        if (!isAvailable())
            return;
        mInternalPlayer.setRepeatMode(isLooping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
    }

    @Override
    public void setSpeed(float speed) {
        if (!isAvailable())
            return;
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        mSpeedPlaybackParameters = playbackParameters;
        mInternalPlayer.setPlaybackParameters(playbackParameters);
    }

    @Override
    public float getSpeed() {
        if (mSpeedPlaybackParameters != null) {
            return mSpeedPlaybackParameters.speed;
        }
        return 1f;
    }

    @Override
    public long getTcpSpeed() {
        // no support
        return 0;
    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        if (mPlayerEventListener == null) return;

        // 调用seekTo时还会重新走这里，还会prepared，这里check下
        if (mIsPreparing && playbackState == Player.STATE_READY) {
            mPlayerEventListener.onPrepared();
            mPlayerEventListener.onInfo(MEDIA_INFO_RENDERING_START, 0);
            mIsPreparing = false;
        }

        switch (playbackState) {
            case Player.STATE_ENDED:
                mPlayerEventListener.onCompletion();
                break;
        }
    }

    @Override
    public void onPlayerError(PlaybackException error) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        if (mPlayerEventListener != null) {
            mPlayerEventListener.onVideoSizeChanged(videoSize.width, videoSize.height);
            if (videoSize.unappliedRotationDegrees > 0) {
                mPlayerEventListener.onInfo(MEDIA_INFO_VIDEO_ROTATION_CHANGED, videoSize.unappliedRotationDegrees);
            }
        }
    }

    @Override
    public void onRenderedFirstFrame() {
        // onRenderedFirstFrame 比 onPlaybackStateChanged 中 STATE_READY 先执行有问题，放到onPlaybackStateChanged 中了
//        if (mPlayerEventListener != null)
//            mPlayerEventListener.onInfo(MEDIA_INFO_RENDERING_START, 0);
    }

    @Override
    public void onIsLoadingChanged(boolean isLoading) {
        if (!isLoading) {
            int buffer = getBufferedPercentage();
            if (mPlayerEventListener != null)
                mPlayerEventListener.onBufferingUpdate(buffer);
        }
    }
}
