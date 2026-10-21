package de.afarber.myviewpager;

import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class CustomPagerAdapter extends FragmentStatePagerAdapter {
    private int mGid;

    public CustomPagerAdapter(FragmentManager fm, int gid) {
        super(fm);
        mGid = gid;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public androidx.fragment.app.Fragment getItem(int position) {
        switch (position) {
            case 0: return MovesFragment.newInstance(mGid);
            case 2: return ChatFragment.newInstance(mGid);
            default: return GameFragment.newInstance(mGid);
        }
    }
}
