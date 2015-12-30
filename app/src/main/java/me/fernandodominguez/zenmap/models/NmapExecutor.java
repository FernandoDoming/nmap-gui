package me.fernandodominguez.zenmap.models;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.constants.ScanTypes;
import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.network.Host;
import me.fernandodominguez.zenmap.models.network.HostStatus;
import me.fernandodominguez.zenmap.models.network.NetworkScan;

/**
 * Created by fernando on 29/12/15.
 */
public class NmapExecutor extends AsyncTask<Scan, Integer, String> {

    private Context context;
    private Nmap nmap;
    private Scan scan;

    private String ns = null;

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
        scan = params[0];
        String result = null;

        try {
            if (scan.getIntensity().equals(ScanTypes.INTENSE_SCAN)) {
                result = nmap.intenseScan(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.INTENSE_SCAN_ALL_TCP_PORTS)) {
                result = nmap.intenseScanAllTcpPorts(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.HOST_DISCOVERY)) {
                result = nmap.hostDiscovery(scan.getTarget());
            } else if (scan.getIntensity().equals(ScanTypes.REGULAR_SCAN)) {
                result = nmap.regularScan(scan.getTarget());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Parse the result into POJOs
        XmlPullParser parser = Xml.newPullParser();
        ScanResult scanResult = null;
        try {
            parser.setInput(new StringReader(result));
            if (scan.getType() == ScanTypes.HOST_SCAN) {
                scanResult = parseHostScan(parser);
            } else if (scan.getType() == ScanTypes.NETWORK_SCAN) {
                scanResult = parseNetworkScan(parser);
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        textView.setText(result);

    }

    /* XML Parsing */

    private HostScan parseHostScan(XmlPullParser parser) {

        return null;
    }

    private NetworkScan parseNetworkScan(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Host> hosts = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("host")) {
                hosts.add(readHost(parser));
            }
        }
        return new NetworkScan(hosts);
    }

    private Host readHost(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "host");
        HostStatus status = null;
        String address = null;

        int depth = parser.getDepth();
        while (!(parser.next() == XmlPullParser.END_TAG && parser.getDepth() == depth)) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("status")) {
                status = readStatus(parser);
            } else if (name.equals("address")) {
                address = readAddress(parser);
            }
        }
        return new Host(address, status);
    }

    private HostStatus readStatus(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "status");
        String status = parser.getAttributeValue(null, "state");
        String reason = parser.getAttributeValue(null, "reason");
        //parser.require(XmlPullParser.END_TAG, ns, "status");
        return new HostStatus(status, reason);
    }

    private String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "address");
        String address = parser.getAttributeValue(null, "addr");
        return address;
    }

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
