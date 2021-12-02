package com.bill.player.exo;

import android.content.Context;

import com.bill.baseplayer.player.PlayerFactory;

/**
 * author ywb
 * date 2021/12/2
 * desc
 */
public class ExoPlayerFactory extends PlayerFactory {

    public static ExoPlayerFactory create() {
        return new ExoPlayerFactory();
    }

    @Override
    public ExoMediaPlayer createPlayer(Context context) {
        return new ExoMediaPlayer(context);
    }
}