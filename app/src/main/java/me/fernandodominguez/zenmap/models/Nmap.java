package me.fernandodominguez.zenmap.models;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by fernando on 28/12/15.
 */
public class Nmap {

    private Context context;
    private String binary;

    public Nmap(Context context) {
        this.context = context;
        this.binary  = context.getFilesDir() + "/bin/nmap";
    }

    public String version() throws IOException, InterruptedException {
        return execute("-v");
    }

    private String execute(String options) throws IOException, InterruptedException {
        Process nmap = Runtime.getRuntime().exec(binary + " " + options);
        nmap.waitFor();
        BufferedReader in = new BufferedReader(new InputStreamReader(nmap.getInputStream()));
        String output = "";

        String line   = null;
        while ((line = in.readLine()) != null) {
            output += line;
        }

        return output;
    }
}
