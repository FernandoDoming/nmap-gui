package me.fernandodominguez.zenmap.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.adapters.GeneralResultsListAdapter;
import me.fernandodominguez.zenmap.adapters.HostPagerAdapter;
import me.fernandodominguez.zenmap.constants.Extras;
import me.fernandodominguez.zenmap.constants.ScanTypes;
import me.fernandodominguez.zenmap.helpers.StringHelper;
import me.fernandodominguez.zenmap.models.NmapExecutor;
import me.fernandodominguez.zenmap.models.Scan;
import me.fernandodominguez.zenmap.models.host.Service;
import me.fernandodominguez.zenmap.models.network.Host;

public class HostDetailActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private HostPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SharedPreferences sharedPrefs;
    private Context context = this;

    private FloatingActionButton fab;

    private Host host;
    private String NMAP_BINARY_FILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_detail);

        host = (Host) getIntent().getSerializableExtra(Extras.HOST_EXTRA);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(host.getTitle());
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new HostPagerAdapter(getSupportFragmentManager(), host);

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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addHostDetails(ViewGroup viewGroup, Host host) {

        for (String detail : Host.HOST_DETAILS) {
            try {
                Method method = host.getClass().getMethod("get" + StringHelper.toCamelCase(detail));
                String data   = (String) method.invoke(host);

                if (data != null) {
                    int butResId = getResources().getIdentifier(
                            "host_" + detail + "_discover",
                            "id",
                            getPackageName()
                    );
                    // Hide the discover button in case data is returned
                    viewGroup.findViewById(butResId).setVisibility(View.INVISIBLE);
                    // Set the textview content to that data
                    int tvResId =
                            getResources().getIdentifier("host_" + detail, "id",
                                                         getPackageName());
                    TextView tv = (TextView) viewGroup.findViewById(tvResId);
                    tv.setText(data);
                }
            } catch (NoSuchMethodException e) {     // No multi-except catch due to min API lvl
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (host.getServices() != null && !host.getServices().isEmpty()) {
            ListView resultsListView = (ListView) findViewById(R.id.general_result_listview);
            GeneralResultsListAdapter<Service> adapter =
                    new GeneralResultsListAdapter<>(this, host.getServices());
            resultsListView.setAdapter(adapter);
        }
    }

    public void scanDetail(View view) {
        scanDetail(view.getTag().toString());
        Snackbar.make(
                findViewById(R.id.main_content),
                getResources().getString(R.string.scan_details),
                Snackbar.LENGTH_LONG).show();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.scan_progress);
        progressBar.setIndeterminate(true);
    }

    /* Private methods */

    private void scanDetail(String detail) {
        final String target = host.getAddress();
        Scan scan = new Scan(target, ScanTypes.HOST_SCAN);
        // TODO: Set intensity accordingly
        scan.setIntensity(ScanTypes.OS_SCAN);

        NmapExecutor executor = new NmapExecutor(context, NMAP_BINARY_FILE);
        executor.execute(scan);
    }

}
