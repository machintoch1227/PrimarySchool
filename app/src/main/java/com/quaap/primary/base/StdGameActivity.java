package com.quaap.primary.base;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quaap.primary.R;
import com.quaap.primary.base.component.InputMode;
import com.quaap.primary.base.component.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tom on 12/28/16.
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
public abstract class StdGameActivity extends SubjectBaseActivity {

    // Things here are common to game activities which want ot use the standard layout.

    private int mProblemView;

    private volatile Timer timer;
    private volatile TimerTask hinttask;

    private volatile boolean showHint = false;
    private volatile int hintTick;

    protected static final int BASE_HINT_TIME = 20000;
    protected static final int BASE_HINT_REPEAT_TIME = 3000;

    public StdGameActivity(int problemView) {
        super(R.layout.std_game_layout);
        mProblemView = problemView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup probarea = (ViewGroup) findViewById(R.id.problem_area);

        LayoutInflater.from(this).inflate(mProblemView, probarea);

    }

    @Override
    protected void onPause() {


        cancelHint();

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();



    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        GridLayout answerarea = (GridLayout) findViewById(R.id.answer_area);
        LinearLayout centercol = (LinearLayout) findViewById(R.id.centercol);
        ActionBar actionBar = getSupportActionBar();
        if (isLandscape()) {

            //answerarea.setOrientation(GridLayout.VERTICAL);
            setColumnCount(answerarea,2);

            centercol.setOrientation(LinearLayout.HORIZONTAL);

            if (actionBar != null) {
                actionBar.hide();
            }
        } else {
            //answerarea.setOrientation(GridLayout.VERTICAL);


            setColumnCount(answerarea,1);

            centercol.setOrientation(LinearLayout.VERTICAL);

            if (actionBar != null) {
                actionBar.show();
            }

        }

    }

    private void setColumnCount(GridLayout answerarea, int count) {
        List<View> kids = new ArrayList<>();
        for(int i = 0; i< answerarea.getChildCount(); i++) {
            kids.add(answerarea.getChildAt(i));
        }
        answerarea.removeAllViews();

        answerarea.setColumnCount(count);

        for (View k: kids) {
            k.setLayoutParams(new GridLayout.LayoutParams());
            answerarea.addView(k);
        }
    }

    @Override
    protected void onSetStatus(String text) {
        final TextView status = (TextView) findViewById(R.id.txtstatus);
        status.setText(text);

    }


    @Override
    protected void onShowLevel() {


        if (((StdLevel) getLevel()).getInputMode() == InputMode.Buttons) {
            showHint = false;
        } else {
            showHint = true;
        }
        Keyboard.hideKeys(getKeysArea());
    }

    @Override
    protected void onBeforeShowProb() {
        View problem_area = findViewById(R.id.problem_area);
        problem_area.setVisibility(View.INVISIBLE);
        View answer_area = findViewById(R.id.answer_area);
        answer_area.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onAfterShowProb() {
        View problem_area = findViewById(R.id.problem_area);
        problem_area.setVisibility(View.VISIBLE);
        View answer_area = findViewById(R.id.answer_area);
        answer_area.setVisibility(View.VISIBLE);
    }

    @Override
    protected void answerDone(boolean isright, String problem, String answer, String useranswer) {
        if (isright) cancelHint();
        super.answerDone(isright, problem, answer, useranswer);
    }

    protected ViewGroup getKeysArea() {
        return (ViewGroup) findViewById(R.id.keypad_area);
    }

    protected GridLayout getAnswerArea() {
        return (GridLayout) findViewById(R.id.answer_area);
    }


    protected void startHint(int difficultyFactor) {
        startHint(BASE_HINT_TIME + 1000*difficultyFactor, BASE_HINT_REPEAT_TIME + 250*difficultyFactor);

    }

    protected void startHint(int firstHintDelayMillis, int repeatHintDelaysMillis) {

        cancelHint();

        if (showHint) {
            hintTick = 0;
            hinttask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPerformHint(hintTick++);
                        }
                    });
                }
            };

            if (timer!=null) {
                timer.scheduleAtFixedRate(hinttask, firstHintDelayMillis, repeatHintDelaysMillis);
            }
        }
    }

    protected int getHintTicks() {
        return hintTick;
    }

    protected void onPerformHint(int hintTick) {
        //override to do something.
        if (hintTick==0) {
            getSoundEffects().playHighClick();
        }
    }

    protected void cancelHint() {
        if (hinttask != null) {
            hinttask.cancel();
            hinttask = null;
        }
    }
}
