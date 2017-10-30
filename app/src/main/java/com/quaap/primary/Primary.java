package com.quaap.primary;


import android.app.Application;

import com.quaap.primary.base.component.SoundEffects;
import com.quaap.primary.base.component.TextToVoice;

/**
 * Created by tom on 12/29/16.
 * <p>
 * Copyright (C) 2016  tom
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
public class Primary extends Application {

    private TextToVoice mTtv;


    private SoundEffects mSoundEffects;

    @Override
    public void onCreate() {
        super.onCreate();
        mSoundEffects = new SoundEffects(this);
    }


    public SoundEffects getSoundEffects() {
        return mSoundEffects;
    }

    public TextToVoice getTextToVoice() {
        if (mTtv == null) {
            mTtv = new TextToVoice(this);
        }
        return mTtv;
    }

    @Override
    public void onTerminate() {
        if (mTtv != null) {
            mTtv.shutDown();
            mTtv = null;
        }
        mSoundEffects.release();
        super.onTerminate();
    }

}
