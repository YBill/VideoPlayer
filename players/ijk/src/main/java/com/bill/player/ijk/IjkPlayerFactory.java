package com.bill.player.ijk;

import android.content.Context;

import com.bill.baseplayer.player.AbstractPlayer;
import com.bill.baseplayer.util.CreateClsFactory;

/**
 * author ywb
 * date 2021/12/1
 * desc
 */
public class IjkPlayerFactory extends CreateClsFactory<AbstractPlayer> {

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public AbstractPlayer create(Context context) {
        return new IjkPlayer(context);
    }
}