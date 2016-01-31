package me.fernandodominguez.zenmap.parsers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.host.Port;
import me.fernandodominguez.zenmap.models.host.PortStatus;

/**
 * Created by fernando on 30/12/15.
 */
public class HostScanParser {

    private String ns = null;

    public HostScan parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        HostScan hostScan = new HostScan();
        List<Port> ports = new ArrayList<>();
        String hostname  = null;
        String address   = null;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("address")) {
                address = readAddress(parser);
            } else if (name.equals("ports")) {
                ports = readPorts(parser);
            } else if (name.equals("hostname")) {
                hostname = readHostname(parser);
            } else if (name.equals("finished")) {
                hostScan.setEndTime( Long.parseLong(parser.getAttributeValue(null, "time")) );
                hostScan.setElapsed( Float.parseFloat(parser.getAttributeValue(null, "elapsed")) );
            } else if (name.equals("nmaprun")) {
                hostScan.setStartTime( Long.parseLong(parser.getAttributeValue(null, "start")) );
            }
        }
        hostScan.setPorts(ports);
        hostScan.setHostname(hostname);
        hostScan.setAddress(address);
        return hostScan;
    }

    private List<Port> readPorts(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "ports");
        List<Port> ports = new ArrayList<>();

        int depth = parser.getDepth();
        while (!(parser.next() == XmlPullParser.END_TAG && parser.getDepth() == depth)) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("port")) {
                ports.add(readPort(parser));
            }
        }
        return ports;
    }

    private Port readPort(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "port");

        PortStatus status = null;
        String protocol = null;
        String port = null;
        String service = null;

        protocol = parser.getAttributeValue(null, "protocol");
        port     = parser.getAttributeValue(null, "portid");

        int depth = parser.getDepth();
        while (!(parser.next() == XmlPullParser.END_TAG && parser.getDepth() == depth)) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("state")) {
                status = readStatus(parser);
            } else if (name.equals("service")) {
                service = readService(parser);
            }
        }
        return new Port(protocol, port, service, status);
    }

    private String readHostname(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "hostname");
        return parser.getAttributeValue(null, "name");
    }

    private String readService(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service");
        return parser.getAttributeValue(null, "name");
    }

    private PortStatus readStatus(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "state");
        String state = parser.getAttributeValue(null, "state");
        String reason = parser.getAttributeValue(null, "reason");
        return new PortStatus(state, reason);
    }

    private String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "address");
        String address = parser.getAttributeValue(null, "addr");
        return address;
    }
}
