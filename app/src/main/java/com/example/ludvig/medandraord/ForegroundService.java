package com.example.ludvig.medandraord;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

public class ForegroundService extends Service {
    final int ONGOING_NOTIFICATION_ID = 1;

    MyBinder binder;

    @Override
    public void onCreate() {
        System.out.println("onCreate in service");
        startForeground(ONGOING_NOTIFICATION_ID, getNotification(""));

        if (binder == null) {
            binder = new MyBinder();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onstart");
        return START_STICKY;
    }


    @TargetApi(23)
    private Notification getNotification(String text) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(false)
                .setAutoCancel(false)
                .setContentTitle("Med Andra Ord - Game i progress!")
                .setSmallIcon(R.drawable.thirdicon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.thirdicon));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            builder.setColor(getResources().getColor(R.color.colorBack, null));
        }

        if (text != null) {
            builder.setContentText(text);
        }

        Intent notificationIntent = new Intent(this, InGameHomeScreen.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("from", "service");
        notificationIntent.setAction("action");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //http://www.truiton.com/2014/10/android-foreground-service-example/
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    public void updateNotification(String text) {
        Notification notification = getNotification(text);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ONGOING_NOTIFICATION_ID, notification);

    }

    class MyBinder extends Binder {
        ForegroundService getService() {
            return ForegroundService.this;
        }
    }

}

