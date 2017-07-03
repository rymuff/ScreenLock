package com.kweisa.screenlock;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.List;

import me.zhanghai.android.patternlock.ConfirmPatternActivity;
import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;

public class MyConfirmPatternActivity extends ConfirmPatternActivity {
    private final String TAG = "ConfirmPatternActivity";
    private MySQLiteOpenHelper sqLiteOpenHelper;
    private long prepareTime;
    private long startTime;

    @Override
    public void onBackPressed() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Warning")
//                .setMessage("If you bypass Screen Lock, the test is finish immediately.")
//                .setCancelable(false)
//                .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .setNegativeButton("Finish the test anyway", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
//                    }
//                });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLeftButton.setEnabled(false);
        mRightButton.setEnabled(false);
        sqLiteOpenHelper = new MySQLiteOpenHelper(getApplicationContext());

        if (getSharedPreferences("default", MODE_PRIVATE).getBoolean("fail", false)) {
            stopService(new Intent(MyConfirmPatternActivity.this, ScreenOffService.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        prepareTime = System.currentTimeMillis();
        super.onResume();
    }

    @Override
    protected boolean isStealthModeEnabled() {
        return false;
    }

    @Override
    public void onPatternStart() {
        startTime = System.currentTimeMillis();
        super.onPatternStart();
    }

    @Override
    public void onPatternDetected(List<PatternView.Cell> pattern) {
        super.onPatternDetected(pattern);
    }

    @Override
    protected void onWrongPattern() {
        SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int wrongPatternCount = sharedPreferences.getInt("wrong_pattern_count", 0);
        editor.putInt("wrong_pattern_count", ++wrongPatternCount);
        editor.apply();

        super.onWrongPattern();

        if (wrongPatternCount >= 5) {
            editor.putBoolean("fail", true);
            editor.apply();
            stopService(new Intent(MyConfirmPatternActivity.this, ScreenOffService.class));
            finish();
        }
        mMessageText.append("\nWrong Count: " + wrongPatternCount + " / 5");
    }

    @Override
    protected void onConfirmed() {
        SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("wrong_pattern_count", 0);
        editor.apply();

        super.onConfirmed();
    }

    @Override
    protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        String patternSha1 = sharedPreferences.getString("pattern_sha1", null);
        boolean isCorrect = TextUtils.equals(PatternUtils.patternToSha1String(pattern), patternSha1);

        String patternString = "";
        for (PatternView.Cell cell : pattern) {
            patternString += (cell.getRow() * 3 + cell.getColumn() + 1);
        }

        sqLiteOpenHelper.insert(prepareTime, startTime, System.currentTimeMillis(), ""+isCorrect, patternString);
        prepareTime = System.currentTimeMillis();

        return isCorrect;
    }

    @Override
    protected void onUserLeaveHint() {
        sqLiteOpenHelper.insert(System.currentTimeMillis(), 0, 0, "Home", "HOME");
        Toast.makeText(MyConfirmPatternActivity.this, "Do not press home key", Toast.LENGTH_SHORT).show();
        finish();
        super.onUserLeaveHint();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onForgotPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Pattern")
                .setMessage("If you reset the pattern, the test will reset also.")
                .setCancelable(false)
                .setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Reset the test anyway", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSharedPreferences("default", MODE_PRIVATE).edit().clear().apply();
                        getSharedPreferences("drawable", MODE_PRIVATE).edit().clear().apply();

                        startActivity(new Intent(MyConfirmPatternActivity.this, MainActivity.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
