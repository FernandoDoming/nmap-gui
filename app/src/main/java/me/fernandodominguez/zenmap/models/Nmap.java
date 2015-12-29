package me.fernandodominguez.zenmap.models;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.fernandodominguez.zenmap.R;

/**
 * Created by fernando on 28/12/15.
 */
public class Nmap {

    private Context context;
    private String binary;

    private String target;

    public Nmap(Context context) {
        this.context = context;
        this.binary  = context.getFilesDir() + "/bin/nmap";
    }

    public String version() throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.version_opts), "" );
    }

    public String regularScan(String target) throws IOException, InterruptedException {
        return execute( context.getString(R.string.regular_scan_opts), target );
    }

    public String intenseScan(String target) throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.intense_scan_opts), target );
    }

    public String intenseScanAllTcpPorts(String target) throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.intense_scan_all_tcp_opts), target );
    }

    public String hostDiscovery(String target) throws IOException, InterruptedException {
        return execute( context.getResources().getString(R.string.host_discovery_opts), target );
    }

    private String execute(String options, String target) throws IOException, InterruptedException {
        Process nmap = Runtime.getRuntime().exec(binary + " " + options + " " + target);
        nmap.waitFor();
        BufferedReader in = new BufferedReader(new InputStreamReader(nmap.getInputStream()));
        String output = "";

        String line   = null;
        while ((line = in.readLine()) != null) {
            output += "\n" + line;
        }

        return output;
    }
}
