package app.woojeong.happyboom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.CustomTabLayout.TabLayoutWithArrow;

import static com.androidquery.util.AQUtility.getContext;

public class CompanyActivity extends AppCompatActivity {
    private static final String TAG = "CompanyActivity";

    AQuery aQuery = null;
    Context context;
    String token;
    SharedPreferences prefToken;

    TextView tabText1, tabText2;
    View view1, view2;
    ViewPager viewPager;
    TabLayoutWithArrow tabLayout;
    CompanyPagerAdapter pagerAdapter;
    FragmentManager fragmentManager;
    String tagSelected, getMember;

    String getName, getCnt, getYear, getType, getHome, getContent, getPhoto, getLike;  //기업

    FrameLayout back_con, star_con;
    ImageView back, profile_img;
    TextView name, worker_cnt, since_year, company_form, homepage, introduce;
    CheckBox star;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        context = getContext();
        aQuery = new AQuery(this);
        prefToken = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        token = prefToken.getString("Token", "");

        getMember = getIntent().getStringExtra("idx");

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

        profile_img = findViewById(R.id.profile_img);
        name = findViewById(R.id.name);
        worker_cnt = findViewById(R.id.worker_cnt);
        since_year = findViewById(R.id.since_year);
        company_form = findViewById(R.id.company_form);
        homepage = findViewById(R.id.homepage);
        introduce = findViewById(R.id.introduce);
        star_con = findViewById(R.id.star_con);
        star = findViewById(R.id.star);

        star_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star.callOnClick();
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = ServerUrl.getBaseUrl() + "/premium/like";
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("key", getMember);
                Log.i(TAG, " params " + params);
                aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                        Log.i(TAG, " jsonObject " + jsonObject);
                        try {
                            if (jsonObject.getBoolean("return")) {    //return이 true 면?

                            } else if (!jsonObject.getBoolean("return")) {
                                Toast.makeText(CompanyActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.header("epoch-agent", token).header("User-Agent", "android"));
            }
        });

        tabLayout = (TabLayoutWithArrow) findViewById(R.id.tabs_company);
        view1 = getLayoutInflater().inflate(R.layout.tab_text, null);
        tabText1 = (TextView) view1.findViewById(R.id.tab_text);
        tabText1.setText("영상");
        tabText1.setTextColor(getResources().getColor(R.color.colorMain2));
        tagSelected = "0";
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));

        view2 = getLayoutInflater().inflate(R.layout.tab_text, null);
        tabText2 = (TextView) view2.findViewById(R.id.tab_text);
        tabText2.setText("커뮤니티");
        tabText2.setTextColor(getResources().getColor(R.color.colorTextGrey2));
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        tabLayout.setTabGravity(TabLayoutWithArrow.GRAVITY_FILL);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentManager = getSupportFragmentManager();
        pagerAdapter = new CompanyPagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayoutWithArrow.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setOnTabSelectedListener(new TabLayoutWithArrow.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayoutWithArrow.Tab tab) {
                tagSelected = tab.getPosition() + "";
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    tabText1.setTextColor(getResources().getColor(R.color.colorMain2));
                    tabText2.setTextColor(getResources().getColor(R.color.colorTextGrey2));

                    Company1Fragment company1Fragment= (Company1Fragment) pagerAdapter.getFragment(0);
                    company1Fragment.listing();
                } else if (tab.getPosition() == 1) {
                    tabText1.setTextColor(getResources().getColor(R.color.colorTextGrey2));
                    tabText2.setTextColor(getResources().getColor(R.color.colorMain2));
                    Company2Fragment company2Fragment= (Company2Fragment) pagerAdapter.getFragment(1);
                    company2Fragment.listing();
                }
            }

            @Override
            public void onTabUnselected(TabLayoutWithArrow.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayoutWithArrow.Tab tab) {
                tagSelected = tab.getPosition() + "";
            }
        });

        appBardata();
    }

    void appBardata() {
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

                        getName = jsonData.getString("name");
                        getCnt = jsonData.getString("cnt");
                        getYear = jsonData.getString("year");
                        getType = jsonData.getString("type");
                        getHome = jsonData.getString("home");
                        getContent = jsonData.getString("content");
                        getPhoto = ServerUrl.getBaseUrl() + "/uploads/images/origin/" + jsonData.getString("image");
                        getLike = jsonData.getString("islike");

                        name.setText(getName);
                        worker_cnt.setText(getCnt);
                        since_year.setText(getYear);
                        if ("1".equals(getType)) {
                            company_form.setText("개인 사업자");
                        } else if ("2".equals(getType)) {
                            company_form.setText("법인사업자");
                        } else if ("3".equals(getType)) {
                            company_form.setText("금융기관");
                        } else if ("4".equals(getType)) {
                            company_form.setText("공기업");
                        } else if ("5".equals(getType)) {
                            company_form.setText("단체");
                        } else if ("6".equals(getType)) {
                            company_form.setText("기타");
                        }
                        homepage.setText(getHome);
                        putImage(profile_img, getPhoto);
                        introduce.setText(getContent);

                        if("0".equals(getLike)){
                            star.setChecked(false);
                        } else {
                            star.setChecked(true);
                        }

                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", token).header("User-Agent", "android"));
    }

    void putImage(ImageView imageView, String getImg) {
        Glide.with(this)
                .load(getImg)
                .into(imageView);
    }
}
