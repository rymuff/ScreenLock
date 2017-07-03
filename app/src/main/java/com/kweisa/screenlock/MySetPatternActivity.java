package com.kweisa.screenlock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Random;

import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;
import me.zhanghai.android.patternlock.SetPatternActivity;

public class MySetPatternActivity extends SetPatternActivity {
    private final String TAG = "SetPatternActivity";

    private MySQLiteOpenHelper mySQLiteOpenHelper;

    private long prepareTime;
    private long startTime;
    private long endTime;

    private int policy;
    private int point1;
    private int point2;

    @Override
    public void onPatternCleared() {
        prepareTime = System.currentTimeMillis();
        super.onPatternCleared();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mySQLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int userId = sharedPreferences.getInt("user_id", 0);
        policy = userId % 3;
        editor.putInt("policy", policy);

        Log.d(TAG, "user_id: " + getSharedPreferences("default", MODE_PRIVATE).getInt("user_id", 0));
        Log.d(TAG, "policy: " + policy);

        if (policy == 1) {
            point1 = getRandom(9);
            editor.putInt("point1", point1);
            mPatternView.setBackground(getResources().getDrawable(getSharedPreferences("drawable", MODE_PRIVATE).getInt("pattern" + point1, 0)));

            Log.d(TAG, "point1: " + point1);
        } else if (policy == 2) {
            point1 = getRandom(9);
            point2 = getRandom(9);
            while (point1 == point2 || point1 > point2) {
                point1 = getRandom(9);
                point2 = getRandom(9);
            }
            editor.putInt("point1", point1);
            editor.putInt("point2", point2);
            mPatternView.setBackground(getResources().getDrawable(getSharedPreferences("drawable", MODE_PRIVATE).getInt("pattern" + point1 + point2, 0)));
            Log.d(TAG, "point1: " + point1);
            Log.d(TAG, "point2: " + point2);
        }
        editor.apply();

        prepareTime = System.currentTimeMillis();
    }

    @Override
    public void onPatternDetected(List<PatternView.Cell> newPattern) {
        endTime = System.currentTimeMillis();

        String pattern = "";
        for (PatternView.Cell cell : newPattern) {
            pattern += (cell.getRow() * 3 + cell.getColumn() + 1);
        }
        mySQLiteOpenHelper.insert(prepareTime, startTime, endTime, TAG, pattern);
        Log.d(TAG, "onPatternDetected: " + pattern);

        if (policy == 1 && !pattern.contains(String.valueOf(point1))) {
            mPatternView.clearPattern();
            mMessageText.setText("Please pass point " + point1);
        } else if (policy == 2 && (!pattern.contains(String.valueOf(point1)) || !pattern.contains(String.valueOf(point2)))) {
            mPatternView.clearPattern();
            mMessageText.setText("Please pass point " + point1 + " and point " + point2);
        } else {
            super.onPatternDetected(newPattern);
        }
    }

    @Override
    public void onPatternStart() {
        startTime = System.currentTimeMillis();
        super.onPatternStart();
    }

    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        Log.d(TAG, "onSetPattern");

        String patternString = "";
        for (PatternView.Cell cell : pattern) {
            patternString += (cell.getRow() * 3 + cell.getColumn() + 1);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("pattern_sha1", PatternUtils.patternToSha1String(pattern));
        editor.putString("pattern_string", patternString);
        editor.apply();
    }

    @Override

    protected void onConfirmed() {
        SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("init", true);

        long startTime = System.currentTimeMillis();
        editor.putLong("start_time", startTime);
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), ScreenOffService.class);
        startService(intent);

        super.onConfirmed();
    }

    public int getRandom(int max) {
        return new Random().nextInt(max) + 1;
    }
}
