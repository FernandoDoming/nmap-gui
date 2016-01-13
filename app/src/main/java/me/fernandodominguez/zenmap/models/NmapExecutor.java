package me.fernandodominguez.zenmap.models;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.activities.MainActivity;
import me.fernandodominguez.zenmap.constants.ScanTypes;
import me.fernandodominguez.zenmap.parsers.HostScanParser;
import me.fernandodominguez.zenmap.parsers.NetworkScanParser;

/**
 * Created by fernando on 29/12/15.
 */
public class NmapExecutor extends AsyncTask<Scan, Integer, ScanResult> {

    private Context context;
    private Nmap nmap;
    private Scan scan;

    public NmapExecutor(Context context) {
        this.context = context;
        this.nmap = new Nmap(context);
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected ScanResult doInBackground(Scan... params) {
        scan = params[0];
        String output = null;
        ScanResult scanResult = null;

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
                scanResult.saveWithChildren();
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

        return scanResult;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ScanResult result) {
        ((MainActivity) context).getAdapter().addScan(result);
        ProgressBar scanProgress = (ProgressBar) ((Activity) context).findViewById(R.id.scan_progress);
        scanProgress.setIndeterminate(false);
    }

    /* XML Parsing */

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
