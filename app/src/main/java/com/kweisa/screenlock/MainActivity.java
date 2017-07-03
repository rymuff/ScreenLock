package com.kweisa.screenlock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("init", false)) {
            startActivity(new Intent(MainActivity.this, UploadActivity.class));
            finish();
        }

        buildDrawableList();

        final EditText editText = (EditText) findViewById(R.id.user_id);
        Button button = (Button) findViewById(R.id.start);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int userId = Integer.parseInt(editText.getText().toString());
                Log.d(TAG, "ID: " + userId);
                SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("user_id", userId);
                editor.apply();

                startActivity(new Intent(MainActivity.this, MySetPatternActivity.class));
                finish();
            }
        });
    }

    private void buildDrawableList() {
        SharedPreferences sharedPreferences = getSharedPreferences("drawable", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("pattern1", R.drawable.pattern1);
        editor.putInt("pattern2", R.drawable.pattern2);
        editor.putInt("pattern3", R.drawable.pattern3);
        editor.putInt("pattern4", R.drawable.pattern4);
        editor.putInt("pattern5", R.drawable.pattern5);
        editor.putInt("pattern6", R.drawable.pattern6);
        editor.putInt("pattern7", R.drawable.pattern7);
        editor.putInt("pattern8", R.drawable.pattern8);
        editor.putInt("pattern9", R.drawable.pattern9);
        editor.putInt("pattern12", R.drawable.pattern12);
        editor.putInt("pattern13", R.drawable.pattern13);
        editor.putInt("pattern14", R.drawable.pattern14);
        editor.putInt("pattern15", R.drawable.pattern15);
        editor.putInt("pattern16", R.drawable.pattern16);
        editor.putInt("pattern17", R.drawable.pattern17);
        editor.putInt("pattern18", R.drawable.pattern18);
        editor.putInt("pattern19", R.drawable.pattern19);
        editor.putInt("pattern23", R.drawable.pattern23);
        editor.putInt("pattern24", R.drawable.pattern24);
        editor.putInt("pattern25", R.drawable.pattern25);
        editor.putInt("pattern26", R.drawable.pattern26);
        editor.putInt("pattern27", R.drawable.pattern27);
        editor.putInt("pattern28", R.drawable.pattern28);
        editor.putInt("pattern29", R.drawable.pattern29);
        editor.putInt("pattern34", R.drawable.pattern34);
        editor.putInt("pattern35", R.drawable.pattern35);
        editor.putInt("pattern36", R.drawable.pattern36);
        editor.putInt("pattern37", R.drawable.pattern37);
        editor.putInt("pattern38", R.drawable.pattern38);
        editor.putInt("pattern39", R.drawable.pattern39);
        editor.putInt("pattern45", R.drawable.pattern45);
        editor.putInt("pattern46", R.drawable.pattern46);
        editor.putInt("pattern47", R.drawable.pattern47);
        editor.putInt("pattern48", R.drawable.pattern48);
        editor.putInt("pattern49", R.drawable.pattern49);
        editor.putInt("pattern56", R.drawable.pattern56);
        editor.putInt("pattern57", R.drawable.pattern57);
        editor.putInt("pattern58", R.drawable.pattern58);
        editor.putInt("pattern59", R.drawable.pattern59);
        editor.putInt("pattern67", R.drawable.pattern67);
        editor.putInt("pattern68", R.drawable.pattern68);
        editor.putInt("pattern69", R.drawable.pattern69);
        editor.putInt("pattern78", R.drawable.pattern78);
        editor.putInt("pattern79", R.drawable.pattern79);
        editor.putInt("pattern89", R.drawable.pattern89);
        editor.apply();
    }
}
