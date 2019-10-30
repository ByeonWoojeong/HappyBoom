package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.DTO.Comment;
import app.woojeong.happyboom.DTO.Notice;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class NoticeActivity extends AppCompatActivity {
    private static String TAG = "NoticeActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;

    SharedPreferences get_token;
    String getToken;

    FrameLayout back_con;
    ImageView back;

    ExpandableListView expandable_list;
    ExpandableListAdapter adapter = null;
    JSONObject jsonObjectList;
    ArrayList<Notice> data = null;
    ArrayList<ArrayList<Notice>> data2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(NoticeActivity.this, true);
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

        expandable_list = findViewById(R.id.expandable_list);
        data = new ArrayList<Notice>();
        data2 = new ArrayList<ArrayList<Notice>>();

        expandableListing();
    }

    void expandableListing(){
        data.clear();
        final String url = ServerUrl.getBaseUrl() + "/setting/notice";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                try {
                    if (jsonObject.getBoolean("return")) {
                        final JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObjectList = jsonArray.getJSONObject(i);
                            data.add(new Notice(jsonObjectList.getString("title"), jsonObjectList.getString("content")));
                            data2.add(new ArrayList<Notice>());
                            data2.get(i).add(new Notice(jsonObjectList.getString("title"), jsonObjectList.getString("content")));
                        }
                        adapter = new NoticeAdapter(NoticeActivity.this, expandable_list, R.layout.list_notice_title_item, R.layout.list_notice_content_item, data, data2);
                        expandable_list.setAdapter(adapter);
                    } else {
                        data.clear();
                        data2.clear();
                        adapter = new NoticeAdapter(NoticeActivity.this, expandable_list, R.layout.list_notice_title_item, R.layout.list_notice_content_item, data, data2);
                        expandable_list.setAdapter(adapter);
                        oneBtnDialog = new OneBtnDialog(NoticeActivity.this, "공지사항이 없습니다 !", "확인");
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
