package me.fernandodominguez.zenmap.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import me.fernandodominguez.zenmap.R;

/*
* Attribution of this file is mainly to kost @ github
* */

public class DownloadTask extends AsyncTask<String,Integer,String> {
    public final Context context;
    public PowerManager.WakeLock mWakeLock;
    public String dlurl;
    public String dlfn;
    public String dlprefix;
    private ProgressDialog sharedProgressDialog;

    public DownloadTask(Context context, ProgressDialog sharedProgressDialog) {
        this.context = context;
        this.sharedProgressDialog = sharedProgressDialog;
    }

    @Override
    protected String doInBackground(String... sParm) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            dlurl    = sParm[0];
            dlfn     = sParm[1];
            dlprefix = sParm[2];
            URL url = new URL(sParm[0]);
            Log.i("NetworkMapper", "Downloading URL: " + url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            Log.i("NetworkMapper","Downloading to: "+sParm[1]);
            output = new FileOutputStream(sParm[1]);


            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    this.publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {}

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        sharedProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        // super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        sharedProgressDialog.setIndeterminate(false);
        sharedProgressDialog.setMax(100);
        sharedProgressDialog.setProgress(progress[0]);
        sharedProgressDialog.setMessage(context.getString(R.string.dlg_progress_title_download));
    }
}
