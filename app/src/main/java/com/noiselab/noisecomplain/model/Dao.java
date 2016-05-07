package com.noiselab.noisecomplain.model;

import android.content.Context;
import android.inputmethodservice.Keyboard;

import java.util.List;

/**
 * Created by shawn on 27/3/2016.
 */
public abstract class Dao<T> {

    protected Context mContext;

    public Dao(Context context) {
        mContext = context;
    }

    public abstract List<T> queryAll();

    public abstract T queryById(String id);

    public abstract long insert(T bean);

    public abstract long update(T bean);

    public abstract int deleteById(String id);

}

