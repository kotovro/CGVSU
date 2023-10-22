package com.cgvsu.protocurvefxapp;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.tan;

public class DrawCurveUtils {

    public static void drawPolynomialCurve(GraphicsContext graphicsContext, ArrayList<ParametrizedPoint> points, Canvas canvas, int pointRadius) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (points.size() > 1) {
            drawLagrange(graphicsContext, points, true);
        }
        for (ParametrizedPoint point : points) {
            MutablePoint2D point2D = point.getValue();
            graphicsContext.fillOval(
                    point2D.getX() - pointRadius, point2D.getY() - pointRadius,
                    2 * pointRadius, 2 * pointRadius);
        }
    }
    private static void drawLagrange(GraphicsContext graphicsContext, ArrayList<ParametrizedPoint> points, boolean isGradient) {
        MutablePoint2D curPoint = points.get(0).getValue();
        double endT = points.get(points.size() -1).getKey();
        double curT = points.get(0).getKey();

        Color curColor = getColor(graphicsContext, Color.BLACK, curT, endT, new MutablePoint2D(0, 0), curPoint, isGradient);

        while (curT < endT) {
            curT += 1;
            if (curT > endT) {
                curT = endT;
            }
            MutablePoint2D newPoint = solvePolynomial(curT, points);
            curColor = DrawCurveUtils.getColor(graphicsContext, curColor, curT, endT, curPoint, newPoint, isGradient);
            RasterizationUtils.rasterizeLine(graphicsContext, curPoint.toPoint2D(), newPoint.toPoint2D(), curColor, RasterizationUtils.WU);
            curPoint = newPoint;
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

    private static Color getColor(GraphicsContext graphicsContext,
                                  Color curColor,
                                  double curT,
                                  double endT,
                                  MutablePoint2D startPoint,
                                  MutablePoint2D endPoint,
                                  boolean isGradient) {
        if (!isGradient) {
            return curColor;
        }

        double deltaRed = 200 / endT;
        double deltaGreen = 255 / graphicsContext.getCanvas().getWidth();
        double deltaBlue = 255 / graphicsContext.getCanvas().getHeight();

        int curRed = (int) (Math.round(curT * deltaRed));
        int curGreen = getGradientColor(endPoint.getX(), startPoint.getX(), curColor.getGreen(), deltaGreen);
        int curBlue = getGradientColor(endPoint.getY(),  startPoint.getY(), curColor.getBlue(),  deltaBlue);

        return Color.rgb(curRed, curGreen, curBlue);
    }
    private static int getGradientColor(double p1, double p2, double curVal, double shift) {
        int res = (int) (Math.round((p1 - p2) * shift) + curVal * 255);
        if (res < 0) {
            res = 0;
        } else if (res > 255) {
            res = 255;
        }
        return res;
    }
}
