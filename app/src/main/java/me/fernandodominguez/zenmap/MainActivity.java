package me.fernandodominguez.zenmap;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.fernandodominguez.zenmap.constants.ScanTypes;
import me.fernandodominguez.zenmap.helpers.FileHelper;
import me.fernandodominguez.zenmap.models.Nmap;
import me.fernandodominguez.zenmap.models.Scan;

public class MainActivity extends AppCompatActivity {

    private final String NMAP_BINARY_FILE = "nmap";

    private Scan newScan = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newScan();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        try {
            installNmap();
            Nmap nmap = new Nmap(this);
            Log.d("NMAP VERSION", nmap.version());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Private methods */

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
                    }
                })
                .cancelable(false)
                .show();
    }

    private Scan configureScanFromDialog(MaterialDialog dialog, Scan scan) {
        View view = dialog.getCustomView();
        EditText targetEditText = (EditText) view.findViewById(R.id.input_target);
        Spinner  intensitySpinner = (Spinner) view.findViewById(R.id.intensity_spinner);

        scan.setTarget(targetEditText.getText().toString());
        scan.setIntensity(intensitySpinner.getSelectedItem().toString());

        return scan;
    }

    private void installNmap() throws Exception {

        String dir = getFilesDir() + "/bin/";
        File nmap = new File(dir + NMAP_BINARY_FILE);
        if (nmap.exists()) return;

        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
            for (String file : files) {
                if (file.equals(NMAP_BINARY_FILE)) {
                    InputStream stream = this.getAssets().open(file);
                    new File(dir).mkdir();
                    OutputStream output = new BufferedOutputStream(new FileOutputStream(dir + NMAP_BINARY_FILE));

                    byte data[] = new byte[1024];
                    int count;

                    while ((count = stream.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    FileHelper.chmod(new File(dir + NMAP_BINARY_FILE), 0550);
                    output.flush();
                    output.close();
                    stream.close();
                }
            }

        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

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
}
