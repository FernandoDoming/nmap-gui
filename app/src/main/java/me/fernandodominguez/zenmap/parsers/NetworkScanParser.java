package me.fernandodominguez.zenmap.parsers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.models.network.Host;
import me.fernandodominguez.zenmap.models.network.HostStatus;
import me.fernandodominguez.zenmap.models.network.NetworkScan;

/**
 * Created by fernando on 30/12/15.
 */
public class NetworkScanParser {

    private String ns = null;

    public NetworkScan parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Host> hosts = new ArrayList<>();

        NetworkScan networkScan = new NetworkScan();
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("host")) {
                hosts.add(readHost(parser));
            } else if (name.equals("finished")) {
                networkScan.setEndTime( Long.parseLong(parser.getAttributeValue(null, "time")) );
                networkScan.setElapsed( Float.parseFloat(parser.getAttributeValue(null, "elapsed")) );
                networkScan.setSummary( parser.getAttributeValue(null, "summary") );
            } else if (name.equals("nmaprun")) {
                networkScan.setStartTime( Long.parseLong(parser.getAttributeValue(null, "start")) );
            }
        }
        networkScan.setHosts(hosts);
        return networkScan;
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
}
