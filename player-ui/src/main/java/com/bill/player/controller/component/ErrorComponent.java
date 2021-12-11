package com.bill.player.controller.component;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.bill.baseplayer.config.VideoPlayType;
import com.bill.baseplayer.util.Utils;
import com.bill.player.controller.R;

/**
 * author ywb
 * date 2021/12/7
 * desc 错误组件
 */
public class ErrorComponent extends BaseComponent implements View.OnClickListener {

    private AppCompatTextView errorMsgTv;

    public ErrorComponent(@NonNull Context context) {
        super(context);
        this.setVisibility(GONE);
        errorMsgTv = this.findViewById(R.id.iv_component_error_msg);
        this.findViewById(R.id.iv_component_error_back).setOnClickListener(this);
        this.findViewById(R.id.iv_component_error_retry).setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_component_error;
    }

    @Override
    public void onPlayStateChanged(@VideoPlayType int playState) {
        if (playState == VideoPlayType.STATE_ERROR) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_component_error_back) {
            if (mControlWrapper != null && mControlWrapper.isFullScreen()) {
                Activity activity = Utils.scanForActivity(getContext());
                mControlWrapper.toggleFullScreen(activity);
            } else {
                Activity activity = Utils.scanForActivity(getContext());
                activity.finish();
            }
        } else if (v.getId() == R.id.iv_component_error_retry) {
            if (mControlWrapper != null)
                mControlWrapper.replay(true);
        }
    }
}
