package com.kweisa.screenlock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ScreenOffService extends Service {
    private ScreenOffBroadcastReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (receiver != null) {
            unregisterReceiver(receiver);
            stopForeground(true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        receiver = new ScreenOffBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setContentTitle("Screen Lock")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.build();
        startForeground(1, notification);

        if (intent != null && intent.getAction() == null && receiver == null) {
            receiver = new ScreenOffBroadcastReceiver();
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            registerReceiver(receiver, intentFilter);
        }

        return START_REDELIVER_INTENT;
    }


}
