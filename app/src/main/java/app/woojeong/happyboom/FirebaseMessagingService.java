package app.woojeong.happyboom;


import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.net.URL;

import static app.woojeong.happyboom.GlobalApplication.applicationLifecycleHandler;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ("1".equals(msg.obj.toString().trim())) {
                Toast.makeText(FirebaseMessagingService.this, "다른 기기에서 로그인을 시도하여 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
            } else if ("2".equals(msg.obj.toString().trim())) {
                Toast.makeText(FirebaseMessagingService.this, "다른 기기에서 로그인을 시도하여 해피붐을 종료합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String text = "", type = "", link = "", image = "";
        text += remoteMessage.getData().get("text");
        type += remoteMessage.getData().get("type");
        link += remoteMessage.getData().get("link");
        image += remoteMessage.getData().get("image");

        Log.i("Firebase", "" + remoteMessage.getData());

        if ("text".equals(type)) {

            if ("community".equals(remoteMessage.getData().get("case"))) {
                if (applicationLifecycleHandler.isInBackground()) {
                    sendNotificationComment(text, remoteMessage.getData().get("key"));
                } else {
                    sendNotificationComment(text, remoteMessage.getData().get("key"));
                }
            } else if ("premium".equals(remoteMessage.getData().get("case"))) {
                if (applicationLifecycleHandler.isInBackground()) {
                    sendNotificationPremium(text, remoteMessage.getData().get("key"));
                } else {
                    sendNotificationPremium(text, remoteMessage.getData().get("key"));
                }
            } else {
                if (applicationLifecycleHandler.isInBackground()) {
                    sendNotificationText(text, link);
                } else {
                    sendNotificationText(text, link);
                }
            }


        } else if ("image".equals(type)) {
            Log.i("Firebase", "" + remoteMessage);
            if (applicationLifecycleHandler.isInBackground()) {
                sendNotificationImage(text, image, link);
            } else {
                sendNotificationImage(text, image, link);
            }
        } else if ("logout".equals(type)) {
            SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
            boolean loginChecked = pref.getBoolean("loginChecked", false);
            if (loginChecked) {
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = autoLogin.edit();
                editor2.clear();
                editor2.commit();
//                ShortcutBadger.removeCount(getApplicationContext());
                if (!applicationLifecycleHandler.isInBackground()) {
                    Message msg = handler.obtainMessage();
                    msg.obj = "1";
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(3000);
                        Intent killApp = new Intent(FirebaseMessagingService.this, MainActivity.class);
                        killApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        killApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        killApp.putExtra("KILL_APP", true);
                        startActivity(killApp);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
//                    getOtherLogin = true;
                    Message msg = handler.obtainMessage();
                    msg.obj = "2";
                    handler.sendMessage(msg);
                }
            } else {
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                SharedPreferences autoLogin = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = autoLogin.edit();
                editor2.clear();
                editor2.commit();
//                ShortcutBadger.removeCount(getApplicationContext());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotificationText(String text, String link) {
        Intent intent = null;
        if ("".equals(link)) {
            intent = new Intent(this, IntroActivity.class);
        } else {
            intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() / 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("해피붐", "해피붐", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder = new NotificationCompat.Builder(this, notificationChannel.getId());
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        notificationBuilder.setContentTitle("해피붐")
                .setSmallIcon(R.drawable.noti_icon)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);
        notificationManager.notify(1, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotificationComment(String text, String key) {
        Intent intent = null;
        Intent intentFirst = null;
        if (!"".equals(key)) {

            intent = new Intent(this, MainActivity.class);
            intent.putExtra("activity", "community");
            intent.putExtra("idx", key);
        } else {
            intent = new Intent(this, IntroActivity.class);
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() / 1000), intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("해피붐", "해피붐", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder = new NotificationCompat.Builder(this, notificationChannel.getId());
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }
        notificationBuilder.setContentTitle("해피붐")
                .setSmallIcon(R.drawable.noti_icon)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);
        notificationManager.notify(1, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotificationPremium(String text, String key) {
        Intent intent = null;
        if (!"".equals(key)) {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("activity", "company");
            intent.putExtra("idx", key);
        } else {
            intent = new Intent(this, IntroActivity.class);
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() / 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("해피붐", "해피붐", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder = new NotificationCompat.Builder(this, notificationChannel.getId());
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        notificationBuilder.setContentTitle("해피붐")
                .setSmallIcon(R.drawable.noti_icon)
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent);
        notificationManager.notify(1, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotificationImage(String text, String image, String link) {
        Intent intent = null;
        if ("".equals(link)) {
            intent = new Intent(this, IntroActivity.class);
        } else {
            intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(link));
        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() / 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = null;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel("해피붐", "해피붐", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationBuilder = new NotificationCompat.Builder(this, notificationChannel.getId());
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }
        try {
            URL url = new URL(image);
            Bitmap getBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            notificationBuilder.setContentTitle("해피붐")
                    .setContentText("아래로 드래그하여 알림을 확인해주세요.")
                    .setSmallIcon(R.drawable.noti_icon)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setLights(Color.RED, 3000, 3000)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmap).setBigContentTitle("해피붐").setSummaryText(text))
                    .setContentIntent(pendingIntent);
            notificationManager.notify(1, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
