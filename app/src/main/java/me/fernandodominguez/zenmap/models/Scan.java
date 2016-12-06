package me.fernandodominguez.zenmap.models;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by fernando on 28/12/15.
 */

public class Scan implements Serializable {
    private String name;
    private int type;
    private String target;
    private String intensity;
    private ScanResult scanResult;

    /* Constructors */

    public Scan() {
    }

    public Scan(String target) {
        this.target = target;
    }

    public Scan(String target, String intensity) {
        this.target = target;
        this.intensity = intensity;
    }

    public Scan(String target, int type) {
        this.type = type;
        this.target = target;
    }

    public Scan(String target, int type, String intensity) {
        this.type = type;
        this.target = target;
        this.intensity = intensity;
    }

    /* Public methods */

    public void run(Context context, String binary) {
        new NmapExecutor(context, binary).execute(this);
    }

    public String getTitle() {
        if (getName() == null ) {
            return getTarget();
        } else {
            return getName();
        }
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

    public ScanResult getScanResult() { return scanResult; }
}
