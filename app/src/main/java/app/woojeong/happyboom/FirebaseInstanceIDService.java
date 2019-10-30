package app.woojeong.happyboom;


import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i("FCM", "token: " + token);
        SharedPreferences prefToken = FirebaseInstanceIDService.this.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefToken.edit();
        editor.clear();
        editor.putString("Token", token);
        editor.commit();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("Token", token)
//                .add("package","package_name")
//                .build();
//
//        Request request = new Request.Builder()
//                .url("http://push.globalhumanism.kr/push/?type=action&value=reg")
//                .post(body)
//                .build();
//
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}


