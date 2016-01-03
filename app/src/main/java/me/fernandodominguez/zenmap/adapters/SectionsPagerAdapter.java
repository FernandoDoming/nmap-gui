package me.fernandodominguez.zenmap.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.fernandodominguez.zenmap.fragments.PlaceholderFragment;
import me.fernandodominguez.zenmap.models.ScanResult;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final String SECTION_1_TITLE = "General";
    private final String SECTION_2_TITLE = "";
    private final String SECTION_3_TITLE = "RAW";

    private ScanResult scanResult;

    public SectionsPagerAdapter(FragmentManager fm, ScanResult scanResult) {
        super(fm);
        this.scanResult = scanResult;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1, scanResult);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return SECTION_1_TITLE;
            case 1:
                return SECTION_2_TITLE;
            case 2:
                return SECTION_3_TITLE;
        }
        return null;
    }
}
