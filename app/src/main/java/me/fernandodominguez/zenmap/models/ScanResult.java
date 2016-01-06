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

    @Column(name = "Name")
    protected String name;

    @Column(name = "Target")
    protected String target;

    @Column(name = "StartTime")
    protected long startTime;

    @Column(name = "EndTime")
    protected long endTime;

    @Column(name = "Elapsed")
    protected float elapsed;

    @Column(name = "ScanStatus")
    protected String scanStatus;

    @Column(name = "Output")
    protected String output;

    /* Constructors */

    public ScanResult() {
        super();
    }

    /* Public methods */

    public String getTitle(){
        return getName();
    }

    /* Getters & setters */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public float getElapsed() {
        return elapsed;
    }

    public void setElapsed(float elapsed) {
        this.elapsed = elapsed;
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
