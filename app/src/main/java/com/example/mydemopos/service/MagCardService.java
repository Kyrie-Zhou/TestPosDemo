package com.example.mydemopos.service;

import static android.content.ContentValues.TAG;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;

import com.example.mydemopos.R;
import com.example.mydemopos.activity.MainActivity;
import com.pax.api.MagException;
import com.pax.api.MagManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MagCardService extends Service {
    MagManager magManager = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: " );
        super.onCreate();
        startForeground(1,getNotification("磁条卡服务","磁条卡正在检测信息"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取磁条卡信息
        new Thread(() -> {
            try {
                magManager = MagManager.getInstance();
                magManager.magOpen();
            } catch (MagException e) {
                e.printStackTrace();
                Log.e("magMsg","磁条卡出问题！");
            }
            String ver = "";
            boolean isSwip = false;
            String info = "";
            Intent intent1 = new Intent("com.pax.intent.action.magSwip");
            while (true){
                try {
                    ver = magManager.magGetVersion();
                    isSwip = magManager.magSwiped();
                    Log.e(TAG, "ver and isSwip are" + ver + " " + isSwip);
                    if(magManager.magSwiped()){
                        info = magManager.magRead().track2;
                        intent1.putExtra("info", info);
                        Log.e("info", info + " " +ver );
                        sendBroadcast(intent1);
                        magManager.magClose();//及时关闭资源
                        break;
                    }
                    //检测到之后，立马跳出循环，不再执行
                } catch (MagException e) {
                    Log.e(TAG, "请刷卡");
                }
            }
        }).start();
        Log.e("startcommand", "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @MainThread
    public Notification getNotification(String title,String message){
        String id = "my_channel_01";
        String name="my_channel_name";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        Notification.Builder builder = new Notification.Builder(this);//创建builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = builder
                    .setChannelId(id)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.main_mag).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.main_mag)
                    .setOngoing(true);
            notification = notificationBuilder.build();
        }
        return notification;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("unbind-service","onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("destory-service", "onDestroy: " );
    }

}
