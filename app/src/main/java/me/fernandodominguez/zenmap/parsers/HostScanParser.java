package me.fernandodominguez.zenmap.parsers;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.host.Service;
import me.fernandodominguez.zenmap.models.host.ServiceStatus;

/**
 * Created by fernando on 30/12/15.
 */
public class HostScanParser {

    private String ns = null;

    public HostScan parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        HostScan hostScan = new HostScan();
        List<Service> services = new ArrayList<>();
        String hostname  = null;
        String address   = null;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("address")) {
                address = readAddress(parser);
            } else if (name.equals("services")) {
                services = readPorts(parser);
            } else if (name.equals("hostname")) {
                hostname = readHostname(parser);
            } else if (name.equals("finished")) {
                hostScan.setEndTime( Long.parseLong(parser.getAttributeValue(null, "time")) );
                hostScan.setElapsed( Float.parseFloat(parser.getAttributeValue(null, "elapsed")) );
            } else if (name.equals("nmaprun")) {
                hostScan.setStartTime( Long.parseLong(parser.getAttributeValue(null, "start")) );
            }
        }
        hostScan.setServices(services);
        hostScan.setHostname(hostname);
        hostScan.setAddress(address);
        return hostScan;
    }

    private List<Service> readPorts(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "services");
        List<Service> services = new ArrayList<>();

        int depth = parser.getDepth();
        while (!(parser.next() == XmlPullParser.END_TAG && parser.getDepth() == depth)) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("service")) {
                services.add(readPort(parser));
            }
        }
        return services;
    }

    private Service readPort(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "service");

        ServiceStatus status = null;
        String protocol = null;
        String port = null;
        String service = null;
        String version = null;

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
                version = readVersion(parser);
            }
        }
        Service srvc = new Service(protocol, port, service, status);
        if(version != null) srvc.setVersion(version);
        return srvc;
    }

    private String readHostname(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "hostname");
        return parser.getAttributeValue(null, "name");
    }

    private String readService(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service");
        return parser.getAttributeValue(null, "name");
    }

    private String readVersion(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "service");
        String product = parser.getAttributeValue(null, "product");
        String version = parser.getAttributeValue(null, "version");
        String extra = parser.getAttributeValue(null, "extrainfo");
        return product + " " + version + " " + extra;
    }

    private ServiceStatus readStatus(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "state");
        String state = parser.getAttributeValue(null, "state");
        String reason = parser.getAttributeValue(null, "reason");
        return new ServiceStatus(state, reason);
    }

    private String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "address");
        String address = parser.getAttributeValue(null, "addr");
        return address;
    }
}
