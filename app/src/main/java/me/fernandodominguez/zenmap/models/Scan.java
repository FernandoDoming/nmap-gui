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

    public void run(Context context) {
        new NmapExecutor(context).execute(this);
    }
}
