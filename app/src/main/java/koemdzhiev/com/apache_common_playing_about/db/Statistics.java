package koemdzhiev.com.apache_common_playing_about.db;

import io.realm.RealmObject;

/**
 * Created by Georgi on 12/11/2016.
 */

public class Statistics extends RealmObject {
    private double meanX;
    private double meanY;
    private double meanZ;
    private double sdX;
    private double sdY;
    private double sdZ;

    public double getSdX() {
        return sdX;
    }

    public void setSdX(double sdX) {
        this.sdX = sdX;
    }

    public double getSdY() {
        return sdY;
    }

    public void setSdY(double sdY) {
        this.sdY = sdY;
    }

    public double getSdZ() {
        return sdZ;
    }

    public void setSdZ(double sdZ) {
        this.sdZ = sdZ;
    }

    public double getMeanY() {
        return meanY;
    }

    public void setMeanY(double meanY) {
        this.meanY = meanY;
    }

    public double getMeanZ() {
        return meanZ;
    }

    public void setMeanZ(double meanZ) {
        this.meanZ = meanZ;
    }


    public double getMeanX() {
        return meanX;
    }

    public void setMeanX(double meanX) {
        this.meanX = meanX;
    }
}
