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
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.activities.MainActivity;
import me.fernandodominguez.zenmap.activities.ScanDetailActivity;
import me.fernandodominguez.zenmap.constants.ScanTypes;
import me.fernandodominguez.zenmap.parsers.HostScanParser;
import me.fernandodominguez.zenmap.parsers.NetworkScanParser;

/**
 * Created by fernando on 29/12/15.
 */
public class NmapExecutor extends AsyncTask<Scan, Integer, Scan> {

    private Context context;
    public PowerManager.WakeLock mWakeLock;

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
            }
            if (scanResult != null) {
                scanResult.setName(scan.getName());
                scanResult.setOutput(output);
                scan.setScanResult(scanResult);
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
        } catch (XmlPullParserException | IOException e) {
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

        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.getAdapter().addScan(scan);
            ProgressBar scanProgress = (ProgressBar) mainActivity.findViewById(R.id.scan_progress);
            scanProgress.setIndeterminate(false);

            Snackbar snackbar = Snackbar
                    .make( mainActivity.coordinatorLayout,
                            context.getString(R.string.scan_done, scan.getName()),
                            Snackbar.LENGTH_LONG)
                    .setAction( context.getString(R.string.view), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ScanDetailActivity.class);
                            intent.putExtra("scan", scan);
                            context.startActivity(intent);
                        }
                    });
            snackbar.show();

        } else if (context instanceof ScanDetailActivity) {
            Activity activity = (Activity) context;
            activity.finish();
            activity.startActivity(activity.getIntent());
        }

        Log.i(this.getClass().getName(), "Scan finished for " + scan.getTarget());
        mWakeLock.release();
        Log.d(this.getClass().getName(), "Wakelock released");
    }
}
