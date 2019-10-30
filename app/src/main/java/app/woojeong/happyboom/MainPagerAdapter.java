package app.woojeong.happyboom;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    Main1Fragment tab1;
    Main2Fragment tab2;
    Main3Fragment tab3;

    FragmentManager fragmentManager;
    String[] titles = {
            "커뮤니티", "프리미엄", "프로필"
    };

    public MainPagerAdapter(FragmentManager fm) {
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
                tab1 = new Main1Fragment();
                fragmentManager.beginTransaction().add(tab1, "Main1Fragment");
                fragmentManager.beginTransaction().addToBackStack(null);
                fragmentManager.beginTransaction().commitAllowingStateLoss();
                fragmentManager.beginTransaction().commit();
                return tab1;
            case 1:
                tab2 = new Main2Fragment();
                fragmentManager.beginTransaction().add(tab2, "Main2Fragment");
                fragmentManager.beginTransaction().addToBackStack(null);
                fragmentManager.beginTransaction().commitAllowingStateLoss();
                fragmentManager.beginTransaction().commit();
                return tab2;
            case 2:
                tab3 = new Main3Fragment();
                fragmentManager.beginTransaction().add(tab3, "Main3Fragment");
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