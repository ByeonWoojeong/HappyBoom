package app.woojeong.happyboom;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import app.woojeong.happyboom.CustomTabLayout.TabLayoutWithArrow;

public class ContentsActivity extends AppCompatActivity {

    TextView tabText1, tabText2, tabText3;
    View view1, view2, view3;
    ViewPager viewPager;
    TabLayoutWithArrow tabLayout;
    ContentsPagerAdapter pagerAdapter;
    FragmentManager fragmentManager;
    String tagSelected;

    FrameLayout back_con;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

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

        tabLayout = (TabLayoutWithArrow) findViewById(R.id.tabs_contents);

        view1 = getLayoutInflater().inflate(R.layout.tab_text, null);
        tabText1 = (TextView) view1.findViewById(R.id.tab_text);
        tabText1.setText("내 영상");
        tabText1.setTextColor(getResources().getColor(R.color.colorMain2));
        tagSelected = "0";
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));

        view2 = getLayoutInflater().inflate(R.layout.tab_text, null);
        tabText2= (TextView) view2.findViewById(R.id.tab_text);
        tabText2.setText("내가 쓴글");
        tabText2.setTextColor(getResources().getColor(R.color.colorTextGrey2));
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));

        view3 = getLayoutInflater().inflate(R.layout.tab_text, null);
        tabText3 = (TextView) view3.findViewById(R.id.tab_text);
        tabText3.setText("즐겨찾기");
        tabText3.setTextColor(getResources().getColor(R.color.colorTextGrey2));
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        tabLayout.setTabGravity(TabLayoutWithArrow.GRAVITY_FILL);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentManager = getSupportFragmentManager();
        pagerAdapter = new ContentsPagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayoutWithArrow.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setOnTabSelectedListener(new TabLayoutWithArrow.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayoutWithArrow.Tab tab) {
                tagSelected = tab.getPosition() + "";
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    tabText1.setTextColor(getResources().getColor(R.color.colorMain2));
                    tabText2.setTextColor(getResources().getColor(R.color.colorTextGrey2));
                    tabText3.setTextColor(getResources().getColor(R.color.colorTextGrey2));

                    Contents1Fragment contents1Fragment = (Contents1Fragment) pagerAdapter.getFragment(0);
                    contents1Fragment.reload();

                } else if (tab.getPosition() == 1) {
                    tabText1.setTextColor(getResources().getColor(R.color.colorTextGrey2));
                    tabText2.setTextColor(getResources().getColor(R.color.colorMain2));
                    tabText3.setTextColor(getResources().getColor(R.color.colorTextGrey2));
                    Contents2Fragment contents2Fragment = (Contents2Fragment) pagerAdapter.getFragment(1);
                    contents2Fragment.listing();
                }
                else if (tab.getPosition() == 2) {
                    tabText1.setTextColor(getResources().getColor(R.color.colorTextGrey2));
                    tabText2.setTextColor(getResources().getColor(R.color.colorTextGrey2));
                    tabText3.setTextColor(getResources().getColor(R.color.colorMain2));
                    Contents3Fragment contents3Fragment = (Contents3Fragment) pagerAdapter.getFragment(2);
                    contents3Fragment.listing();
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
    }
    
}
