package com.example.ludvig.medandraord;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class ForegroundService extends Service {
    final int ONGOING_NOTIFICATION_ID = 1;

    MyBinder binder;

    @Override
    public void onCreate() {
        System.out.println("onCreate in service");
        startForeground(ONGOING_NOTIFICATION_ID, getNotification());

        if(binder == null) {
            binder = new MyBinder();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println(intent.getAction());
        System.out.println("onstart");
        return START_STICKY;
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(false)
                .setAutoCancel(false)
                .setContentText("Med Andra Ord")
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent notificationIntent = new Intent(this, InGameHomeScreen.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("from", "service");
        notificationIntent.setAction("action");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //http://www.truiton.com/2014/10/android-foreground-service-example/
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    public class MyBinder extends Binder {
        ForegroundService getService() {
            return ForegroundService.this;
        }
    }

    public void stopService() {
        stopForeground(true);
    }
}

