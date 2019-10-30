package app.woojeong.happyboom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.DTO.Comment;
import app.woojeong.happyboom.DTO.MainVideo;

import static app.woojeong.happyboom.GlobalApplication.setDarkMode;

public class ProfileActivity extends AppCompatActivity {
    private static String TAG = "ProfileActivity";
    Context context;
    InputMethodManager ipmm;
    AQuery aQuery = null;
    OneBtnDialog oneBtnDialog;

    SharedPreferences get_token;
    String getToken, getMember;

    FrameLayout back_con;
    ImageView back, profile_img;
    ScrollView scrollView;
    LinearLayout normal_con;
    TextView nickname, join_date, birth, dream, introduce_normal;

    ListView list_view;
    ArrayList<MainVideo> data;
    MainVideoAdapter mainVideoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(ProfileActivity.this, true);
            }
        }

        context = this;
        aQuery = new AQuery(this);
        ipmm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        getMember = getIntent().getStringExtra("member");

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

        profile_img = findViewById(R.id.profile_img);

        normal_con = findViewById(R.id.normal_con);

        nickname = findViewById(R.id.nickname);
        join_date = findViewById(R.id.join_date);
        birth = findViewById(R.id.birth);
        dream = findViewById(R.id.dream);
        introduce_normal = findViewById(R.id.introduce_normal);

        list_view = findViewById(R.id.list_view);
        data = new ArrayList<MainVideo>();
        mainVideoAdapter = new MainVideoAdapter(ProfileActivity.this, R.layout.list_main_item, data, list_view);
        list_view.setAdapter(mainVideoAdapter);

        showing();
    }

    void showing() {
        final String url = ServerUrl.getBaseUrl() + "/member/profile";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("member", getMember);
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?

                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        JSONArray jsonArrayList = new JSONArray(jsonObject.getString("community"));

                        String getProfile = jsonData.getString("image");

                        putImage(profile_img, ServerUrl.getBaseUrl() + "/uploads/images/origin/" + getProfile);

                        nickname.setText(jsonData.getString("nick"));
                        join_date.setText(jsonData.getString("date"));
                        birth.setText(jsonData.getString("birth"));
                        dream.setText(jsonData.getString("dream"));
                        introduce_normal.setText(jsonData.getString("intro"));

                        for (int i = 0; i < jsonArrayList.length(); i++) {
                            JSONObject getJsonObject = jsonArrayList.getJSONObject(i);
                            String getIdx = getJsonObject.getString("idx");
                            String getImage = getJsonObject.getString("image");

                            String getContent = getJsonObject.getString("content");
                            String getLikeCnt = getJsonObject.getString("like");
                            String getShareCnt = getJsonObject.getString("share");
                            String getReplyCnt = getJsonObject.getString("reply");
                            String getDate = getJsonObject.getString("date");
                            String getNick = getJsonObject.getString("nick");
                            String getIsLike = getJsonObject.getString("islike");
                            data.add(new MainVideo(getIdx, getImage, getProfile, getNick, getDate, getContent, getLikeCnt, getShareCnt, getReplyCnt, getIsLike));
                        }
                        list_view.setAdapter(mainVideoAdapter);
                        setListViewHeightBasedOnChildren(list_view);
                        mainVideoAdapter.notifyDataSetChanged();
                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }

    void putImage(ImageView imageView, String getImg) {
        Glide.with(this)
                .load(getImg)
                .into(imageView);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            Log.i(TAG, " 123 :: " + i);
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
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
