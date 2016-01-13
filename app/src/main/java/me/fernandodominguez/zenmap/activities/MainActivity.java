package me.fernandodominguez.zenmap.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.adapters.ScansListAdapter;
import me.fernandodominguez.zenmap.constants.ScanTypes;
import me.fernandodominguez.zenmap.helpers.FileHelper;
import me.fernandodominguez.zenmap.helpers.ScanHelper;
import me.fernandodominguez.zenmap.models.Scan;
import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.network.NetworkScan;

public class MainActivity extends AppCompatActivity {

    private final String NMAP_BINARY_FILE = "nmap";

    private Scan newScan = null;
    private List<ScanResult> scans = new ArrayList<>();
    private ScansListAdapter adapter = null;

    private ProgressBar scanProgress;
    private ListView scanListView;

    private final Context context = this;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        scanProgress = (ProgressBar) findViewById(R.id.scan_progress);
        scanListView = (ListView) findViewById(R.id.scans_list);
        setSupportActionBar(toolbar);

        List<HostScan> hostScans = HostScan.all();
        List<NetworkScan> networkScans = NetworkScan.all();
        scans.addAll(hostScans);
        scans.addAll(networkScans);

        adapter = new ScansListAdapter(this, scans);
        scanListView.setAdapter(adapter);
        scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ScanDetailActivity.class);
                intent.putExtra("scan", scans.get(position));
                context.startActivity(intent);
            }
        });
        scanListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemSelect(position, view);
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newScan();
            }
        });

        try {
            installNmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        newBaseScan(R.string.host_scan_title, R.layout.new_host_scan_dialog);
    }

    private void newNetworkScan() {
        newBaseScan(R.string.network_scan_title, R.layout.new_network_scan_dialog);
    }

    private void newBaseScan(int titleRes, int layoutRes) {
        new MaterialDialog.Builder(this)
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
                        newScan.run(context);
                        scanProgress.setIndeterminate(true);
                    }
                })
                .cancelable(false)
                .show();
    }

    private Scan configureScanFromDialog(MaterialDialog dialog, Scan scan) {
        View view = dialog.getCustomView();
        EditText nameEditText = (EditText) view.findViewById(R.id.input_name);
        EditText targetEditText = (EditText) view.findViewById(R.id.input_target);
        Spinner  intensitySpinner = (Spinner) view.findViewById(R.id.intensity_spinner);

        scan.setName(nameEditText.getText().toString());
        scan.setTarget(targetEditText.getText().toString());
        String intensity = ScanHelper.intensityKeyFromValue(this, intensitySpinner.getSelectedItem().toString());
        scan.setIntensity(intensity);

        return scan;
    }

    private void installNmap() throws Exception {

        String dir = getFilesDir() + "/bin/";

        AssetManager assetManager = getAssets();
        String[] files = null;
        files = assetManager.list("bin");
        for (String file : files) {
            if (!new File(dir + file).exists()) {
                try {
                    //FIXME directories not copying
                    InputStream stream = this.getAssets().open("bin/" + file);
                    new File(dir).mkdir();
                    OutputStream output = new BufferedOutputStream(new FileOutputStream(dir + file));

                    byte data[] = new byte[1024];
                    int count;

                    while ((count = stream.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    if (file.equals(NMAP_BINARY_FILE)) FileHelper.chmod(new File(dir + NMAP_BINARY_FILE), 0550);
                    output.flush();
                    output.close();
                    stream.close();
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy" + file, e);
                }
            }
        }
    }

    public ScansListAdapter getAdapter() {
        return adapter;
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
                        ScanResult toDelete = adapter.getItem(selected.keyAt(i));
                        adapter.delete(toDelete);
                    }
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
    }
}
