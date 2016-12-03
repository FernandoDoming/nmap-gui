package me.fernandodominguez.zenmap.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.adapters.SectionsPagerAdapter;
import me.fernandodominguez.zenmap.constants.Extras;
import me.fernandodominguez.zenmap.models.Scan;

public class ScanDetailActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SharedPreferences sharedPrefs;
    private Context context = this;

    private FloatingActionButton fab;

    private Scan scan;
    private String NMAP_BINARY_FILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_detail);

        scan = (Scan) getIntent().getSerializableExtra(Extras.SCAN_EXTRA);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(scan.getScanResult().getTitle());
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                                                         scan.getScanResult());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        NMAP_BINARY_FILE
                = sharedPrefs.getString(getString(R.string.nmap_binary_path),
                                        getFilesDir().getParent() + "/bin/nmap");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            scan.run(context, NMAP_BINARY_FILE);
            startRefreshingAnimation();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /* Private methods */

    private void startRefreshingAnimation() {
        fab.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_forever));
    }
}
