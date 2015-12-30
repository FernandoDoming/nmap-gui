package me.fernandodominguez.zenmap.models;

/**
 * Created by fernando on 30/12/15.
 */
public abstract class ScanResult implements IScanResult {
    protected long startTime;
    protected long endTime;
    protected String scanStatus;
}
