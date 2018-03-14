package de.afarber.myviewpager;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static de.afarber.myviewpager.MainActivity.GID;

public class PagerFragment extends Fragment  {
    private CustomPagerAdapter mAdapter;
    private ViewPager mPager;

    public interface PagerListener {
    }

    public static PagerFragment newInstance(int gid) {
        PagerFragment f = new PagerFragment();

        Bundle args = new Bundle();
        args.putInt(GID, gid);
        f.setArguments(args);

        return f;
    }

    // have to use deprecated method here, because onAttach(Context) is not called for API < 23
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (! (activity instanceof PagerListener)) {
            throw new ClassCastException(activity + " must implement PagerFragment.PagerListener");
        }
    }

    @Override
	public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager, container, false);
	}

	@Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        int gid = getArguments().getInt(GID);
        getActivity().setTitle(getString(R.string.app_name) + " " + gid);
        mAdapter = new CustomPagerAdapter(getChildFragmentManager(), gid);
        mPager = view.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(1);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pager, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_chat) {
            if (mPager.getCurrentItem() == 1) {
                mPager.setCurrentItem(2);
            } else {
                mPager.setCurrentItem(1);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
