package com.bill.baseplayer.render;

import android.view.View;

import com.bill.baseplayer.base.VideoView;
import com.bill.baseplayer.config.AspectRatioType;

/**
 * author ywb
 * date 2021/11/24
 * desc 渲染器测量
 */
public class MeasureHelper {

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRotationDegree;
    private @AspectRatioType
    int mCurrentAspectRatio;

    public void setVideoRotation(int videoRotationDegree) {
        mVideoRotationDegree = videoRotationDegree;
    }

    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    public void setAspectRatio(@AspectRatioType int aspectRatio) {
        mCurrentAspectRatio = aspectRatio;
    }

    /**
     * 测量
     */
    public int[] doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            // swap
            widthMeasureSpec = widthMeasureSpec + heightMeasureSpec;
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec;
            widthMeasureSpec = widthMeasureSpec - heightMeasureSpec;
        }

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        if (mVideoHeight == 0 || mVideoWidth == 0) {
            return new int[]{width, height};
        }

        //如果设置了比例
        switch (mCurrentAspectRatio) {
            case AspectRatioType.AR_ASPECT_FIT_PARENT:
            default:
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                break;
            case AspectRatioType.AR_ASPECT_WRAP_CONTENT:
                width = mVideoWidth;
                height = mVideoHeight;
                break;
            case AspectRatioType.AR_16_9_FIT_PARENT:
                if (height > width / 16f * 9) {
                    height = (int) (width / 16f * 9);
                } else {
                    width = (int) (height / 9f * 16);
                }
                break;
            case AspectRatioType.AR_4_3_FIT_PARENT:
                if (height > width / 4f * 3) {
                    height = (int) (width / 4f * 3);
                } else {
                    width = (int) (height / 3f * 4);
                }
                break;
            case AspectRatioType.AR_MATCH_PARENT:
                width = widthMeasureSpec;
                height = heightMeasureSpec;
                break;
            case AspectRatioType.AR_ASPECT_FILL_PARENT:
                if (mVideoWidth * height > width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else {
                    height = width * mVideoHeight / mVideoWidth;
                }
                break;
        }

        return new int[]{width, height};
    }

}
