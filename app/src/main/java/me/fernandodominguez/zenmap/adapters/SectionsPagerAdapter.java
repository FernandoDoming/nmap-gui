package me.fernandodominguez.zenmap.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;

import me.fernandodominguez.zenmap.fragments.ScanDetailFragment;
import me.fernandodominguez.zenmap.models.ScanResult;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final String SECTION_1_TITLE = "General";
    private final String SECTION_2_TITLE = "Network";
    private final String SECTION_3_TITLE = "RAW";
    private final int    NUM_PAGES       = 3;

    private ScanResult scanResult;

    private HashMap<Integer, Fragment> items;

    public SectionsPagerAdapter(FragmentManager fm, ScanResult scanResult) {
        super(fm);
        this.scanResult = scanResult;
        this.items = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        Integer pos = position + 1;
        if (items.get(pos) == null) {
            items.put(pos, ScanDetailFragment.newInstance(pos, scanResult));
        }
        return items.get(pos);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
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
