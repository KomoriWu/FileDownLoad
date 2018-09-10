package com.komori.filedownload;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by KomoriWu
 * on 2018/9/7.
 */

public class Utils {
    public static final int NOTIFICATION_ID = 0x1;
    public static final String ID = "download";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initNotificationChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("chat",
                "Channel1"));
        NotificationChannel channel = new NotificationChannel(ID,
                "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
//        channel.enableLights(true);
//        channel.setLightColor(Color.GREEN);
//        channel.setShowBadge(true);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{0});
        notificationManager.createNotificationChannel(channel);
    }
    /**
     * 显示一个下载带进度条的通知
     *
     * @param context 上下文
     */
    public static void showNotificationProgress(Context context) {
        //进度条通知
        final NotificationCompat.Builder builderProgress = new NotificationCompat.Builder(context);
        builderProgress.setContentTitle("下载中");
        builderProgress.setSmallIcon(R.mipmap.ic_launcher);
        builderProgress.setTicker("进度条通知");
        builderProgress.setProgress(100, 0, false);
        final Notification notification = builderProgress.build();
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //发送一个通知
        notificationManager.notify(2, notification);
        /**创建一个计时器,模拟下载进度**/
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int progress = 0;

            @Override
            public void run() {
                Log.i("progress", progress + "");
                while (progress <= 100) {
                    progress++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //更新进度条
                    builderProgress.setProgress(100, progress, false);
                    //再次通知
                    notificationManager.notify(2, builderProgress.build());
                }
                //计时器退出
                this.cancel();
                //进度条退出
                notificationManager.cancel(2);
                return;//结束方法
            }
        }, 0);
    }


}
