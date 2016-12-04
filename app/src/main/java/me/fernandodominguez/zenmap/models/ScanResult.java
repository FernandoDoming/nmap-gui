package me.fernandodominguez.zenmap.models;

import java.io.Serializable;
import java.util.List;

import me.fernandodominguez.zenmap.models.network.Host;

/**
 * Created by fernando on 30/12/15.
 */
public abstract class ScanResult implements IScanResult, Serializable {

    protected String name;
    protected String target;
    protected long startTime;
    protected long endTime;
    protected float elapsed;
    protected String scanStatus;
    protected String output;
    protected String summary;

    /* Public methods */

    public String getTitle(){
        if (getName() == null) {
            return getTarget();
        } else {
            return getName();
        }
    }

    public abstract List<Host> getHosts();

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

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }
}
