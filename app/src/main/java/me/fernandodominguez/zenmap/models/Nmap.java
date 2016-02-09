package me.fernandodominguez.zenmap.models;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import me.fernandodominguez.zenmap.R;

/**
 * Created by fernando on 28/12/15.
 */
public class Nmap {

    private Context context;
    private String binary;

    private String target;

    public Nmap(Context context, String binary) {
        this.context = context;
        this.binary  = binary;
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

    public String osScan(String target) throws IOException, InterruptedException {
        return executeAsRoot(context.getResources().getString(R.string.os_scan_opts), target);
    }

    private String execute(String options, String target) throws IOException, InterruptedException {
        String xml = context.getResources().getString(R.string.xml_opt);
        List<String> lines = Shell.SH.run(binary + " " + options + " " + xml + " " + target);

        String output = "";
        for (String line : lines) {
            output += "\n" + line;
        }

        return output.trim();
    }

    private String executeAsRoot(String options, String target) throws IOException, InterruptedException {
        String xml = context.getResources().getString(R.string.xml_opt);
        List<String> lines = Shell.SU.run(binary + " " + options + " " + xml + " " + target);

        String output = "";
        for (String line : lines) {
            output += "\n" + line;
        }

        return output.trim();
    }
}
