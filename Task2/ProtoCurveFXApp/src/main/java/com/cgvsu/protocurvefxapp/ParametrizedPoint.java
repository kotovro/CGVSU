package com.cgvsu.protocurvefxapp;

public class ParametrizedPoint {
    double key;
    MutablePoint2D value;

    public ParametrizedPoint(double key, MutablePoint2D value) {
        this.key = key;
        this.value = value;
    }

    public double getKey() {
        return key;
    }

    public void setKey(double key) {
        this.key = key;
    }

    public MutablePoint2D getValue() {
        return value;
    }

}
