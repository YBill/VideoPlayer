package com.bill.baseplayer.player;

import android.content.Context;

import com.bill.baseplayer.util.CreateClsFactory;

/**
 * author ywb
 * date 2021/11/24
 * desc
 */
public class AndroidMediaPlayerFactory extends CreateClsFactory<AbstractPlayer> {

    public static AndroidMediaPlayerFactory create() {
        return new AndroidMediaPlayerFactory();
    }

    @Override
    public AbstractPlayer create(Context context) {
        return new AndroidMediaPlayer(context);
    }
}