package com.bill.player.ijk;

import android.content.Context;

import com.bill.baseplayer.player.AndroidMediaPlayer;
import com.bill.baseplayer.player.PlayerFactory;

/**
 * author ywb
 * date 2021/12/1
 * desc
 */
public class IjkPlayerFactory extends PlayerFactory {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkPlayer createPlayer(Context context) {
        return new IjkPlayer(context);
    }
}