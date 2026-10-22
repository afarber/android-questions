package de.afarber.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CrimePagerActivity extends FragmentActivity {
	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//mViewPager = new ViewPager(this);
        //mViewPager.setId(R.id.viewPager1);
        //setContentView(mViewPager);
        
		setContentView(R.layout.crime_pager);
		mViewPager = (ViewPager)findViewById(R.id.viewPager2);

		mCrimes = CrimeLab.get(this).getCrimes();
		
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount() {
				return mCrimes.size();
			}
			
			@Override
			public Fragment getItem(int pos) {
				Crime crime = mCrimes.get(pos);
				return CrimeFragment.newInstance(crime.getId());
			}
		});
		
		UUID crimeId = (UUID)getIntent()
			.getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		
		for (int i = 0; i < mCrimes.size(); i++) {
			Crime crime = mCrimes.get(i);

			if (crime.getId().equals(crimeId)) {
				mViewPager.setCurrentItem(i);
				setTitle(crime.getTitle());
				break;
			}
		}
		
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) { }
			
			public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) { }
			
			public void onPageSelected(int pos) {
				Crime crime = mCrimes.get(pos);
				if (crime.getTitle() != null) {
					setTitle(crime.getTitle());
				}
			}
		});		
	}
}
