package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    ProgressDialog progressDialog;

    FrameLayout login_con;
    ImageView naver_icon, kakao_icon, facebook_icon;
    EditText id, password;
    TextView login, find_id, find_password, join, naver_text, kakao_text, facebook_text;

    JSONObject jsonObject;

    //facebook
    CallbackManager callbackManager;

    //kakao
    SessionCallback mKakaocallback;

    //naver
    public static OAuthLogin naver_original_btn;
    String accessToken;
    static LoginActivity activity;
    boolean naver = true;

    SharedPreferences get_token;
    String getToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(LoginActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getAppKeyHash(); // KFD5l6Ah87n5iJONet9MhUoiKos=

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");
        Log.i(TAG, " getToken " + getToken);

        login_con = findViewById(R.id.login_con);

        naver_icon = findViewById(R.id.naver_icon);
        kakao_icon = findViewById(R.id.kakao_icon);
        facebook_icon = findViewById(R.id.facebook_icon);

        id = findViewById(R.id.id);
        password = findViewById(R.id.password);

        login = findViewById(R.id.login);
        find_id = findViewById(R.id.find_id);
        find_password = findViewById(R.id.find_password);
        join = findViewById(R.id.join);
        naver_text = findViewById(R.id.naver_text);
        kakao_text = findViewById(R.id.kakao_text);
        facebook_text = findViewById(R.id.facebook_text);

        login_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.callOnClick();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(id.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "아이디를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if ("".equals(password.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "비밀번호를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();

                loginNormal();
            }
        });

        find_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindID1Activity.class);
                startActivity(intent);
            }
        });

        find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindPassword1Activity.class);
                startActivity(intent);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                intent.putExtra("type", "normal");
                startActivity(intent);
            }
        });

        naver_original_btn = OAuthLogin.getInstance();
        naver_original_btn.init(
                LoginActivity.this,
                "sHMpJAjVkMW2qGMwn3oT",
                "1FkIGG6VCY",
                "해피붐"
        );

        naver_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naver_icon.callOnClick();
            }
        });
        naver_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (naver) {
                    naver_original_btn.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
                    naver = false;
                }
            }
        });

        kakao_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakao_icon.callOnClick();
            }
        });
        kakao_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.requestLogout(new LogoutResponseCallback() { //로그아웃
                    @Override
                    public void onCompleteLogout() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mKakaocallback = new SessionCallback();
                                        com.kakao.auth.Session.getCurrentSession().removeCallback(mKakaocallback);
                                        com.kakao.auth.Session.getCurrentSession().addCallback(mKakaocallback);
                                        LoginButton loginButton = (LoginButton) findViewById(R.id.kakaotalk_original_btn);
                                        loginButton.callOnClick();
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        });

        facebook_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebook_icon.callOnClick();
            }
        });
        facebook_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFacebookLogin();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mKakaocallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Session.getCurrentSession().removeCallback(mKakaocallback);
            mKakaocallback = new SessionCallback();
            Session.getCurrentSession().addCallback(mKakaocallback);
            return;
        }
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        switch (resultCode) {
            case RESULT_CANCELED:
                break;
            case 999:
                setResult(999);
                finish();
                break;
        }
    }

    //로그인 (일반회원)
    void loginNormal() {
        final String url = ServerUrl.getBaseUrl() + "/login/pass";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id.getText().toString());
        params.put("pass", password.getText().toString());
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(LoginActivity.this, "다시 시도해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "Hash Key : " + something);
            }
        } catch (Exception e) {
            Log.i(TAG, "name not found" + e.toString());
        }
    }

    //FaceBook
    public void isFacebookLogin() {
        Log.i(TAG, " isFacebookLogin()");
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {
                Log.i(TAG, " result " + result);
                GraphRequest request;
                request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        Log.i(TAG, " response " + response);
                        if (response.getError() != null) {

                        } else {
//                            try {
                            Log.i(TAG, "isLogin : OK");
                            Log.i(TAG, "AccessToken: " + result.getAccessToken().getToken());
                            Log.i(TAG, "user: " + user.toString());
                            setResult(RESULT_OK);

//                                jsonObject = new JSONObject(user.toString());
//                                SharedPreferences facebookData = LoginActivity.this.getSharedPreferences("facebookData", Activity.MODE_PRIVATE);
//                                SharedPreferences.Editor editor = facebookData.edit();
//                                editor.clear();
//                                editor.putString("id", jsonObject.getString("id")+"");
//                                editor.putString("name", jsonObject.getString("name")+"");
//                                editor.putString("gender", jsonObject.getString("gender")+"");
//                                editor.commit();

                            get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
                            String token = get_token.getString("Token", "");
                            String url = ServerUrl.getBaseUrl() + "/login/facebook";
                            Map<String, Object> params = new HashMap<String, Object>();
                            params.put("token", result.getAccessToken().getToken());
                            Log.i(TAG, " params " + result.getAccessToken().getToken());
                            Log.i(TAG, " token " + token);
                            aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
                                @Override
                                public void callback(String url, String jsonString, AjaxStatus status) {
                                    Log.i(TAG, " jsonString " + jsonString);
                                    try {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.getBoolean("return")) {    //return이 true 면?
                                            loginNormal();
                                        } else if (!jsonObject.getBoolean("return")) {
                                            if ("detail".equals(jsonObject.getString("type")) || "phone".equals(jsonObject.getString("type"))) {
                                                Toast.makeText(LoginActivity.this, "회원 가입 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                                                intent.putExtra("type", "sns");
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(LoginActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.header("epoch-agent", token).header("User-Agent", "android"));

//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "오류로 페이스북 로그인 실패 " + error);
            }

            @Override
            public void onCancel() {
                Log.i(TAG, " onCancel");
            }
        });
    }

    // Naver
    private OAuthLoginHandler mOAuthLoginHandler = new NaverLoginHandler(this);

    private class NaverLoginHandler extends OAuthLoginHandler {

        private final WeakReference<LoginActivity> mActivity;

        public NaverLoginHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void run(boolean success) {
            activity = mActivity.get();

            if (success) {
                accessToken = naver_original_btn.getAccessToken(activity);
                String refreshToken = naver_original_btn.getRefreshToken(activity);
                long expiresAt = naver_original_btn.getExpiresAt(activity);
                String tokenType = naver_original_btn.getTokenType(activity);
                new RequestApiTask().execute();
            } else {
                Toast.makeText(activity, "네이버 로그인을 실패 하였습니다.", Toast.LENGTH_SHORT).show();
                String errorCode = naver_original_btn.getLastErrorCode(activity).getCode();
                String errorDesc = naver_original_btn.getLastErrorDesc(activity);
                Log.i(TAG, " errorCode:" + errorCode + ", errorDesc:" + errorDesc);
                naver = true;
            }
        }
    }

    private class RequestApiTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = naver_original_btn.getAccessToken(activity);
            Pasingversiondata(naver_original_btn.requestApi(activity, at, url));
            return null;
        }

        protected void onPostExecute(Void content) {
            SharedPreferences get_token = context.getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
            final String getToken = get_token.getString("Token", "");
            String url = ServerUrl.getBaseUrl() + "/login/naver";
            final Map<String, Object> params = new HashMap<>();
            params.put("token", accessToken);
            Log.i(TAG, " params " + params);
            aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                    Log.i(TAG, " jsonObject " + jsonObject);
                    try {

                        if (jsonObject.getBoolean("return")) {
//                            SharedPreferences prefLoginChecked = getSharedPreferences("prefLoginChecked", Activity.MODE_PRIVATE);
//                            SharedPreferences.Editor editor2 = prefLoginChecked.edit();
//                            editor2.clear();
//                            editor2.putString("type", "naver");
//                            editor2.commit();
                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (!jsonObject.getBoolean("return")) {
//                            if ("detail".equals(jsonObject.getString("type"))) {
//                                //추가 정보 입력
//                                AccessToken.createEmptyToken();
//
//                                OAuthLogin mOAuthLoginInstance;
//                                mOAuthLoginInstance = OAuthLogin.getInstance();
//                                mOAuthLoginInstance.logout(LoginActivity.this);
//
//                                UserManagement.requestLogout(new LogoutResponseCallback() {
//                                    @Override
//                                    public void onCompleteLogout() {
//                                        //로그아웃 성공 후 하고싶은 내용 코딩 ~
//                                    }
//                                });
//                                Toast.makeText(LoginActivity.this, "회원 가입 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
////                                finish();
//                                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
////                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                startActivityForResult(intent, 1);
//                            } else
                            if ("detail".equals(jsonObject.getString("type")) || "phone".equals(jsonObject.getString("type"))) {
                                //추가 정보 입력
                                AccessToken.createEmptyToken();

                                OAuthLogin mOAuthLoginInstance;
                                mOAuthLoginInstance = OAuthLogin.getInstance();
                                mOAuthLoginInstance.logout(LoginActivity.this);

                                UserManagement.requestLogout(new LogoutResponseCallback() {
                                    @Override
                                    public void onCompleteLogout() {
                                        //로그아웃 성공 후 하고싶은 내용 코딩 ~
                                    }
                                });
                                Toast.makeText(LoginActivity.this, "회원 가입 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
//                                finish();
                                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                                intent.putExtra("type", "sns");
//                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivityForResult(intent, 1);
                            } else {
                                Toast.makeText(LoginActivity.this, "다시 시도해주세요 !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    naver = true;
                }
            }.header("epoch-agent", getToken).header("User-Agent", "android"));
        }

        private void Pasingversiondata(String data) { // xml 파싱
            Log.d("네이버로그인_xml파싱", data);
            String f_array[] = new String[9];

            try {
                XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserCreator.newPullParser();
                InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
                parser.setInput(input, "UTF-8");
                int parserEvent = parser.getEventType();
                String tag;
                boolean inText = false;
                boolean lastMatTag = false;
                int colIdx = 0;
                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG:
                            tag = parser.getName();
                            if (tag.compareTo("xml") == 0) {
                                inText = false;
                            } else if (tag.compareTo("data") == 0) {
                                inText = false;
                            } else if (tag.compareTo("result") == 0) {
                                inText = false;
                            } else if (tag.compareTo("resultcode") == 0) {
                                inText = false;
                            } else if (tag.compareTo("message") == 0) {
                                inText = false;
                            } else if (tag.compareTo("response") == 0) {
                                inText = false;
                            } else {
                                inText = true;
                            }
                            break;
                        case XmlPullParser.TEXT:
                            tag = parser.getName();
                            if (inText) {
                                if (parser.getText() == null) {
                                    f_array[colIdx] = "";

                                } else {
                                    f_array[colIdx] = parser.getText().trim();
                                }

                                colIdx++;
                            }
                            inText = false;
                            break;
                        case XmlPullParser.END_TAG:
                            tag = parser.getName();
                            inText = false;
                            break;
                    }
                    parserEvent = parser.next();
                }
            } catch (Exception e) {
                Log.e("dd", "Error in network call", e);
            }
        }
    }

    // Kakao
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            KakaorequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Session.getCurrentSession().removeCallback(mKakaocallback);
            mKakaocallback = new SessionCallback();
            Session.getCurrentSession().addCallback(mKakaocallback);
        }
    }

    protected void KakaorequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                int ErrorCode = errorResult.getErrorCode();
                int ClientErrorCode = -777;

                if (ErrorCode == ClientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다.\n잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, errorResult.toString() + " 오류로 카카오 로그인 실패");
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.i(TAG, "오류로 카카오 로그인 실패");
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                final String userName = userProfile.getNickname();
                final String userId = String.valueOf(userProfile.getId());
                final String profileUrl = userProfile.getProfileImagePath();
                Log.i(TAG, "profileUrl : " + profileUrl + " userId : " + userId + " userName : " + userName);
                Log.i(TAG, "AccessToken: " + Session.getCurrentSession().getAccessToken());

//                SharedPreferences kakaotalkData = LoginActivity.this.getSharedPreferences("kakaotalkData", Activity.MODE_PRIVATE);
//                SharedPreferences.Editor editor = kakaotalkData.edit();
//                editor.clear();
//                editor.putString("kakao", userId + "");
//                editor.putString("name", userName + "");
//                editor.putString("url", profileUrl + "");
//                editor.commit();

                String url = ServerUrl.getBaseUrl() + "/login/kakao";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("token", Session.getCurrentSession().getAccessToken());
                aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                        Log.i(TAG, " jsonObject " + jsonObject);

                        try {
                            if (jsonObject.getBoolean("return")) {
                                //로그인 성공
                                SharedPreferences prefLoginChecked = getSharedPreferences("prefLoginChecked", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = prefLoginChecked.edit();
                                editor2.clear();
                                editor2.putString("type", "kakao");
                                editor2.commit();

                                finish();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                            } else if (!jsonObject.getBoolean("return")) {

//                                if ("detail".equals(jsonObject.getString("type"))) {
//                                    //추가 정보 입력
//                                    Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
////                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                    startActivityForResult(intent, 1);
//                                } else
                                if ("detail".equals(jsonObject.getString("type")) || "phone".equals(jsonObject.getString("type"))) {
                                    //추가 정보 입력
                                    Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                                    intent.putExtra("type", "sns");
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    startActivityForResult(intent, 1);
                                } else {
                                    Toast.makeText(LoginActivity.this, "다시 시도해주세요 !", Toast.LENGTH_SHORT).show();
                                }
                            }
                            Session.getCurrentSession().removeCallback(mKakaocallback);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.header("epoch-agent", getToken).header("User-Agent", "android"));
            }

            @Override
            public void onNotSignedUp() {
            }
        });
    }

    public static String getHashKey(Context context) {
        String keyHash = null;
        try {
            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                Log.d(TAG, " - " + keyHash);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
        if (keyHash != null) {
            return keyHash;
        } else {
            return null;
        }
    }

    public class OneBtnDialog extends Dialog {
        OneBtnDialog oneBtnDialog = this;
        Context context;

        public OneBtnDialog(final Context context, final String text, final String btnText) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_one_btn);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = (TextView) findViewById(R.id.title1);
            TextView title2 = (TextView) findViewById(R.id.title2);
            TextView btn1 = (TextView) findViewById(R.id.btn1);
            title2.setVisibility(View.GONE);
            title1.setText(text);
            btn1.setText(btnText);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oneBtnDialog.dismiss();
                }
            });
        }
    }

    public class ProgressDialog extends Dialog {

        public ProgressDialog(Context context) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_progress);

        }
    }
}
