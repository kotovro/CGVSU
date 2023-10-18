package com.cgvsu.protocurvefxapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class DrawCurveUtils {
    
    private static void drawLagrange(GraphicsContext graphicsContext, ArrayList<ParametrizedPoint> points) {
        double endT = points.get(points.size() -1).getKey();
        double curT = points.get(0).getKey();
        MutablePoint2D curPoint = points.get(0).getValue();
        while (curT < endT) {
            curT += 1;
            if (curT > endT) {
                curT = endT;
            }
            MutablePoint2D newPoint = solvePolynomial(curT, points);
            lineBresenham(graphicsContext, curPoint.getX(), curPoint.getY(), newPoint.getX(), newPoint.getY());
            //graphicsContext.strokeLine(curPoint.getX(), curPoint.getY(), newPoint.getX(), newPoint.getY());
            curPoint = newPoint;
        }
    }
    private static void lineBresenham(GraphicsContext graphicsContext, double x0, double y0, double x1, double y1) {
        if (x0 < x1) {
            lineBresenhamBase(graphicsContext, x0, y0, x1, y1);
        } else {
            lineBresenhamBase(graphicsContext, x1, y0, x0, y1);
        }
    }
    private static void lineBresenhamBase(GraphicsContext graphicsContext, double x0, double y0, double x1, double y1) {
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        int deltax = (int) (x1 - x0);
        int deltay = (int) (y1 - y0);
        int error = 0;
        if (deltax > deltay) {
            int deltaerr = (deltay + 1);
            int y = (int) y0;
            int diry = y1 > y0 ? 1 : -1;
            for (int x = (int) x0; x < x1; x++) {
                pixelWriter.setColor(x, y, Color.BLACK);
                error = error + deltaerr;
                if (error >= (deltax + 1)) {
                    y = y + diry;
                    error = error - (deltax + 1);
                }
            }
        } else {
            int deltaerr = (deltax + 1);
            int x = (int) x0;
            int dirx = x1 > x0 ? 1 : -1;
            for (int y = (int) y0; y < y1; y++) {
                pixelWriter.setColor(x, y, Color.BLACK);
                error = error + deltaerr;
                if (error >= (deltay + 1)) {
                    y = y + dirx;
                    error = error - (deltay + 1);
                }
            }
        }
    }

    public static void drawPolynomialCurve(GraphicsContext graphicsContext, ArrayList<ParametrizedPoint> points, Canvas canvas, int pointRadius) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (ParametrizedPoint point : points) {
            MutablePoint2D point2D = point.getValue();
            graphicsContext.fillOval(
                    point2D.getX() - pointRadius, point2D.getY() - pointRadius,
                    2 * pointRadius, 2 * pointRadius);
        }
        if (points.size() > 1) {
            drawLagrange(graphicsContext, points);
        }
    }
    private static MutablePoint2D solvePolynomial(double t, ArrayList<ParametrizedPoint> points) {
        int i = 0;
        double resultX = 0;
        double resultY = 0;
        for (ParametrizedPoint pairI: points) {
            double product = 1;
            int j = 0;
            for (ParametrizedPoint pairJ: points) {
                if (i != j) {
                    product *= (t - pairJ.getKey()) / (pairI.getKey() - pairJ.getKey());
                }
                j++;
            }
            resultX += product * pairI.getValue().getX();
            resultY += product * pairI.getValue().getY();
            i++;
        }
        return new MutablePoint2D(resultX, resultY);
    }
}
