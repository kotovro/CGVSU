package com.cgvsu.protocurvefxapp;

public class MutablePoint2D {
    private double x;
    private double y;

    public MutablePoint2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double distance(MutablePoint2D point) {
        return Math.sqrt(Math.pow(x - point.getX(), 2) + Math.pow(y - point.getY(), 2));
    }
    public boolean equals( Object obj ) {
        if (obj instanceof MutablePoint2D) {
            MutablePoint2D point = (MutablePoint2D) obj;
            return point.getX() == x && point.getY() == y;
        } else {
            return false;
        }
    }

}
