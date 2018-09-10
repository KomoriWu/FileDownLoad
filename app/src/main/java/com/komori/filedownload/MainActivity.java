package com.komori.filedownload;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.text.NumberFormat;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private String url = "http://goodpay.oss-cn-shenzhen.aliyuncs.com/test/HaoFu_Test_8.1.7.apk";
    private String name = "test6.apk";
    private String path = getExternalStorageDirectory() + "/" + name;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    private Bitmap btm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);//可以换成你的app的logo

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileDownloader.setup(this);
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Utils.initNotificationChannel(MainActivity.this);
                            showChannelNotification(MainActivity.this, TITLE, CONTENT);
                        } else {
                            initNotification();
                        }
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.e(TAG, "progress:" + (soFarBytes * 100 / totalBytes) + "%");// 打印计算结果
                        int progress = soFarBytes * 100 / totalBytes;
                        builder.setProgress(100, progress, false);
                        builder.setContentText("下载" + progress + "%");
                        notificationManager.notify(Utils.NOTIFICATION_ID, builder.build());

                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        Log.e(TAG, "blockComplete:");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        Log.e(TAG, "completed:");
                        notificationManager.cancel(Utils.NOTIFICATION_ID);
                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, "com.komori.filedownload",
                                    new File(path));
                            intent1.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        } else {
                            intent1.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                        }
                        startActivity(intent1);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    Notification.Builder builder;
    NotificationManager notificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showChannelNotification(Context context, String title, String content) {
        notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context, Utils.ID);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(btm)
                .setContentText(content)
                .setNumber(3)
                .setContentTitle(title)
                .setVibrate(new long[]{0})
                .setProgress(100, 0, false)
                .build();
        notificationManager.notify(Utils.NOTIFICATION_ID, builder.build());
    }


    //初始化通知
    private void initNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher_round)//可以换成你的app的logo
                .setLargeIcon(btm)
                .setContentText(CONTENT)
                .setContentTitle(TITLE)
                .setProgress(100, 0, false)
                .build();
        notificationManager.notify(Utils.NOTIFICATION_ID, builder.build());
    }
}
