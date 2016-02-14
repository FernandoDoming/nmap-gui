package me.fernandodominguez.zenmap.async;

import android.content.Context;
import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.UnknownHostException;

import me.fernandodominguez.zenmap.activities.MainActivity;
import me.fernandodominguez.zenmap.models.Scan;

/**
 * Created by fernando on 14/02/16.
 */
public class DnsNameResolutionTask extends AsyncTask<String,Integer,String> {

    private Context context;

    public DnsNameResolutionTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String name = params[0];
        try {
            return InetAddress.getByName(name).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return name;
    }

    @Override
    protected void onPostExecute(String ip) {
        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            Scan scan = activity.getNewScan();
            scan.setTarget(ip);
            activity.runScan(scan);
        }
    }
}
