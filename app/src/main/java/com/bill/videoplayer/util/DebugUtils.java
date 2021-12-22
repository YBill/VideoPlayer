package com.bill.videoplayer.util;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.config.AspectRatioType;
import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.config.VideoViewConfig;
import com.bill.baseplayer.config.VideoViewManager;
import com.bill.baseplayer.controller.ControlWrapper;
import com.bill.baseplayer.player.AndroidMediaPlayerFactory;
import com.bill.baseplayer.render.NullRenderViewFactory;
import com.bill.baseplayer.render.SurfaceRenderViewFactory;
import com.bill.baseplayer.render.TextureRenderViewFactory;
import com.bill.baseplayer.util.CreateClsFactory;
import com.bill.player.exo.ExoPlayerFactory;
import com.bill.player.ijk.IjkPlayerFactory;

import java.lang.reflect.Field;

/**
 * author ywb
 * date 2021/12/22
 * desc 测试工具类
 */
public class DebugUtils {

    public static void setVideoViewFactory(String factoryType, CreateClsFactory<?> factory) {
        try {
            VideoViewConfig config = VideoViewManager.getInstance().getConfig();
            Field factoryField = config.getClass().getDeclaredField(factoryType);
            factoryField.setAccessible(true);
            factoryField.set(config, factory);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentPlayer(ControlWrapper controlWrapper) {
        String player;
        Object playerFactory = getCurrentFactoryInVideoView(controlWrapper, "mPlayerFactory");
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

    public static String getCurrentRenderer(ControlWrapper controlWrapper) {
        String player;
        Object playerFactory = getCurrentFactoryInVideoView(controlWrapper, "mRenderViewFactory");
        if (playerFactory instanceof TextureRenderViewFactory) {
            player = "TextureView";
        } else if (playerFactory instanceof SurfaceRenderViewFactory) {
            player = "SurfaceView";
        } else if (playerFactory instanceof NullRenderViewFactory) {
            player = "NullRenderView";
        } else {
            player = "unknown";
        }
        return String.format("Renderer: %s ", player);
    }

    private static Object getCurrentFactoryInVideoView(ControlWrapper controlWrapper, String field) {
        Object playerFactory = null;
        try {
            Field playerControlField = controlWrapper.getClass().getDeclaredField("mPlayerControl");
            playerControlField.setAccessible(true);
            Object playerControl = playerControlField.get(controlWrapper);
            if (playerControl instanceof VideoView) {
                VideoView videoView = (VideoView) playerControl;
                Field mPlayerFactoryField = videoView.getClass().getDeclaredField(field);
                mPlayerFactoryField.setAccessible(true);
                playerFactory = mPlayerFactoryField.get(videoView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }

    public static void setAspectRatioType(int aspectRatioType) {
        try {
            VideoViewConfig config = VideoViewManager.getInstance().getConfig();
            Field factoryField = config.getClass().getDeclaredField("mScreenScaleType");
            factoryField.setAccessible(true);
            factoryField.setInt(config, aspectRatioType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static int getCurrentScaleTypeInVideoView(ControlWrapper controlWrapper) {
        int screenScaleType = -1;
        try {
            Field playerControlField = controlWrapper.getClass().getDeclaredField("mPlayerControl");
            playerControlField.setAccessible(true);
            Object playerControl = playerControlField.get(controlWrapper);
            if (playerControl instanceof VideoView) {
                VideoView videoView = (VideoView) playerControl;
                Field mPlayerFactoryField = videoView.getClass().getDeclaredField("mScreenScaleType");
                mPlayerFactoryField.setAccessible(true);
                screenScaleType = mPlayerFactoryField.getInt(videoView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenScaleType;
    }

    public static String getAspectRatioType(ControlWrapper controlWrapper) {
        int type = getCurrentScaleTypeInVideoView(controlWrapper);
        String aspectRatioType;
        switch (type) {
            case AspectRatioType.AR_ASPECT_FIT_PARENT:
                aspectRatioType = "fitCenter";
                break;
            case AspectRatioType.AR_ASPECT_FILL_PARENT:
                aspectRatioType = "centerCrop";
                break;
            case AspectRatioType.AR_ASPECT_WRAP_CONTENT:
                aspectRatioType = "center";
                break;
            case AspectRatioType.AR_MATCH_PARENT:
                aspectRatioType = "fitXY";
                break;
            case AspectRatioType.AR_16_9_FIT_PARENT:
                aspectRatioType = "16:9";
                break;
            case AspectRatioType.AR_4_3_FIT_PARENT:
                aspectRatioType = "4:3";
                break;
            default:
                aspectRatioType = "unknown";
                break;
        }
        return String.format("AspectRatioType: %s", aspectRatioType);
    }

    public static String getPlayState2str(int state) {
        String playStateString;
        switch (state) {
            default:
            case VideoPlayType.STATE_IDLE:
                playStateString = "idle";
                break;
            case VideoPlayType.STATE_PREPARING:
                playStateString = "preparing";
                break;
            case VideoPlayType.STATE_PREPARED:
                playStateString = "prepared";
                break;
            case VideoPlayType.STATE_PLAYING:
                playStateString = "playing";
                break;
            case VideoPlayType.STATE_PAUSED:
                playStateString = "pause";
                break;
            case VideoPlayType.STATE_COMPLETED:
                playStateString = "completed";
                break;
            case VideoPlayType.STATE_ERROR:
                playStateString = "error";
                break;
        }
        return String.format("PlayState: %s", playStateString);
    }

}
