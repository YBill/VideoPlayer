package com.bill.videoplayer;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.controller.BaseComponent;
import com.bill.baseplayer.controller.ControlWrapper;
import com.bill.baseplayer.player.AndroidMediaPlayerFactory;
import com.bill.baseplayer.render.SurfaceRenderViewFactory;
import com.bill.baseplayer.render.TextureRenderViewFactory;
import com.bill.player.exo.ExoPlayerFactory;
import com.bill.player.ijk.IjkPlayerFactory;

import java.lang.reflect.Field;

/**
 * author ywb
 * date 2021/12/1
 * desc
 */
public class DebugInfoComponent extends BaseComponent {

    private AppCompatTextView mTextView;

    public DebugInfoComponent(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        mTextView = new AppCompatTextView(getContext());
        mTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mTextView.setBackgroundResource(android.R.color.black);
        mTextView.setTextSize(10);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mTextView.setLayoutParams(lp);
        this.addView(mTextView);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        mTextView.setText(getDebugString(playState));
    }

    private String getDebugString(int playState) {
        return getCurrentPlayer() + getCurrentRenderer() + "\n"
                + "video width: " + mControlWrapper.getVideoSize()[0] + " , height: " + mControlWrapper.getVideoSize()[1] + "\n"
                + playState2str(playState);
    }

    private String getCurrentPlayer() {
        String player;
        Object playerFactory = getCurrentFactoryInVideoView(mControlWrapper, "mPlayerFactory");
        if (playerFactory instanceof AndroidMediaPlayerFactory) {
            player = "MediaPlayer";
        } else if (playerFactory instanceof IjkPlayerFactory) {
            player = "IjkPlayer";
        } else if (playerFactory instanceof ExoPlayerFactory) {
            player = "ExoPlayer";
        } else {
            player = "unknown";
        }
        return String.format("Player: %s ", player);
    }

    private String getCurrentRenderer() {
        String player;
        Object playerFactory = getCurrentFactoryInVideoView(mControlWrapper, "mRenderViewFactory");
        if (playerFactory instanceof TextureRenderViewFactory) {
            player = "TextureView";
        } else if (playerFactory instanceof SurfaceRenderViewFactory) {
            player = "SurfaceView";
        } else {
            player = "unknown";
        }
        return String.format("Renderer: %s ", player);
    }

    private static Object getCurrentFactoryInVideoView(ControlWrapper controlWrapper, String field) {
        Object playerFactory = null;
        try {
            Field mPlayerControlField = controlWrapper.getClass().getDeclaredField("mPlayerControl");
            mPlayerControlField.setAccessible(true);
            Object playerControl = mPlayerControlField.get(controlWrapper);
            if (playerControl instanceof VideoView) {
                playerFactory = getCurrentFactoryInVideoView((VideoView) playerControl, field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }

    private static Object getCurrentFactoryInVideoView(VideoView videoView, String field) {
        Object playerFactory = null;
        try {
            Field mPlayerFactoryField = videoView.getClass().getDeclaredField(field);
            mPlayerFactoryField.setAccessible(true);
            playerFactory = mPlayerFactoryField.get(videoView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }

    private static String playState2str(int state) {
        String playStateString;
        switch (state) {
            default:
            case VideoView.STATE_IDLE:
                playStateString = "idle";
                break;
            case VideoView.STATE_PREPARING:
                playStateString = "preparing";
                break;
            case VideoView.STATE_PREPARED:
                playStateString = "prepared";
                break;
            case VideoView.STATE_PLAYING:
                playStateString = "playing";
                break;
            case VideoView.STATE_PAUSED:
                playStateString = "pause";
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                playStateString = "playback completed";
                break;
            case VideoView.STATE_ERROR:
                playStateString = "error";
                break;
        }
        return String.format("PlayState: %s", playStateString);
    }

}
