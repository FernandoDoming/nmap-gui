package me.fernandodominguez.zenmap.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.activities.HostDetailActivity;
import me.fernandodominguez.zenmap.activities.MainActivity;
import me.fernandodominguez.zenmap.activities.ScanDetailActivity;
import me.fernandodominguez.zenmap.constants.ScanTypes;
import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.network.Host;
import me.fernandodominguez.zenmap.models.network.HostStatus;
import me.fernandodominguez.zenmap.parsers.HostScanParser;
import me.fernandodominguez.zenmap.parsers.NetworkScanParser;

/**
 * Created by fernando on 29/12/15.
 */
public class NmapExecutor extends AsyncTask<Scan, Integer, Scan> {

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    private Nmap nmap;
    private Scan scan;

    public NmapExecutor(Context context, String binary) {
        this.context = context;
        this.nmap = new Nmap(context, binary);
    }

    @Override
    protected void onPreExecute() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        Log.d(this.getClass().getName(), "Acquiring wakelock");
        mWakeLock.acquire();
    }

    @Override
    protected Scan doInBackground(Scan... params) {
        scan = params[0];
        String output = null;
        ScanResult scanResult = null;
        Log.i(this.getClass().getName(), "Starting scan for " + scan.getTarget());
        Log.i(this.getClass().getName(), "Scan intensity: "   + scan.getIntensity());

        try {
            Method method = nmap.getClass().getMethod(scan.getIntensity(), String.class);
            output = (String) method.invoke(nmap, scan.getTarget());

            // Parse the result into POJOs
            XmlPullParser parser = Xml.newPullParser();

            parser.setInput(new StringReader(output));
            if (scan.getType() == ScanTypes.HOST_SCAN) {
                scanResult = new HostScanParser().parse(parser);
            } else if (scan.getType() == ScanTypes.NETWORK_SCAN) {
                scanResult = new NetworkScanParser().parse(parser);
                scanResult.setTarget(scan.getTarget());
            } else {
                Log.e(this.getClass().getName(), "Scan is neither HOST nor NETWORK. Scan will fail.");
            }

            if (scanResult != null) {
                scanResult.setName(scan.getName());
                scanResult.setOutput(output);
                scan.setScanResult(scanResult);
            } else {
                Log.w(this.getClass().getName(), "Scan " + scan.getTitle() + " result was null");
            }

        } catch (NoSuchMethodException | SecurityException e) {
            Log.e(this.getClass().getName(), scan.getIntensity() + " is not supported");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(this.getClass().getName(), "Invocation for " + scan.getIntensity()
                                            + " in " + nmap + " failed");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(this.getClass().getName(), "Illegal access made: " + scan.getIntensity()
                    + " in " + nmap );
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        scan.setScanResult(scanResult);
        return scan;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(final Scan scan) {

        // TODO: maybe I should implement this method on each activity specific instantiation
        if (context instanceof MainActivity) {

            MainActivity mainActivity = (MainActivity) context;
            ProgressBar scanProgress = (ProgressBar) mainActivity.findViewById(R.id.scan_progress);
            scanProgress.setIndeterminate(false);

            if (scan.getScanResult() == null) {
                Snackbar snackbar = Snackbar
                        .make(mainActivity.coordinatorLayout,
                              context.getString(R.string.scan_failed),
                              Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                mainActivity.getAdapter().addScan(scan);

                Snackbar snackbar = Snackbar
                        .make(mainActivity.coordinatorLayout,
                                context.getString(R.string.scan_done, scan.getTitle()),
                                Snackbar.LENGTH_LONG)
                        .setAction(context.getString(R.string.view), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, ScanDetailActivity.class);
                                intent.putExtra("scan", scan);
                                context.startActivity(intent);
                            }
                        });
                snackbar.show();
            }

        } else if (context instanceof ScanDetailActivity) {

            Activity activity = (Activity) context;
            activity.finish();
            activity.startActivity(activity.getIntent());

        } else if (context instanceof HostDetailActivity) {

            HostDetailActivity activity = (HostDetailActivity) context;
            if (scan.getScanResult() != null) {
                HostScan result = (HostScan) scan.getScanResult();
                if (result.getHost() != null) {
                    Host host = result.getHost();

                    if ( host.getStatus().toString().equals(HostStatus.DOWN) ) {
                        Snackbar.make(activity.findViewById(R.id.main_content),
                                    context.getString(R.string.host_down),
                                    Snackbar.LENGTH_LONG).show();
                    } else {
                        activity.addHostDetails(
                                (ViewGroup) activity.findViewById(R.id.host_general_properties), host
                        );
                    }
                }
            }
            ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.scan_progress);
            if (progressBar != null) {
                progressBar.setIndeterminate(false);
            }
        }

        Log.i(this.getClass().getName(), "Scan finished for " + scan.getTarget());
        mWakeLock.release();
        Log.d(this.getClass().getName(), "Wakelock released");
    }
}
