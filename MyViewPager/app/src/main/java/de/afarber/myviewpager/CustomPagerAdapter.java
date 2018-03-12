package de.afarber.myviewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class CustomPagerAdapter extends FragmentPagerAdapter {
    private Fragment mCurrentFragment;
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
        if (position == 0) {
            return MovesFragment.newInstance(mGid);
        }
        if (position == 2) {
            return ChatFragment.newInstance(mGid);
        }
        return GameFragment.newInstance(mGid);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentFragment = (Fragment) object;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
