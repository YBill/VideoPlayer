package com.bill.baseplayer.util;

import android.content.Context;

/**
 * author ywb
 * date 2021/12/11
 * desc 创建类
 */
public abstract class CreateClsFactory<T> {

    public abstract T create(Context context);

}
