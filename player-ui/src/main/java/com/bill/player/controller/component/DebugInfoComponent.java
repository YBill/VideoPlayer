package com.bill.player.controller.component;

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
import com.bill.baseplayer.player.PlayerFactory;
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
        return getCurrentPlayer() + playState2str(playState) + "\n"
                + "video width: " + mControlWrapper.getVideoSize()[0] + " , height: " + mControlWrapper.getVideoSize()[1];
    }

    private String getCurrentPlayer() {
        String player;
        Object playerFactory = getCurrentPlayerFactoryInVideoView(mControlWrapper);
        if (playerFactory instanceof AndroidMediaPlayerFactory) {
            player = "MediaPlayer";
        } else if (playerFactory instanceof IjkPlayerFactory) {
            player = "IjkPlayer";
        } else if (playerFactory instanceof ExoPlayerFactory) {
            player = "ExoPlayer";
        } else {
            player = "unknown";
        }
        return String.format("player: %s ", player);
    }

    public static Object getCurrentPlayerFactoryInVideoView(ControlWrapper controlWrapper) {
        Object playerFactory = null;
        try {
            Field mPlayerControlField = controlWrapper.getClass().getDeclaredField("mPlayerControl");
            mPlayerControlField.setAccessible(true);
            Object playerControl = mPlayerControlField.get(controlWrapper);
            if (playerControl instanceof VideoView) {
                playerFactory = getCurrentPlayerFactoryInVideoView((VideoView) playerControl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }

    public static Object getCurrentPlayerFactoryInVideoView(VideoView videoView) {
        Object playerFactory = null;
        try {
            Field mPlayerFactoryField = videoView.getClass().getDeclaredField("mPlayerFactory");
            mPlayerFactoryField.setAccessible(true);
            playerFactory = mPlayerFactoryField.get(videoView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }

    public static String playState2str(int state) {
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
            case VideoView.STATE_BUFFERING:
                playStateString = "buffering";
                break;
            case VideoView.STATE_BUFFERED:
                playStateString = "buffered";
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                playStateString = "playback completed";
                break;
            case VideoView.STATE_ERROR:
                playStateString = "error";
                break;
        }
        return String.format("playState: %s", playStateString);
    }

}
