package com.bill.player.exo;

import android.content.Context;

import com.bill.baseplayer.player.AbstractPlayer;
import com.bill.baseplayer.util.CreateClsFactory;

/**
 * author ywb
 * date 2021/12/2
 * desc
 */
public class ExoPlayerFactory extends CreateClsFactory<AbstractPlayer> {

    public static ExoPlayerFactory create() {
        return new ExoPlayerFactory();
    }

    @Override
    public AbstractPlayer create(Context context) {
        return new ExoMediaPlayer(context);
    }
}