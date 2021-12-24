package com.bill.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bill.baseplayer.config.AspectRatioType;
import com.bill.baseplayer.player.AndroidMediaPlayerFactory;
import com.bill.baseplayer.render.NullRenderViewFactory;
import com.bill.baseplayer.render.SurfaceRenderViewFactory;
import com.bill.baseplayer.render.TextureRenderViewFactory;
import com.bill.player.exo.ExoPlayerFactory;
import com.bill.player.ijk.IjkPlayerFactory;
import com.bill.videoplayer.act.ParallelPlayerActivity;
import com.bill.videoplayer.act.ShortVideoGroupActivity;
import com.bill.videoplayer.act.SmallVideoListActivity;
import com.bill.videoplayer.act.TinyGroupActivity;
import com.bill.videoplayer.act.UniversalPlayerActivity;
import com.bill.videoplayer.util.DebugUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void handlePlayer(View view) {
        Intent intent = new Intent(this, UniversalPlayerActivity.class);
        startActivity(intent);
    }

    public void handleOpenMore(View view) {
        Intent intent = new Intent(this, ParallelPlayerActivity.class);
        startActivity(intent);
    }

    public void handleShortVideo(View view) {
        Intent intent = new Intent(this, ShortVideoGroupActivity.class);
        startActivity(intent);
    }

    public void handleSmallVideo(View view) {
        Intent intent = new Intent(this, SmallVideoListActivity.class);
        startActivity(intent);
    }

    public void handleTiny(View view) {
        Intent intent = new Intent(this, TinyGroupActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        /**
         * 为了方便，下面都是通过反射来修改的，真实使用情况可以使用 {@link com.bill.baseplayer.base.VideoView} 来修改
         */

        switch (itemId) {
            case R.id.ijk:
                DebugUtils.setVideoViewFactory("mPlayerFactory", IjkPlayerFactory.create());
                break;
            case R.id.exo:
                DebugUtils.setVideoViewFactory("mPlayerFactory", ExoPlayerFactory.create());
                break;
            case R.id.media:
                DebugUtils.setVideoViewFactory("mPlayerFactory", AndroidMediaPlayerFactory.create());
                break;
            case R.id.texture:
                DebugUtils.setVideoViewFactory("mRenderViewFactory", TextureRenderViewFactory.create());
                break;
            case R.id.surface:
                DebugUtils.setVideoViewFactory("mRenderViewFactory", SurfaceRenderViewFactory.create());
                break;
            case R.id.none:
                DebugUtils.setVideoViewFactory("mRenderViewFactory", NullRenderViewFactory.create());
                break;
            case R.id.fit_center:
                DebugUtils.setAspectRatioType(AspectRatioType.AR_ASPECT_FIT_PARENT);
                break;
            case R.id.center_crop:
                DebugUtils.setAspectRatioType(AspectRatioType.AR_ASPECT_FILL_PARENT);
                break;
            case R.id.center:
                DebugUtils.setAspectRatioType(AspectRatioType.AR_ASPECT_WRAP_CONTENT);
                break;
            case R.id.fit_xy:
                DebugUtils.setAspectRatioType(AspectRatioType.AR_MATCH_PARENT);
                break;
            case R.id.st_16_9:
                DebugUtils.setAspectRatioType(AspectRatioType.AR_16_9_FIT_PARENT);
                break;
            case R.id.st_4_3:
                DebugUtils.setAspectRatioType(AspectRatioType.AR_4_3_FIT_PARENT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}