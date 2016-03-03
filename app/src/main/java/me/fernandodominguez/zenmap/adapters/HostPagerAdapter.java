package me.fernandodominguez.zenmap.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.fernandodominguez.zenmap.fragments.HostDetailFragment;
import me.fernandodominguez.zenmap.fragments.ScanDetailFragment;
import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.network.Host;

/**
 * Created by fernando on 24/02/16.
 */
public class HostPagerAdapter extends FragmentPagerAdapter {

    private final String SECTION_1_TITLE = "General";
    private final String SECTION_2_TITLE = "Network";
    private final String SECTION_3_TITLE = "RAW";
    private final int    NUM_PAGES       = 3;

    private Host host;

    public HostPagerAdapter(FragmentManager fm, Host host) {
        super(fm);
        this.host = host;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return HostDetailFragment.newInstance(position + 1, host);
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
