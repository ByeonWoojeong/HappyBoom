package app.woojeong.happyboom;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ContentsPagerAdapter extends FragmentStatePagerAdapter {

    Contents1Fragment tab1;
    Contents2Fragment tab2;
    Contents3Fragment tab3;

    FragmentManager fragmentManager;
    String[] titles = {
            "내영상", "내가쓴글", "즐겨찾기"
    };

    public ContentsPagerAdapter(FragmentManager fm) {
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
                tab1 = new Contents1Fragment();
                fragmentManager.beginTransaction().add(tab1, "Contents1Fragment");
                fragmentManager.beginTransaction().addToBackStack(null);
                fragmentManager.beginTransaction().commitAllowingStateLoss();
                fragmentManager.beginTransaction().commit();
                return tab1;
            case 1:
               tab2 = new Contents2Fragment();
                fragmentManager.beginTransaction().add(tab2, "Contents2Fragment");
                fragmentManager.beginTransaction().addToBackStack(null);
                fragmentManager.beginTransaction().commitAllowingStateLoss();
                fragmentManager.beginTransaction().commit();
                return tab2;
            case 2:
                tab3 = new Contents3Fragment();
                fragmentManager.beginTransaction().add(tab3, "Contents3Fragment");
                fragmentManager.beginTransaction().addToBackStack(null);
                fragmentManager.beginTransaction().commitAllowingStateLoss();
                fragmentManager.beginTransaction().commit();
                return tab3;
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
            case 2:
                return tab3;
            default:
                return null;
        }
    }
}