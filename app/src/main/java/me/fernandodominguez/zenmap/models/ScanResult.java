package me.fernandodominguez.zenmap.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by fernando on 30/12/15.
 */
@Table(name = "ScanResults")
public abstract class ScanResult extends Model implements IScanResult, Serializable {

    @Column(name = "Target")
    protected String target;

    @Column(name = "StartTime")
    protected long startTime;

    @Column(name = "EndTime")
    protected long endTime;

    @Column(name = "ScanStatus")
    protected String scanStatus;

    @Column(name = "Output")
    protected String output;

    public ScanResult() {
        super();
    }

    /* Getters & setters */

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getScanStatus() {
        return scanStatus;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
