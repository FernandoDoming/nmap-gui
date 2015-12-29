package me.fernandodominguez.zenmap.models;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.constants.ScanTypes;

/**
 * Created by fernando on 29/12/15.
 */
public class NmapExecutor extends AsyncTask<Scan, Integer, String> {

    private Context context;
    private Nmap nmap;

    private TextView textView;

    public NmapExecutor(Context context) {
        this.context = context;
        this.nmap = new Nmap(context);
    }

    @Override
    protected void onPreExecute() {
        textView = (TextView) ((Activity) context).findViewById(R.id.hello_world);
    }

    @Override
    protected String doInBackground(Scan... params) {
        Scan scan = params[0];
        String scanResult = null;

        try {
            if (scan.getIntensity().equals(ScanTypes.INTENSE_SCAN)) {
                scanResult = nmap.intenseScan(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.INTENSE_SCAN_ALL_TCP_PORTS)) {
                scanResult = nmap.intenseScanAllTcpPorts(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.HOST_DISCOVERY)) {
                scanResult = nmap.hostDiscovery(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.REGULAR_SCAN)) {
                scanResult = nmap.regularScan(scan.getTarget());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return scanResult;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        textView.setText(s);
    }
}
