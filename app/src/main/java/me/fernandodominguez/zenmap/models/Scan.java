package me.fernandodominguez.zenmap.models;

import android.content.Context;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.network.NetworkScan;

/**
 * Created by fernando on 28/12/15.
 */
@Table(name = "Scans")
public class Scan extends Model implements Serializable {

    @Column(name = "Name")
    private String name;

    @Column(name = "Type")
    private int type;

    @Column(name = "Target")
    private String target;

    @Column(name = "Intensity")
    private String intensity;

    @Column(name = "ScanResult")
    private ScanResult scanResult;

    /* Constructors */

    public Scan() {
        super();
    }

    /* Public methods */

    public void run(Context context, String binary) {
        new NmapExecutor(context, binary).execute(this);
    }

    public static List<Scan> all() {
        List<Scan> scans = new Select().all().from(Scan.class).execute();
        for (Scan scan : scans) {
            scan.getScanResult();
        }
        return scans;
    }

    public void saveWithChildren() {
        this.save();
        if (scanResult != null) {
            scanResult.scan = this;
            scanResult.saveWithChildren();
        }
    }

    public ScanResult getScanResult() {
        if (scanResult == null) {
            try {
                scanResult = getMany(HostScan.class, "Scan").get(0);
            } catch (IndexOutOfBoundsException e) {
                Log.i(this.getClass().getName(), this + " has no HostScans");
            }
            try {
                scanResult = getMany(NetworkScan.class, "Scan").get(0);
            } catch (IndexOutOfBoundsException e) {
                Log.i(this.getClass().getName(), this + " has no NetworkScans");
            }
        }
        scanResult = scanResult.populate();
        return scanResult;
    }

    /* Getters & setters */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public void setScanResult(ScanResult scanResult) {
        this.scanResult = scanResult;
    }
}
