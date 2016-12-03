package me.fernandodominguez.zenmap.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.adapters.ScansListAdapter;
import me.fernandodominguez.zenmap.async.DnsNameResolutionTask;
import me.fernandodominguez.zenmap.async.SimpleHttpTask;
import me.fernandodominguez.zenmap.constants.Extras;
import me.fernandodominguez.zenmap.constants.ScanTypes;
import me.fernandodominguez.zenmap.helpers.NetworkHelper;
import me.fernandodominguez.zenmap.helpers.ScanHelper;
import me.fernandodominguez.zenmap.models.Scan;

public class MainActivity extends AppCompatActivity {

    private String NMAP_BINARY_FILE;
    public String NMAP_DOWNLOAD_URL;
    public String NMAP_VERSION_URL;

    private Scan newScan = null;
    private List<Scan> scans = new ArrayList<>();
    private ScansListAdapter adapter = null;
    private SharedPreferences sharedPrefs;

    private ProgressBar scanProgress;
    public CoordinatorLayout coordinatorLayout;
    private ListView scanListView;
    public ProgressDialog sharedProgressDialog;

    private final Context context = this;
    private int currentEabi;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        NMAP_DOWNLOAD_URL = getResources().getString(R.string.default_update_url);
        NMAP_VERSION_URL  = getResources().getString(R.string.default_version_url);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        scanProgress = (ProgressBar) findViewById(R.id.scan_progress);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator);
        scanListView = (ListView) findViewById(R.id.scans_list);
        setSupportActionBar(toolbar);

        adapter = new ScansListAdapter(this, scans);
        scanListView.setAdapter(adapter);
        scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ScanDetailActivity.class);
                intent.putExtra(Extras.SCAN_EXTRA, scans.get(position));
                context.startActivity(intent);
            }
        });

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        NMAP_BINARY_FILE = getFilesDir().getParent() + "/bin/nmap";
        editor.putString(getString(R.string.nmap_binary_path), NMAP_BINARY_FILE);
        editor.apply();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newScan();
            }
        });

        sharedProgressDialog = new ProgressDialog(this);
        sharedProgressDialog.setMessage(getString(R.string.dlg_progress_title_download));
        sharedProgressDialog.setIndeterminate(true);
        sharedProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        sharedProgressDialog.setCancelable(true);

        try {
            installNmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ScansListAdapter getAdapter() {
        return adapter;
    }

    public Scan getNewScan() {
        return newScan;
    }

    public String doNextEabi() {
        switch (currentEabi++) {
            case 0:
                return Build.CPU_ABI;
            case 1:
                return Build.CPU_ABI2;
        }
        return null;
    }

    public void runScan(Scan scan) {
        showRunAlert(getResources().getString(R.string.run_alert));
        scan.run(context, NMAP_BINARY_FILE);

        scanProgress.setIndeterminate(true);
    }

    /* Private methods */

    private void onListItemSelect(int position, View view) {
        adapter.toggleSelection(position);
        boolean hasSelectedElements = adapter.getSelectedCount() > 0;

        if (hasSelectedElements && actionMode == null) {
            // Has selected items but action mode is not initiated
            actionMode = startActionMode(new ActionModeCallback());
            actionMode.setTitle(adapter.getSelectedCount() + " items selected");
        } else if (!hasSelectedElements && actionMode != null) {
            // Action mode is initiated but there are no selected items
            actionMode.finish();
        }  else if (hasSelectedElements && actionMode != null) {
            // Has selected items and action mode is initiated
            actionMode.setTitle(adapter.getSelectedCount() + " items selected");
        }

        if (adapter.getSelectedIds().get(position)) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    private void newScan() {

        newScan = new Scan();

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.new_scan)
                .items(R.array.scan_types)
                .alwaysCallSingleChoiceCallback()
                .cancelable(false)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (text.equals( getResources().getString(R.string.net_scan) )) {
                            newScan.setType(ScanTypes.NETWORK_SCAN);
                        } else if (text.equals( getResources().getString(R.string.host_scan) )) {
                            newScan.setType(ScanTypes.HOST_SCAN);
                        }
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        return true;
                    }
                })
                .negativeText(R.string.cancel)
                .positiveText(R.string.next)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (newScan.getType() == ScanTypes.NETWORK_SCAN) {
                            newNetworkScan();
                        } else if (newScan.getType() == ScanTypes.HOST_SCAN) {
                            newHostScan();
                        }
                    }
                })
                .show();
        // Disable positive button until an option is selected
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
    }

    private void newHostScan() {
        MaterialDialog dialog = newBaseScan(R.string.host_scan_title, R.layout.new_host_scan_dialog);
        dialog.show();
    }

    private void newNetworkScan() {
        MaterialDialog dialog = newBaseScan(R.string.network_scan_title, R.layout.new_network_scan_dialog);
        View view = dialog.getCustomView();
        EditText target = (EditText) view.findViewById(R.id.input_target);
        target.setText(NetworkHelper.getNetworkAddress());
        //target.setFocusable(false);
        dialog.show();
    }

    private MaterialDialog newBaseScan(int titleRes, int layoutRes) {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(titleRes)
                .customView(layoutRes , true)
                .positiveText(R.string.done)
                .negativeText(R.string.back)
                .neutralText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        newScan();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        newScan = configureScanFromDialog(dialog, newScan);
                        if (newScan.getTarget() != null) {
                            runScan(newScan);
                        }
                    }
                })
                .cancelable(false)
                .build();

        return dialog;
    }

    private void showRunAlert(String msg) {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
    }

    private Scan configureScanFromDialog(MaterialDialog dialog, Scan scan) {
        View view = dialog.getCustomView();
        EditText nameEditText = (EditText) view.findViewById(R.id.input_name);
        EditText targetEditText = (EditText) view.findViewById(R.id.input_target);
        Spinner  intensitySpinner = (Spinner) view.findViewById(R.id.intensity_spinner);

        scan.setName(nameEditText.getText().toString());
        String intensity = ScanHelper.intensityKeyFromValue(this, intensitySpinner.getSelectedItem().toString());
        scan.setIntensity(intensity);

        String target = targetEditText.getText().toString();
        // if it is NOT an IP address try to resolve the name
        if ( !Patterns.IP_ADDRESS.matcher(target).matches() ) {     // TODO IPv6
            new DnsNameResolutionTask(context).execute(target);
        } else {
            scan.setTarget(target);
        }

        return scan;
    }

    private void installNmap() throws Exception {
        if (!isBinaryHere(NMAP_BINARY_FILE)) {
            askToDownload();
        }
    }

    private void askToDownload() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                downloadAll();
            }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.dlg_ask2download))
                .setPositiveButton(getString(R.string.dlg_ask2download_yes), dialogClickListener)
                .setNegativeButton(getString(R.string.dlg_ask2download_no), dialogClickListener)
                .show();
    }

    private void downloadAll () {
        currentEabi = 0;
        final SimpleHttpTask versionTask = new SimpleHttpTask(this);
        String versionurl = NMAP_VERSION_URL + "/nmap-latest.txt";
        versionTask.execute(versionurl);

        sharedProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                versionTask.cancel(true);
            }
        });
    }


    private boolean isBinaryHere(String binary) {
        File binaryFile = new File(binary);
        return binaryFile.canExecute();
    }

    /* Lifecycle methods */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** ActionModeCallback inner class **/

    public class ActionModeCallback implements ActionMode.Callback {

        private int statusBarColor;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = getWindow().getStatusBarColor();
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_delete:
                    SparseBooleanArray selected = adapter.getSelectedIds();
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        Scan toDelete = adapter.getItem(selected.keyAt(i));
                        adapter.delete(toDelete);
                    }
                    showUndoDelete();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(statusBarColor);
            }
            adapter.removeSelection();
            actionMode = null;
        }

        private void showUndoDelete() {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, getString(R.string.scan_deleted), Snackbar.LENGTH_LONG)
                    .setAction( getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Feature not implemented, sorry :(", Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });

            snackbar.show();
        }
    }
}
