package me.fernandodominguez.zenmap.async;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.activities.MainActivity;
import me.fernandodominguez.zenmap.helpers.FileHelper;

/*
* Attribution of this file is mainly to kost @ github
* */

public class SimpleHttpTask extends AsyncTask<String, Void, String> {
    private final Context context;
    private PowerManager.WakeLock mWakeLock;

    private boolean doneFallback;
    private String archs;

    public SimpleHttpTask(Context context) {
        this.context = context;
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
        if (context instanceof MainActivity) {
            ((MainActivity) context).sharedProgressDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String urllink = params[0];
        String version;

        Integer count = 0;
        Integer maxTries = 3;
        while(true) {
            URL url;
            try {
                url = new URL(urllink);
                Log.i("NetworkMapper", "Downloading from URL: " + url.toString() + "\n");
                HttpURLConnection httpurlconn = (HttpURLConnection) url.openConnection();
                httpurlconn.setInstanceFollowRedirects(true);
                httpurlconn.connect();

                InputStream in = new BufferedInputStream(httpurlconn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

                version = bufferedReader.readLine();
                in.close();
                httpurlconn.disconnect();
                Log.i("NetworkMapper", "Latest version: " + version);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                return sharedPreferences.getString("nmap_version", version);
            } catch (MalformedURLException e) {
                // throw new RuntimeException(e);
                Log.e("NetworkMapper", "MalformedURL: " + urllink);
                return null;
            } catch (IOException e) {
                // throw new RuntimeException(e);
                Log.e("NetworkMapper", "IOException: " + urllink);
                if (++count == maxTries) {
                    Log.e("NetworkMapper", "Reached maximum tries");
                    return null;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    Log.e("NetworkMapper","ThreadSleep");
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();

        if (result == null) { return; }

        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            activity.sharedProgressDialog.dismiss();
            doneFallback = false;
            archs = activity.doNextEabi();
            downloadBinary(result, archs);
        }
    }

    /* Helper methods */

    private void downloadBinary(final String prefixfn, String eabi) {
        String appdir = context.getFilesDir().getParent();
        String bindir = appdir + "/bin";
        String dldir = appdir + "/dl";
        String root = Environment.getExternalStorageDirectory().toString();
        final String datadldir = root + "/opt";

        Log.i("NetworkMapper", "Using bindir:" + bindir + ", dldir:" + dldir);
        FileHelper.makedir(bindir);
        FileHelper.makedir(dldir);

        // Parse version number. Group 0 is major, group 1 is minor
        Pattern p = Pattern.compile("nmap-(\\d+)\\.(\\d+)");
        Matcher m = p.matcher(prefixfn);

        if (!m.matches()) {
            Log.e("NetworkMapper", "Could not parse nmap version numbers. Aborting download");
            return;
        }

        String major = m.group(1);
        String minor = m.group(2);

        String binaryfn = prefixfn+"-binaries-"+eabi+".zip";
        Log.i("NetworkMapper", "Using binaryfn: " + binaryfn);

        if (context instanceof MainActivity) {

            final MainActivity mainActivity = (MainActivity) context;
            final DownloadTask binaryTask = new DownloadTask(context, mainActivity.sharedProgressDialog) {
                @Override
                protected void onPostExecute(String result) {
                    mainActivity.sharedProgressDialog.dismiss();
                    if (result != null) {
                        mWakeLock.release();
                        String nextEabi = mainActivity.doNextEabi();
                        archs = archs + ":" + nextEabi;
                        if (nextEabi == null) {
                            if (doneFallback) {
                                Toast.makeText(context, context.getString(R.string.toast_dowload_binary_error) + result, Toast.LENGTH_LONG).show();
                            } else {
                                if (archs.contains("mips")) {
                                    nextEabi = "mips";
                                }
                                if (archs.contains("x86")) {
                                    nextEabi = "x86";
                                }
                                if (archs.contains("arm")) {
                                    nextEabi = "armeabi";
                                }
                                doneFallback = true;
                                downloadBinary(prefixfn, nextEabi);
                            }
                        } else {
                            downloadBinary(prefixfn, nextEabi);
                        }
                        return;
                    }

                    Toast.makeText(context, context.getString(R.string.toast_download_binary_ok), Toast.LENGTH_SHORT).show();

                    String bindir = context.getFilesDir().getParent() + "/bin/";

                    final UnzipTask binzipTask = new UnzipTask(this.context, mainActivity.sharedProgressDialog) {
                        @Override
                        protected void onPostExecute(String result) {
                            mainActivity.sharedProgressDialog.dismiss();
                            Log.i("NetworkMapper", "Completed. Directory: " + result);
                            String bindir = context.getFilesDir().getParent() + "/bin/";
                            String[] commands = {"ncat", "ndiff", "nmap", "nping"};
                            try {
                                for (String singlecommand : commands) {
                                    Runtime.getRuntime().exec("/system/bin/chmod 755 " + bindir + singlecommand);
                                }
                            } catch (IOException e) {
                                Log.e("NetworkMapper", "IO Exception: \n" + e.toString());
                            }

                            Log.i("NetworkMapper", "Data: Using prefix: " + dlprefix);

                            File myDir = new File(datadldir + "/" + dlprefix);

                            if (myDir.isDirectory()) {
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            downloadData(dlprefix);
                                        } else {
                                            SharedPreferences sharedPref =
                                                    context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPref.edit();
                                            editor.putString(context.getString(R.string.nmapbin_version), prefixfn);
                                            editor.apply();
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(context.getString(R.string.dlg_ask2downloaddata))
                                        .setPositiveButton(context.getString(R.string.dlg_ask2download_yes), dialogClickListener)
                                        .setNegativeButton(context.getString(R.string.dlg_ask2download_no), dialogClickListener)
                                        .show();
                            } else {
                                downloadData(dlprefix);
                            }
                        }
                    };
                    binzipTask.execute(dlfn, bindir, dlprefix);

                    mainActivity.sharedProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            binzipTask.cancel(true);
                        }
                    });

                    mWakeLock.release();
                }
            };
            binaryTask.execute(mainActivity.NMAP_DOWNLOAD_URL + "/v" + major + "." + minor + "/" + binaryfn,
                               dldir + "/" + binaryfn,
                               prefixfn,
                               appdir);

            mainActivity.sharedProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    binaryTask.cancel(true);
                }
            });
        }
    }

    private void downloadData(final String prefixfn) {
        String appdir = context.getFilesDir().getParent();
        final String datadldir = appdir + "/dl";
        final String datadir = appdir + "/data/";
        final String bindir  = appdir + "/bin/";

        // Parse version number. Group 0 is major, group 1 is minor
        Pattern p = Pattern.compile("nmap-(\\d+)\\.(\\d+)");
        Matcher m = p.matcher(prefixfn);

        if (!m.matches()) {
            Log.e("NetworkMapper", "Could not parse nmap version numbers. Aborting download");
            return;
        }

        final String major = m.group(1);
        final String minor = m.group(2);

        Log.i("NetworkMapper", "Using datadldir: " + datadldir);
        FileHelper.makedir(datadldir);

        String datafn = prefixfn + "-data.zip";

        if (context instanceof MainActivity) {
            final MainActivity mainActivity = (MainActivity) context;
            final DownloadTask dataTask = new DownloadTask(context, mainActivity.sharedProgressDialog) {
                @Override
                protected void onPostExecute(String result) {
                    mainActivity.sharedProgressDialog.dismiss();
                    if (result != null) {
                        mWakeLock.release();
                        return;
                    }

                    final UnzipTask datazipTask = new UnzipTask(this.context, mainActivity.sharedProgressDialog) {
                        @Override
                        protected void onPostExecute(String result) {
                            SharedPreferences sharedPref =
                                    context.getSharedPreferences(context.getPackageName() + "_preferences",
                                                                 Context.MODE_PRIVATE);
                            final String oldver = sharedPref.getString(context.getString(R.string.nmapbin_version), "");

                            if (!oldver.equals("") && !oldver.equals(prefixfn)) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(context.getString(R.string.nmapbin_version), prefixfn);
                                editor.apply();
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            Log.i("NetworkMapper", "deleting recursively!");
                                            FileHelper.DeleteRecursive(new File(datadldir + "/" + oldver));
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(context.getString(R.string.dlg_ask2deletedata) + " " + oldver)
                                        .setPositiveButton(context.getString(R.string.dlg_ask2delete_yes), dialogClickListener)
                                        .setNegativeButton(context.getString(R.string.dlg_ask2delete_no), dialogClickListener)
                                        .show();
                            } else {
                                Log.i("NetworkMapper", "No need to delete recursively!");
                            }
                            // Move the support files to the bin dir
                            // /nmap-x.yy/share/nmap/
                            String support = datadir + "/nmap-" + major + "." + minor +
                                             "/share/nmap/";
                            File supportDir = new File(support);
                            if (supportDir.isDirectory()) {
                                File[] content = supportDir.listFiles();
                                for (File f : content) {
                                    if (!f.renameTo( new File(bindir + f.getName()) )) {
                                        Log.e("SimpleHTTPTask", "Moving " + f.getName() + " failed");
                                    }
                                }
                            }
                            mainActivity.sharedProgressDialog.dismiss();
                            Toast.makeText(context, context.getString(R.string.toast_data_extraction_ok), Toast.LENGTH_SHORT).show();
                            Log.i("NetworkMapper", "Data Completed. Directory: " + result);
                        }


                    };
                    datazipTask.execute(dlfn, datadir, dlprefix);

                    mainActivity.sharedProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            datazipTask.cancel(true);
                        }
                    });

                    mWakeLock.release();
                }
            };
            String dataURL = mainActivity.NMAP_DOWNLOAD_URL + "/v" + major + "." + minor + "/" + datafn;
            Log.i("NetworkMapper", "Executing using: " + dataURL);
            dataTask.execute(dataURL, datadldir + "/" + datafn, prefixfn, datadldir);

            mainActivity.sharedProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dataTask.cancel(true);
                }
            });
        }
    }
}
