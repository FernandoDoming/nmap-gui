package me.fernandodominguez.zenmap.models;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

import me.fernandodominguez.zenmap.MainActivity;
import me.fernandodominguez.zenmap.R;
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
    protected ScanResult doInBackground(Scan... params) {
        scan = params[0];
        String output = null;

        try {
            if (scan.getIntensity().equals(ScanTypes.INTENSE_SCAN)) {
                output = nmap.intenseScan(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.INTENSE_SCAN_ALL_TCP_PORTS)) {
                output = nmap.intenseScanAllTcpPorts(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.HOST_DISCOVERY)) {
                output = nmap.hostDiscovery(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.REGULAR_SCAN)) {
                output = nmap.regularScan(scan.getTarget());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Parse the result into POJOs
        XmlPullParser parser = Xml.newPullParser();
        ScanResult scanResult = null;
        try {
            parser.setInput(new StringReader(output));
            if (scan.getType() == ScanTypes.HOST_SCAN) {
                scanResult = new HostScanParser().parse(parser);
            } else if (scan.getType() == ScanTypes.NETWORK_SCAN) {
                scanResult = new NetworkScanParser().parse(parser);
                scanResult.setTarget(scan.getTarget());
            }
            if (scanResult != null) {
                scanResult.setOutput(output);
                scanResult.saveWithChildren();
            }
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
        textView.setText(result.getOutput());
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
