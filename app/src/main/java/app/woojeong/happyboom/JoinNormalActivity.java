package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class JoinNormalActivity extends AppCompatActivity {
    private static String TAG = "JoinNormalActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    JoinDialog joinDialog;

    SharedPreferences get_token;
    String getToken, getType;

    FrameLayout back_con, ok_con;
    LinearLayout sns_con;
    ImageView back, id_overlap, send, verify;
    ScrollView scrollView;
    EditText id, password, password2, phone, my_number, nickname, other_nickname;
    CheckBox check1, check2, check3, check_all;
    TextView agree1, agree2, agree3, ok;

    boolean isId, isPhone, isVerify, isNick, isCheck1, isCheck2, isCheck3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_normal);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(JoinNormalActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ipmm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        getType = getIntent().getStringExtra("type");

        back_con = findViewById(R.id.back_con);
        back = findViewById(R.id.back);
        back_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back.callOnClick();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scrollView = findViewById(R.id.scrollView);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 100);

        ok_con = findViewById(R.id.ok_con);

        sns_con = findViewById(R.id.sns_con);

        if("sns".equals(getType)){
            sns_con.setVisibility(View.GONE);
        }else {
            sns_con.setVisibility(View.VISIBLE);
        }

        id_overlap = findViewById(R.id.id_overlap);
        send = findViewById(R.id.send);
        verify = findViewById(R.id.verify);

        id = findViewById(R.id.id);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        phone = findViewById(R.id.phone);
        my_number = findViewById(R.id.my_number);
        nickname = findViewById(R.id.nickname);
        other_nickname = findViewById(R.id.nickname2);

        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check_all = findViewById(R.id.check_all);

        agree1 = findViewById(R.id.agree1);
        agree2 = findViewById(R.id.agree2);
        agree3 = findViewById(R.id.agree3);
        ok = findViewById(R.id.ok);

        id_overlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(id.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "아이디를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                idDuplication();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(phone.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if (phone.length() < 11 && 11 < phone.length()) {
                    oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를\n정확하게 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if (!phone.getText().toString().startsWith("010")) {
                    oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를\n정확하게 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                sendCertSMS();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(my_number.getText().toString())) {
                    oneBtnDialog = new OneBtnDialog(context, "인증 번호를 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                } else if (phone.length() < 4 && 4 < phone.length()) {
                    oneBtnDialog = new OneBtnDialog(context, "인증 번호를\n정확하게 입력해주세요 !", "확인");
                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    oneBtnDialog.setCancelable(false);
                    oneBtnDialog.show();
                    return;
                }

                verifySMS();
            }
        });

        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck1 = true;

                    if (check2.isChecked() && check3.isChecked()) {
                        check_all.setChecked(true);
                    }
                } else {
                    isCheck1 = false;
                    check_all.setChecked(false);

                }
            }
        });

        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck2 = true;
                    if (check1.isChecked() && check3.isChecked()) {
                        check_all.setChecked(true);
                    }
                } else {
                    isCheck2 = false;
                    check_all.setChecked(false);
                }
            }
        });

        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck3 = true;
                    if (check1.isChecked() && check2.isChecked()) {
                        check_all.setChecked(true);
                    }
                } else {
                    isCheck3 = false;
                    check_all.setChecked(false);
                }
            }
        });

        check_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheck1 = true;
                    isCheck2 = true;
                    isCheck3 = true;
                    check1.setChecked(true);
                    check2.setChecked(true);
                    check3.setChecked(true);
                } else {
                    isCheck1 = false;
                    isCheck2 = false;
                    isCheck3 = false;
                    check1.setChecked(false);
                    check2.setChecked(false);
                    check3.setChecked(false);
                }
            }
        });

        agree1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinNormalActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 1);
                startActivity(intent);
            }
        });
        agree2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinNormalActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 2);
                startActivity(intent);
            }
        });
        agree3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinNormalActivity.this, TermsDetailsActivity.class);
                intent.putExtra("what", 3);
                startActivity(intent);
            }
        });

        ok_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok.callOnClick();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if("sns".equals(getType)){
                    if (!isPhone || "".equals(phone.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를\n인증해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if ("".equals(my_number.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "인증 번호를 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isVerify) {
                        oneBtnDialog = new OneBtnDialog(context, "인증 번호를\n다시 확인해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if ("".equals(nickname.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "닉네임을 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isCheck1 || !isCheck2 || !isCheck3) {
                        oneBtnDialog = new OneBtnDialog(context, "동의 사항을 동의해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    }
                    joinSNS();
                }else {
                    if (!isId || "".equals(id.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "아이디 중복 확인을 해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if ("".equals(password.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "비밀 번호를 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if ("".equals(password2.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "비밀 번호 확인을 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!password.getText().toString().equals(password2.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "비밀 번호 확인을\n다시 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isPhone || "".equals(phone.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "핸드폰 번호를\n인증해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if ("".equals(my_number.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "인증 번호를 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isVerify) {
                        oneBtnDialog = new OneBtnDialog(context, "인증 번호를\n다시 확인해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    }  else if ("".equals(nickname.getText().toString())) {
                        oneBtnDialog = new OneBtnDialog(context, "닉네임을 입력해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    } else if (!isCheck1 || !isCheck2 || !isCheck3) {
                        oneBtnDialog = new OneBtnDialog(context, "동의 사항을 동의해주세요 !", "확인");
                        oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        oneBtnDialog.setCancelable(false);
                        oneBtnDialog.show();
                        return;
                    }
                    joinNormal();
                }
            }
        });
    }

    //아이디 중복 체크
    void idDuplication() {
        final String url = ServerUrl.getBaseUrl() + "/join/id";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id.getText().toString());
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(JoinNormalActivity.this, "사용 가능한 ID 입니다.", Toast.LENGTH_SHORT).show();
                        isId = true;
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "중복된 ID 입니다.", "확인");
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

    //휴대폰 인증번호 요청
    void sendCertSMS() {
        final String url = ServerUrl.getBaseUrl() + "/join/certsms";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("phone", phone.getText().toString());
        params.put("level", "1");
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(JoinNormalActivity.this, "인증 번호를 보냈습니다.", Toast.LENGTH_SHORT).show();
//                        if(null != jsonObject.getString("cert") && !"".equals(jsonObject.getString("cert"))){
//                            Toast.makeText(JoinNormalActivity.this, "인증 번호 : " + jsonObject.getString("cert"), Toast.LENGTH_SHORT).show();
//                        }
                        isPhone = true;
                    } else if (!jsonObject.getBoolean("return")) {
                        if("dup".equals(jsonObject.getString("type"))){
                            oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "중복된 전화번호 입니다 !", "확인");
                            oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            oneBtnDialog.setCancelable(false);
                            oneBtnDialog.show();
                            return;
                        }
                        oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "다시 시도해주세요 !", "확인");
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

    //휴대폰 인증번호 확인
    void verifySMS() {
        final String url = ServerUrl.getBaseUrl() + "/join/verifysms";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("phone", phone.getText().toString());
        params.put("cert", my_number.getText().toString());
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Toast.makeText(JoinNormalActivity.this, "인증 번호가 확인되었습니다.", Toast.LENGTH_SHORT).show();
                        isVerify = true;
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "인증 번호 확인을\n다시 시도해주세요 !", "확인");
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

    //회원 가입(SNS)
    void joinSNS(){
        final String url = ServerUrl.getBaseUrl() + "/join/profile";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nick", nickname.getText().toString());
        if(!"".equals(other_nickname.getText().toString())){
            params.put("nick2", other_nickname.getText().toString());
        }
        Log.i(TAG, " url " + url);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        joinDialog = new JoinDialog(JoinNormalActivity.this, "회원 가입을\n완료하였습니다.", "확인");
                        joinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        joinDialog.setCancelable(false);
                        joinDialog.show();
                    } else if (!jsonObject.getBoolean("return")) {
                        //type : nick 중복된 닉네임, nick2 : 없는 추천인

                        if("nick".equals(jsonObject.getString("type"))){
                            oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "중복된 닉네임 입니다 !", "확인");
                            oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            oneBtnDialog.setCancelable(false);
                            oneBtnDialog.show();
                            return;
                        }

                        if("nick2".equals(jsonObject.getString("type"))){
                            oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "없는 추천인 입니다 !", "확인");
                            oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            oneBtnDialog.setCancelable(false);
                            oneBtnDialog.show();
                            return;
                        }

                        oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "회원 가입을\n실패 하였습니다.", "확인");
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

    //회원 가입(일반)
    void joinNormal(){
        final String url = ServerUrl.getBaseUrl() + "/join/nor";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id.getText().toString());
        params.put("pass", password.getText().toString());
        params.put("nick", nickname.getText().toString());
        if(!"".equals(other_nickname.getText().toString())){
            params.put("nick2", other_nickname.getText().toString());
        }
        Log.i(TAG, " url " + url);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        joinDialog = new JoinDialog(JoinNormalActivity.this, "회원 가입을\n완료하였습니다.", "확인");
                        joinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        joinDialog.setCancelable(false);
                        joinDialog.show();
                    } else if (!jsonObject.getBoolean("return")) {
                        //type : nick 중복된 닉네임, nick2 : 없는 추천인

                        if("nick".equals(jsonObject.getString("type"))){
                            oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "중복된 닉네임 입니다 !", "확인");
                            oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            oneBtnDialog.setCancelable(false);
                            oneBtnDialog.show();
                            return;
                        }

                        if("nick2".equals(jsonObject.getString("type"))){
                            oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "없는 추천인 입니다 !", "확인");
                            oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            oneBtnDialog.setCancelable(false);
                            oneBtnDialog.show();
                            return;
                        }

                        oneBtnDialog = new OneBtnDialog(JoinNormalActivity.this, "회원 가입을\n실패 하였습니다.", "확인");
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

    public class JoinDialog extends Dialog {
        JoinDialog joinDialog = this;
        Context context;

        public JoinDialog(final Context context, final String text, final String btnText) {
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
                    joinDialog.dismiss();
                    Intent intent = new Intent(JoinNormalActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
