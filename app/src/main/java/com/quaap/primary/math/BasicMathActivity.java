package com.quaap.primary.math;


/**
 * Created by tom on 12/15/16.
 * <p>
 * Copyright (C) 2016   Tom Kliethermes
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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import com.quaap.primary.R;
import com.quaap.primary.base.StdGameActivity;
import com.quaap.primary.base.SubjectBaseActivity;
import com.quaap.primary.base.component.InputMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class BasicMathActivity extends StdGameActivity implements SubjectBaseActivity.AnswerGivenListener<Integer>, SubjectBaseActivity.AnswerTypedListener {

    private int num1;
    private int num2;
    private MathOp op;
    private int answer;

    public BasicMathActivity() {
        super(R.layout.std_math_prob);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {
        if (op!=null) {
            saveLevelValue("num1", num1);
            saveLevelValue("num2", num2);
            saveLevelValue("op", op.name());
        }
        super.onPause();
    }



    @Override
    protected void onShowProbImpl() {

        TextView txtMathHint = (TextView)findViewById(R.id.txtMathHint);
        txtMathHint.setText("");

        TextView num1txt = (TextView) findViewById(R.id.num1);
        TextView num2txt = (TextView) findViewById(R.id.num2);
        TextView optxt = (TextView) findViewById(R.id.op);

        num1 = getSavedLevelValue("num1", Integer.MIN_VALUE);
        num2 = getSavedLevelValue("num2", Integer.MIN_VALUE);
        op = MathOp.valueOf(getSavedLevelValue("op", "Plus"));
        if (num1 == Integer.MIN_VALUE || num2 == Integer.MIN_VALUE) {
            makeRandomProblem();
        } else {
            deleteSavedLevelValue("num1");
            deleteSavedLevelValue("num2");
            deleteSavedLevelValue("op");
        }

        num1txt.setText(String.format(Locale.getDefault(), "%d", num1));
        num2txt.setText(String.format(Locale.getDefault(), "%d", num2));
        optxt.setText(op.toString());
        answer = getAnswer(num1, num2, op);

        // LinearLayout answerarea = (LinearLayout)findViewById(R.id.answer_area);
        float fontsize = num1txt.getTextSize();
        BasicMathLevel level = (BasicMathLevel) getLevel();

        int fac = Math.max(3, (int)Math.sqrt(level.getMaxNum() + (op.ordinal()*2)));

        if (level.getInputMode() == InputMode.Buttons) {
            setFasttimes(fac * 300, fac * 400, fac * 600);
            makeAnswerButtons(getAnswerArea(), fontsize);
        } else if (level.getInputMode() == InputMode.Input) {
            setFasttimes(fac * 400, fac * 600, fac * 800);
            makeInputBox(getAnswerArea(), getKeysArea(), this, INPUTTYPE_NUMBER, 3, fontsize / 2);
            startHint(op.ordinal() + 1);

        } else {
            throw new IllegalArgumentException("Unknown inputMode! " + level.getInputMode());
        }


    }


    @Override
    protected void onPerformHint(int hintTick) {
        String a = answer+"";
        if (hintTick <a.length()){
            TextView txtMathHint = (TextView)findViewById(R.id.txtMathHint);
            txtMathHint.setText(a.substring(0, hintTick+1));
        }
        super.onPerformHint(hintTick);
    }

    private int getAnswer(int n1, int n2, MathOp op) {
        switch (op) {
            case Plus:
                return n1 + n2;
            case Minus:
                return n1 - n2;
            case Times:
                return n1 * n2;
            case Divide:
                return n1 / n2;
            default:
                throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }


    @Override
    public boolean onAnswerTyped(String answer) {
        int ans;
        try {
            ans = Integer.parseInt(answer);
        } catch (NumberFormatException ne) {
            return false;
        }
        return onAnswerGiven(ans);
    }


    @Override
    public boolean onAnswerGiven(Integer ans) {

        boolean isright = ans == answer;

        answerDone(isright, num1 + op.toString() + num2, answer + "", ans + "");

        return isright;

    }

    /**
     *  Calculate the points from the current problem
     *  By default this is based on simply the level number.
     *
     *  Override this in each activity.
     *
     *  Range, in general:
     *    difficulty 1:   1 - 50
     *    difficulty 2:   up to 100
     *    difficulty 3:   up to 200
     *    difficulty 4:   up to 500
     *    difficulty 5:   up to 1000
     *
     * @return the points for the current problem
     */
    @Override
    protected int onCalculatePoints() {
        float scoremult = (answer+"").length() - getHintTicks();

        if (scoremult<=0) {  //if the hint is fully shown, give partial credit.
            scoremult=.3f;
        }

        return super.onCalculatePoints() + (int)((1 + Math.abs(num1) + Math.abs(num2)) * (op.ordinal() + 1) * scoremult);
    }

    private void makeRandomProblem() {

        int tries = 0;
        BasicMathLevel level = (BasicMathLevel) getLevel();
        boolean negsallowed = level.getNegatives() != Negatives.None;
        do {
            int min = 0;
            int max = level.getMaxNum();
            if (negsallowed) {
                min = -max;
            }

            op = MathOp.random(level.getMinMathOp(), level.getMaxMathOp());

            if (correct > level.getRounds() / 2) { //increase difficulty in second half of level
                if (negsallowed) {
                    num1 = Math.random() > 5 ? getRand(max / 2 - 1, max) : getRand(min, min / 2 + 1);
                } else {
                    num1 = getRand(max / 2 - 1, max);
                }
            } else {
                num1 = getRand(min / 2, max / 2 + 1);
            }
            if (level.getDoubles()) {
                if (num1==0) {
                    num1 =  getRand(1, max);
                }
                num2 = num1;
            } else {
                num2 = getRand(min, max);
                if ((num2 == 0 || num2 == 1) && Math.random() > .3)
                    num2 = getRand(2, max); //reduce number of x+0 and x+1

                if (level.getNegatives() == Negatives.Required && num1 >= 0 && num2 >= 0) { //force a negative value
                    num1 = -getRand(1, max);
                }

            }
            if ((op == MathOp.Minus && level.getNegatives() == Negatives.None) || op == MathOp.Divide) {
                if (num1 < num2) {
                    int tmp = num1;
                    num1 = num2;
                    num2 = tmp;
                }
                if (op == MathOp.Divide) {
                    if (num2 == 0) {
                        num2 = getRand(1, max);
                        if (num1 < num2) num1 = getRand(num2, max);
                    }
                   // if (num1 % num2 != 0) {
                        num1 = num1 * num2;
                   // }
                }
            }
            if (op == MathOp.Minus) {
                num1 = num1 + num2;
            }
            //prevent 2 identical problems in a row
        } while (tries++ < 50 && seenProblem(num1, num2, op));


    }

    private List<Integer> getAnswerChoices(int numans) {
        List<Integer> answers = new ArrayList<>();
        answers.add(answer);

        boolean allownegs = ((BasicMathLevel) getLevel()).getNegatives() != Negatives.None;
        int range = Math.abs(answer / 10);
        for (int i = 1; i < numans; i++) {
            int tmpans;
            do {

                if (allownegs) {

                    if (getRand(10) > 8) {
                        tmpans = Math.abs(num1) + Math.abs(num2);
                    } else if (getRand(10) > 5) {
                        tmpans = -answer;
                    } else {
                        tmpans = answer + getRand(-3 - range, 3 + range);
                    }

                } else if (getRand(10) > 3) {
                    tmpans = answer + getRand(-3 - range, 3 + range); //normal random
                } else {
                    //tricky answers
                    switch (op) {
                        case Plus:
                            tmpans = Math.max(num1, num2) - Math.min(num1, num2);
                            break;
                        case Times:
                            tmpans = num1 * (num2 + getRand(1, 2));
                            break;
                        case Divide:
                            tmpans = num1 - num2;
                            break;
                        case Minus:
                        default:
                            tmpans = num1 + num2;
                            break;
                    }
                }

            } while (answers.contains(tmpans) || (!allownegs && tmpans < 0));
            answers.add(tmpans);
        }
        Collections.shuffle(answers);
        return answers;
    }


    @SuppressLint("RtlHardcoded")
    private void makeAnswerButtons(GridLayout answerarea, float fontsize) {

        List<Integer> answers = getAnswerChoices(4);



        makeChoiceButtons(answerarea, answers, this, fontsize, null, Gravity.RIGHT);

    }


}

