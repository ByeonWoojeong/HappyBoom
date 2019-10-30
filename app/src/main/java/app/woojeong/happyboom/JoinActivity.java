package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class JoinActivity extends AppCompatActivity {
    private static String TAG = "JoinActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;

    FrameLayout back_con;
    ImageView back, join_normal, join_ceo;

    SharedPreferences get_token;
    String getToken = "", getType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(JoinActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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

        join_normal = findViewById(R.id.join_normal);
        join_ceo = findViewById(R.id.join_ceo);

        join_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setNormal();
                Intent intent = new Intent(JoinActivity.this, JoinNormalActivity.class);
                intent.putExtra("type", getType);
                startActivity(intent);
                finish();
            }
        });

        join_ceo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               setCompany();
                Intent intent = new Intent(JoinActivity.this, JoinCEOActivity.class);
                intent.putExtra("type", getType);
                startActivity(intent);
                finish();
            }
        });
    }

    void setNormal() {
        final String url = ServerUrl.getBaseUrl() + "/join/setnor";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        Log.i(TAG, " getToken " + getToken);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Intent intent = new Intent(JoinActivity.this, JoinNormalActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(JoinActivity.this, "다시 시도해주세요 !", "확인");
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

    void setCompany() {
        final String url = ServerUrl.getBaseUrl() + "/join/setbiz";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        Log.i(TAG, " getToken " + getToken);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        Intent intent = new Intent(JoinActivity.this, JoinCEOActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (!jsonObject.getBoolean("return")) {
                        oneBtnDialog = new OneBtnDialog(JoinActivity.this, "다시 시도해주세요 !", "확인");
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
}
