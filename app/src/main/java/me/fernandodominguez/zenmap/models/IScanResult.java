package me.fernandodominguez.zenmap.models;

/**
 * Created by fernando on 30/12/15.
 */
public interface IScanResult {
    String getTitle();
    String getResult();
    void saveWithChildren();
}
