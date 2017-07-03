package com.kweisa.screenlock;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UploadActivity extends AppCompatActivity {
    private final String TAG = "UploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        TextView start = (TextView) findViewById(R.id.start);
        TextView current = (TextView) findViewById(R.id.current);
        TextView finish = (TextView) findViewById(R.id.finish);
        TextView userId = (TextView) findViewById(R.id.user_id_tv);

        userId.setText("User ID: " + getSharedPreferences("default", MODE_PRIVATE).getInt("user_id", 0));

        long startTime = getSharedPreferences("default", MODE_PRIVATE).getLong("start_time", 0);
        long finishTime = startTime + 24 * 60 * 60 * 1000;
        long currentTime = System.currentTimeMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREA);
        start.setText("Start in: " + simpleDateFormat.format(new Date(startTime)));
        current.setText("Current is: " + simpleDateFormat.format(new Date(currentTime)));
        finish.setText("Finished at: " + simpleDateFormat.format(new Date(finishTime)));

        Button upload = (Button) findViewById(R.id.upload);
        Button reset = (Button) findViewById(R.id.reset);

        upload.setEnabled(true); // TODO: remove this line

        if (currentTime >= finishTime || getSharedPreferences("default", MODE_PRIVATE).getBoolean("fail", false)) {
            upload.setEnabled(true);
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(UploadActivity.this, ScreenOffService.class));
                sendFiles();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("default", MODE_PRIVATE).edit().clear().apply();
                getSharedPreferences("drawable", MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(UploadActivity.this, MainActivity.class));
                finish();
            }
        });
        reset.setEnabled(false);
    }

    public void sendFiles() {
        if (!checkPermission()) {
            requestPermission();
        } else {
            int userId = getSharedPreferences("default", MODE_PRIVATE).getInt("user_id", 0);
            File internalDatabase = getDatabasePath("pattern.db");
            File internalSharedPref = new File("/data/data/com.kweisa.screenlock/shared_prefs/default.xml");
            File externalDatabase = new File(Environment.getExternalStorageDirectory(), "pattern_" + userId + ".db");
            File externalSharedPref = new File(Environment.getExternalStorageDirectory(), "default_" + userId + ".xml");

            try {
                FileChannel source = new FileInputStream(internalDatabase).getChannel();
                FileChannel destination = new FileOutputStream(externalDatabase).getChannel();
                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();

                source = new FileInputStream(internalSharedPref).getChannel();
                destination = new FileOutputStream(externalSharedPref).getChannel();

                destination.transferFrom(source, 0, source.size());
                source.close();
                destination.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(Uri.fromFile(externalDatabase));
            uris.add(Uri.fromFile(externalSharedPref));

            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            emailIntent.setType("text/plain");
            String to[] = {"geumhwancho@gmail.com", "rymuff@gmail.com"}; // TODO: make ID List
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            // the mail subject
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Syspal] Data from User ID: " + userId);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Your name: \nBank name: \nAccount Number: ");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }

    public boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
    }

}
