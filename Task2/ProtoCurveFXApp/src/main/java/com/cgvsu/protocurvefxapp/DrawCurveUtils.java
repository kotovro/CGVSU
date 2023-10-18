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
                x += directionX;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
                y += directionY;//или сместить влево-вправо, если цикл проходит по y
            }
            else
            {
                x += shiftX;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
                y += shiftY;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
            }

            pixelWriter.setColor(x, y, Color.BLACK);
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
