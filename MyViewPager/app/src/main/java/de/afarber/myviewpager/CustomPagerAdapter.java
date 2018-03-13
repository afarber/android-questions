package de.afarber.myviewpager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CustomPagerAdapter extends FragmentPagerAdapter {
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
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0: return MovesFragment.newInstance(mGid);
            case 2: return ChatFragment.newInstance(mGid);
            default: return GameFragment.newInstance(mGid);
        }
    }
}
