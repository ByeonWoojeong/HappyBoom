package app.woojeong.happyboom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.woojeong.happyboom.CustomTabLayout.TabLayoutWithArrow;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Context context;
    AQuery aQuery = null;

    SharedPreferences get_token, HappyBoom;
    String getToken = "";
    SharedPreferences.Editor HappyBoomEditor;

    BackPressCloseHandler backPressCloseHandler;

    TextView tabText1, tabText2, tabText3;
    View view1, view2, view3;
    ViewPager viewPager;
    TabLayout tabLayout;
    MainPagerAdapter pagerAdapter;
    FragmentManager fragmentManager;
    String tagSelected;


    ImageView logo, search, settings;
    FrameLayout tab_con;
    EditText search_edit;

    boolean isSearch;

    String searchVideo = "";

    String KEY = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.setBackgroundDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                setDarkMode(MainActivity.this, true);
            }
        }
        context = this;
        aQuery = new AQuery(this);

        tabLayout = findViewById(R.id.main_tabs);
        view1 = getLayoutInflater().inflate(R.layout.custom_tab_main, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_community);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));

        view2 = getLayoutInflater().inflate(R.layout.custom_tab_main, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_premium);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        view3 = getLayoutInflater().inflate(R.layout.custom_tab_main, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.tab_profile);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        tabLayout.getTabAt(0).setIcon(R.drawable.tab_community);
        tabLayout.setTabGravity(TabLayoutWithArrow.GRAVITY_FILL);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentManager = getSupportFragmentManager();
        pagerAdapter = new MainPagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tagSelected = tab.getPosition() + "";
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    view1.setSelected(true);
                    view2.setSelected(false);
                    view3.setSelected(false);

                    Main1Fragment main1Fragment = (Main1Fragment) pagerAdapter.getFragment(0);
                    main1Fragment.listing();
                } else if (tab.getPosition() == 1) {
                    view1.setSelected(false);
                    view2.setSelected(true);
                    view3.setSelected(false);

                    Main2Fragment main2Fragment = (Main2Fragment) pagerAdapter.getFragment(1);
                    main2Fragment.showing();
                } else if (tab.getPosition() == 2) {
                    view1.setSelected(false);
                    view2.setSelected(false);
                    view3.setSelected(true);

                    Main3Fragment main3Fragment = (Main3Fragment) pagerAdapter.getFragment(2);
                    main3Fragment.reload();

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tagSelected = tab.getPosition() + "";
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    view1.setSelected(true);
                    view2.setSelected(false);
                    view3.setSelected(false);

                    Main1Fragment main1Fragment = (Main1Fragment) pagerAdapter.getFragment(0);
                    main1Fragment.listing();
                } else if (tab.getPosition() == 1) {
                    view1.setSelected(false);
                    view2.setSelected(true);
                    view3.setSelected(false);

                    Main2Fragment main2Fragment = (Main2Fragment) pagerAdapter.getFragment(1);
                    main2Fragment.showing();
                } else if (tab.getPosition() == 2) {
                    view1.setSelected(false);
                    view2.setSelected(false);
                    view3.setSelected(true);

                    Main3Fragment main3Fragment = (Main3Fragment) pagerAdapter.getFragment(2);
                    main3Fragment.reload();

                }
            }
        });

        backPressCloseHandler = new BackPressCloseHandler(this);

        logo = findViewById(R.id.logo);
        search = findViewById(R.id.search);
        settings = findViewById(R.id.settings);
        search_edit = findViewById(R.id.search_edit);

        tab_con = findViewById(R.id.tab_con);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearch) {
                    logo.setVisibility(View.GONE);
                    search_edit.setVisibility(View.VISIBLE);
                    isSearch = true;
                } else {
                    logo.setVisibility(View.VISIBLE);
                    search_edit.setVisibility(View.GONE);
                    searchVideo = search_edit.getText().toString();

                    Main1Fragment main1Fragment = (Main1Fragment) pagerAdapter.getFragment(0);
                    main1Fragment.listing();

                    Main2Fragment main2Fragment = (Main2Fragment) pagerAdapter.getFragment(1);
                    main2Fragment.showing();

                    isSearch = false;
                }
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search_edit.setText("");
                searchVideo = "";

                Main1Fragment main1Fragment = (Main1Fragment) pagerAdapter.getFragment(0);
                main1Fragment.listing();

                Main2Fragment main2Fragment = (Main2Fragment) pagerAdapter.getFragment(1);
                main2Fragment.showing();

                Main3Fragment main3Fragment = (Main3Fragment) pagerAdapter.getFragment(2);
                main3Fragment.reload();

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        get_token = getSharedPreferences("prefToken", Activity.MODE_PRIVATE);
        getToken = get_token.getString("Token", "");

        HappyBoom = getSharedPreferences("HappyBoom", Activity.MODE_PRIVATE);
        HappyBoomEditor = HappyBoom.edit();

        checkMode();


        String activity = getIntent().getStringExtra("activity");
        if("community".equals(activity)){
            String idx = getIntent().getStringExtra("idx");
            Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
            intent.putExtra("idx", idx);
            startActivity(intent);
        } else if("company".equals(activity)){
            String idx = getIntent().getStringExtra("idx");
            Intent intent = new Intent(MainActivity.this, CompanyActivity.class);
            intent.putExtra("idx", idx);
            startActivity(intent);
        }
    }

    private void setDarkMode(MainActivity mainActivity, boolean b) {
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    //로그인 모드(사업자, 이용자)
    void checkMode() {
        final String url = ServerUrl.getBaseUrl() + "/main/loginCheck";
        Map<String, Object> params = new HashMap<String, Object>();
        Log.i(TAG, " params " + params);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject jsonObject, AjaxStatus status) {
                Log.i(TAG, " jsonObject " + jsonObject);
                try {
                    if (jsonObject.getBoolean("return")) {    //return이 true 면?
                        if ("1".equals(jsonObject.getString("level"))) {
                            HappyBoomEditor.putString("Mode", "normal");
                        } else {
                            HappyBoomEditor.putString("Mode", "ceo");
                        }
                        HappyBoomEditor.commit();
                        Log.i(TAG, " Mode " + HappyBoom.getString("Mode", ""));
                    } else if (!jsonObject.getBoolean("return")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("epoch-agent", getToken).header("User-Agent", "android"));
    }
}
