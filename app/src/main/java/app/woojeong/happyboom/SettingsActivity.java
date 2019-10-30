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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class SettingsActivity extends AppCompatActivity {
    private static String TAG = "SettingsActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;
    PasswordDialog passwordDialog;
    OutDialog outDialog;

    SharedPreferences get_token;
    String getToken;

    FrameLayout back_con, contents_con, notice_con, event_con, push_con, payment_con, terms_con, password_con, logout_con, withdrawal_con;
    ImageView back;
    TextView contents, notice, event, push, payment, terms, password, logout, withdrawal;
    SwitchCompat push_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(SettingsActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

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

        contents_con = findViewById(R.id.contents_con);
        notice_con = findViewById(R.id.notice_con);
        event_con = findViewById(R.id.event_con);
        push_con = findViewById(R.id.push_con);
        payment_con = findViewById(R.id.payment_con);
        terms_con = findViewById(R.id.terms_con);
        password_con = findViewById(R.id.password_con);
        logout_con = findViewById(R.id.logout_con);
        withdrawal_con = findViewById(R.id.withdrawal_con);

        contents = findViewById(R.id.contents);
        notice = findViewById(R.id.notice);
        event = findViewById(R.id.event);
        push = findViewById(R.id.push);
        payment = findViewById(R.id.payment);
        terms = findViewById(R.id.terms);
        password = findViewById(R.id.password);
        logout = findViewById(R.id.logout);
        withdrawal = findViewById(R.id.withdrawal);

        push_switch = findViewById(R.id.push_switch);

        contents_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contents.callOnClick();
            }
        });
        contents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ContentsActivity.class);
                startActivity(intent);
            }
        });

        notice_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice.callOnClick();
            }
        });
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, NoticeActivity.class);
                startActivity(intent);
            }
        });

        event_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.callOnClick();
            }
        });
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EventListActivity.class);
                startActivity(intent);
            }
        });

        push_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final String url = ServerUrl.getBaseUrl() + "/setting/setpush";
                Map<String, Object> params = new HashMap<String, Object>();

                if(isChecked){
                    params.put("value", "1");
                    Log.i(TAG, " params " + params);
                    aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                            Log.i(TAG, " jsonObject " + jsonObject);
                            try {
                                if (jsonObject.getBoolean("return")) {    //return이 true 면?

                                } else if (!jsonObject.getBoolean("return")) {
                                    oneBtnDialog = new OneBtnDialog(SettingsActivity.this, "다시 시도해주세요.", "확인");
                                    oneBtnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    oneBtnDialog.setCancelable(false);
                                    oneBtnDialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.header("epoch-agent", getToken).header("User-Agent", "android"));
                } else {
                    params.put("value", "0");
                    Log.i(TAG, " params " + params);
                    aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                            Log.i(TAG, " jsonObject " + jsonObject);
                            try {
                                if (jsonObject.getBoolean("return")) {    //return이 true 면?

                                } else if (!jsonObject.getBoolean("return")) {
                                    oneBtnDialog = new OneBtnDialog(SettingsActivity.this, "다시 시도해주세요.", "확인");
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
            }
        });

        payment_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment.callOnClick();
            }
        });
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });

        terms_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terms.callOnClick();
            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TermsListActivity.class);
                startActivity(intent);
            }
        });

        password_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password.callOnClick();
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordDialog = new PasswordDialog(context, "변경할 비밀 번호");
                passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                passwordDialog.setCancelable(false);
                passwordDialog.show();
            }
        });

        logout_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout.callOnClick();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outDialog = new OutDialog(context, "해피붐을\n로그아웃 하시겠습니까?", "logout");
                outDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                outDialog.setCancelable(false);
                outDialog.show();
            }
        });

        withdrawal_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withdrawal.callOnClick();
            }
        });
        withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outDialog = new OutDialog(context, "회원 탈퇴시 회원정보는\n모두 삭제됩니다.\n탈퇴 하시겠습니까?", "signout");
                outDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                outDialog.setCancelable(false);
                outDialog.show();
            }
        });

        getPush();
    }

    void getPush(){
        final String url = ServerUrl.getBaseUrl() + "/setting/getpush";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        if("1".equals(jsonData.getString("push"))){
                            push_switch.setChecked(true);
                        }else {
                            push_switch.setChecked(false);
                        }

                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(SettingsActivity.this, "다시 시도해주세요.", "확인");
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

    public class PasswordDialog extends Dialog {
        PasswordDialog passwordDialog = this;
        Context context;

        public PasswordDialog(final Context context, final String title) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_input);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = findViewById(R.id.title1);
            EditText content = findViewById(R.id.content);
            final TextView btn1 = (TextView) findViewById(R.id.btn1);
            TextView btn2 = (TextView) findViewById(R.id.btn2);
            title1.setText(title);
            btn1.setText("취소");
            btn2.setText("확인");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    passwordDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if("".equals(content.getText().toString())){
                        Toast.makeText(SettingsActivity.this, "변경할 비밀 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (content.getText().toString().length() < 4){
                        Toast.makeText(SettingsActivity.this, "변경할 비밀 번호를 4자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final String url = ServerUrl.getBaseUrl() + "/setting/newpass";
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("pass", content.getText().toString());
                    Log.i(TAG, " params " + params);
                    aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                        @Override
                        public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                            Log.i(TAG, " jsonObject " + jsonObject);
                            try {
                                if (jsonObject.getBoolean("return")) {    //return이 true 면?
                                    Toast.makeText(SettingsActivity.this, "비밀 번호를 변경하였습니다.", Toast.LENGTH_SHORT).show();
                                    passwordDialog.dismiss();
                                } else if (!jsonObject.getBoolean("return")) {
                                    Toast.makeText(SettingsActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }.header("epoch-agent", getToken).header("User-Agent", "android"));
                }
            });
        }
    }

    public class OutDialog extends Dialog {
        OutDialog outDialog = this;
        Context context;

        public OutDialog(final Context context, String text, String what) {
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_two_btn);
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.context = context;
            TextView title1 = (TextView) findViewById(R.id.title1);
            TextView title2 = (TextView) findViewById(R.id.title2);
            TextView btn1 = (TextView) findViewById(R.id.btn1);
            TextView btn2 = (TextView) findViewById(R.id.btn2);
            title2.setVisibility(View.GONE);
            title1.setText(text);
            btn1.setText("취소");
            btn2.setText("확인");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    outDialog.dismiss();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "";
                    if ("logout".equals(what)) {
                        url = ServerUrl.getBaseUrl() + "/logout";
                    } else {
                        url = ServerUrl.getBaseUrl() + "/signout";
                    }
                    Map<String, Object> params = new HashMap<String, Object>();
                    aQuery.ajax(url, params, String.class, new AjaxCallback<String>() {
                        @Override
                        public void callback(String url, String jsonString, AjaxStatus status) {
                            Log.i(TAG, " jsonString " + jsonString);
                            try {
                                outDialog.dismiss();
                                JSONObject jsonObject = new JSONObject(jsonString);

                                if (jsonObject.getBoolean("return")) {    //return이 true 면?

                                    //토큰은 서버에서 날려주므로 앱에서 토큰을 날릴 필요 없음.

                                    if ("logout".equals(what)) {
                                        Toast.makeText(SettingsActivity.this, "해피붐을 로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SettingsActivity.this, "해피붐을 탈퇴 하였습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                    SharedPreferences HappyBoom = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = HappyBoom.edit();
                                    editor.clear();
                                    editor.apply();
                                    editor.commit();

                                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                } else if (!jsonObject.getBoolean("return")) {
                                    oneBtnDialog = new OneBtnDialog(SettingsActivity.this, "다시 시도해주세요 !", "확인");
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
            });
        }
    }
}
