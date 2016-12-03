package me.fernandodominguez.zenmap.parsers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.models.network.Host;
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
            switch (name) {
                case "host":
                    Host host = new HostScanParser().readHost(parser);
                    if (host.isUp()) {
                        hosts.add(host);
                    }
                    break;
                case "finished":
                    networkScan.setEndTime(Long.parseLong(parser.getAttributeValue(null, "time")));
                    networkScan.setElapsed(Float.parseFloat(parser.getAttributeValue(null, "elapsed")));
                    networkScan.setSummary(parser.getAttributeValue(null, "summary"));
                    break;
                case "nmaprun":
                    networkScan.setStartTime(Long.parseLong(parser.getAttributeValue(null, "start")));
                    break;
            }
        }
        networkScan.setHosts(hosts);
        return networkScan;
    }
}
