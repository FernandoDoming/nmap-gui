package me.fernandodominguez.zenmap.models;

import android.content.Context;

/**
 * Created by fernando on 28/12/15.
 */
public class Scan {

    private int type;
    private String target;
    private String intensity;


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
