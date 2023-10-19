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
            lineBresenham(graphicsContext, (int) curPoint.getX(), (int) curPoint.getY(), (int) newPoint.getX(), (int) newPoint.getY());
            //graphicsContext.strokeLine(curPoint.getX(), curPoint.getY(), newPoint.getX(), newPoint.getY());
            //lineWu(graphicsContext, curPoint.getX(), curPoint.getY(), newPoint.getX(), newPoint.getY());
            curPoint = newPoint;
        }
    }
    private static void lineBresenham(GraphicsContext graphicsContext, int x0, int y0, int x1, int y1) {
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        int deltaX = x1 - x0;
        int deltaY = y1 - y0;

        //определяем напрвление движения по оси
        //если delta положительна, то двигаемся вправо,
        //если отрицательная - влево
        //если 0 - стоим на месте
        int directionX = Integer.compare(deltaX, 0);
        int directionY = Integer.compare(deltaY, 0);

        deltaX = Math.abs(deltaX);
        deltaY = Math.abs(deltaY);

        int shiftX = directionX;	int shiftY = 0;
        int shortShiftCount = deltaY;	int longShiftCount = deltaX;

        if (deltaX < deltaY)
        //случай, когда прямая скорее "высокая", чем длинная, т.е. вытянута по оси y
        {
            shiftX = 0;	shiftY = directionY;
            shortShiftCount = deltaX;	longShiftCount = deltaY;//тогда в цикле будем двигаться по y
        }

        int x = x0;
        int y = y0;
        int err = longShiftCount/2;
        pixelWriter.setColor(x, y, Color.BLACK);//ставим первую точку
        //все последующие точки возможно надо сдвигать, поэтому первую ставим вне цикла

        for (int t = 0; t < longShiftCount; t++)//идём по всем точкам, начиная со второй и до последней
        {
            err -= shortShiftCount;
            if (err < 0)
            {
                err += longShiftCount;
                x += directionX;//сдвинуть прямую (сместить вверх или вниз
                y += directionY;//и сместить влево-вправо)
            }
            else
            {
                x += shiftX;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y += shiftY;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }

            pixelWriter.setColor(x, y, Color.BLACK);
        }
    }
    private static void plot(GraphicsContext graphicsContext, double x, double y, double c) {
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        pixelWriter.setColor((int) x, (int) y, new Color(0, 0, 0, c));
    }

    private static int ipart(double x) {
        return (int) x;
    }

    private static double fpart(double x) {
        return x % 1;
    }

    private static double rfpart(double x) {
        return 1.0 - fpart(x);
    }
    private static void lineWu(GraphicsContext graphicsContext, double x0, double y0, double x1, double y1) {
        boolean steep = abs(y1 - y0) > abs(x1 - x0);
        if (steep)
            lineWu(graphicsContext, y0, x0, y1, x1);

        if (x0 > x1)
            lineWu(graphicsContext, x1, y1, x0, y0);

        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = dy / dx;

        // handle first endpoint
        double xend = Math.round(x0);
        double yend = y0 + gradient * (xend - x0);
        double xgap = rfpart(x0 + 0.5);
        double xpxl1 = xend; // this will be used in the main loop
        double ypxl1 = ipart(yend);

        if (steep) {
            plot(graphicsContext, ypxl1, xpxl1, rfpart(yend) * xgap);
            plot(graphicsContext, ypxl1 + 1, xpxl1, fpart(yend) * xgap);
        } else {
            plot(graphicsContext, xpxl1, ypxl1, rfpart(yend) * xgap);
            plot(graphicsContext, xpxl1, ypxl1 + 1, fpart(yend) * xgap);
        }

        // first y-intersection for the main loop
        double intery = yend + gradient;

        // handle second endpoint
        xend = Math.round(x1);
        yend = y1 + gradient * (xend - x1);
        xgap = fpart(x1 + 0.5);
        double xpxl2 = xend; // this will be used in the main loop
        double ypxl2 = ipart(yend);

        if (steep) {
            plot(graphicsContext, ypxl2, xpxl2, rfpart(yend) * xgap);
            plot(graphicsContext, ypxl2 + 1, xpxl2, fpart(yend) * xgap);
        } else {
            plot(graphicsContext, xpxl2, ypxl2, rfpart(yend) * xgap);
            plot(graphicsContext, xpxl2, ypxl2 + 1, fpart(yend) * xgap);
        }

        // main loop
        for (double x = xpxl1 + 1; x <= xpxl2 - 1; x++) {
            if (steep) {
                plot(graphicsContext, ipart(intery), x, rfpart(intery));
                plot(graphicsContext, ipart(intery) + 1, x, fpart(intery));
            } else {
                plot(graphicsContext, x, ipart(intery), rfpart(intery));
                plot(graphicsContext, x, ipart(intery) + 1, fpart(intery));
            }
            intery = intery + gradient;
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
