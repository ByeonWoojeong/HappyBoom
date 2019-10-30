package app.woojeong.happyboom;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


public class CompanyPagerAdapter extends FragmentStatePagerAdapter {

    Company1Fragment tab1;
    Company2Fragment tab2;

    FragmentManager fragmentManager;
    String[] titles = {
            "영상", "커뮤니티"
    };

    public CompanyPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                tab1 = new Company1Fragment();
                fragmentManager.beginTransaction().add(tab1, "Company1Fragment");
                fragmentManager.beginTransaction().addToBackStack(null);
                fragmentManager.beginTransaction().commitAllowingStateLoss();
                fragmentManager.beginTransaction().commit();
                return tab1;
            case 1:
                tab2 = new Company2Fragment();
                fragmentManager.beginTransaction().add(tab2, "Company2Fragment");
                fragmentManager.beginTransaction().addToBackStack(null);
                fragmentManager.beginTransaction().commitAllowingStateLoss();
                fragmentManager.beginTransaction().commit();
                return tab2;
            default:
                return null;
        }
    }

    Fragment getFragment(int i) {
        switch (i) {
            case 0:
                return tab1;
            case 1:
                return tab2;
            default:
                return null;
        }
    }
}