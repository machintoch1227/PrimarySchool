package com.quaap.primary.base;

import android.content.Context;

import com.quaap.primary.R;
import com.quaap.primary.base.component.InputMode;

/**
 * Created by tom on 12/31/16.
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
public abstract class StdLevel extends Level {
    private InputMode mInputMode;

    public StdLevel(int subjectkey, int rounds, InputMode mInputMode) {
        super(subjectkey, rounds);
        this.mInputMode = mInputMode;
    }


    public InputMode getInputMode() {
        return mInputMode;
    }

    public String getInputModeString(Context context) {
        return getInputMode() == InputMode.Buttons ? context.getString(R.string.disp_buttons) : context.getString(R.string.disp_input);
    }
}
