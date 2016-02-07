package me.fernandodominguez.zenmap.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.helpers.FileHelper;

/**
 * Created by fernando on 01/02/16.
 */

public class UnzipTask extends AsyncTask<String,Integer,String> {
    private final Context context;
    private PowerManager.WakeLock mWakeLock;
    private ProgressDialog sharedProgressDialog;
    int per;
    public String dlprefix;
    int maxfiles;

    public UnzipTask(Context context, ProgressDialog progressDialog) {
        this.context = context;
        this.sharedProgressDialog = progressDialog;
    }

    @Override
    protected String doInBackground(String... sParm) {
        String zipfn=sParm[0];
        String dest=sParm[1];
        dlprefix = sParm[2];
        per=0;
        maxfiles=10;
        try {
            // set maximum to number of compress files
            ZipFile zip = new ZipFile(zipfn);
            maxfiles=zip.size();
            sharedProgressDialog.setMax(maxfiles);

            FileInputStream fin = new FileInputStream(zipfn);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("NetworkMapper", "Unzipping " + ze.getName());

                if (ze.isDirectory()) {
                    FileHelper.makedir(dest + ze.getName());
                } else {
                    per++;
                    publishProgress(per);

                    int size;
                    byte[] buffer = new byte[2048];

                    FileOutputStream outStream = new FileOutputStream(dest+ze.getName());
                    BufferedOutputStream bufferOut = new BufferedOutputStream(outStream, buffer.length);

                    while((size = zin.read(buffer, 0, buffer.length)) != -1) {
                        bufferOut.write(buffer, 0, size);
                    }

                    bufferOut.flush();
                    bufferOut.close();
                }

            }
            zin.close();
            new File(zipfn).delete(); // delete file after successful unzip
        } catch (Exception e) {
            Log.e("NetworkMapper", "unzip", e);
        }
        return dest;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
        sharedProgressDialog.setMessage(context.getString(R.string.dlg_progress_title_extraction));
        sharedProgressDialog.show();
    }

    protected void onProgressUpdate(Integer... progress) {
        sharedProgressDialog.setMax(maxfiles);
        sharedProgressDialog.setProgress(per);
    }

}
