package com.cgvsu.protocurvefxapp;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class RasterizationUtils {

    public static final int STANDART = 0;
    public static final int BRESENHAM = 1;
    public static final int WU = 2;

    public static void rasterizeLine(GraphicsContext graphicsContext, Point2D start, Point2D finish, Color color, int rasterizationType) {
        if (rasterizationType == STANDART) {
            graphicsContext.setStroke(color);
            graphicsContext.strokeLine(start.getX(), start.getY(), finish.getX(), finish.getY());
        }
        else if (rasterizationType == BRESENHAM) {
            lineBresenham(graphicsContext, (int) start.getX(), (int) start.getY(), (int) finish.getX(), (int) finish.getY(), color);
        }
        else if (rasterizationType == WU) {
            lineWu(graphicsContext, start.getX(), start.getY(), finish.getX(), finish.getY(), color);
        }
    }
    private static void plot(GraphicsContext graphicsContext, double x, double y, Color c) {
        PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        pixelWriter.setColor((int) x, (int) y, c);
    }
    private static void lineBresenham(GraphicsContext graphicsContext, int x0, int y0, int x1, int y1, Color color) {


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

        plot(graphicsContext, x, y, color);//ставим первую точку
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

            plot(graphicsContext, x, y, color);
        }
    }

    private static void lineWu(GraphicsContext graphicsContext, double x0, double y0, double x1, double y1, Color color) {
        double lenX = Math.abs(x1 - x0);
        double lenY = Math.abs(y1 - y0);
        double epsilon = 0.00003;
        if (lenX > lenY) { // линия скорее "горизонтальная"
            if (x1 < x0) {
                double temp = x1;
                x1 = x0;
                x0 = temp;
                temp = y1;
                y1 = y0;
                y0 = temp;
            }
            if (lenY < epsilon) { //линия точно горизонтальная
                for (double x = x0; x < x1 + 1; x+=1) {
                    plot(graphicsContext, x, y0, color);
                }
            } else {
                double deltaY = lenY / lenX;
                if (y1 < y0) {
                    deltaY *= -1;
                }
                double curY = y0;
                for (double x = x0; x < x1 + 1; x+=1) {
                    double yHigh = Math.ceil(curY);
                    double yLow = Math.floor(curY);
                    plot(graphicsContext, x, yLow, adjustColor(color, Math.abs(curY - yHigh)));
                    plot(graphicsContext, x, yHigh, adjustColor(color, Math.abs(curY - yLow)));
                    curY += deltaY;
                }
            }
        } else { // линия скорее "вертикальная"
            if (y1 < y0) {
                double temp = x1;
                x1 = x0;
                x0 = temp;
                temp = y1;
                y1 = y0;
                y0 = temp;
            }
            if (lenX < epsilon) { //линия точно вертикальная
                for (double y = y0; y < y1 + 1; y+=1) {
                    plot(graphicsContext, x0, y, color);
                }
            } else {
                double deltaX = lenX / lenY;
                if (x1 < x0) {
                    deltaX *= -1;
                }
                double curX = x0;
                for (double y = y0; y < y1 + 1; y+=1) {
                    double xRight = Math.ceil(curX);
                    double xLeft = Math.floor(curX);
                    plot(graphicsContext, xLeft, y, adjustColor(color, Math.abs(curX - xRight)));
                    plot(graphicsContext, xRight, y, adjustColor(color, Math.abs(curX - xLeft)));
                    curX += deltaX;
                }
            }
        }
    }
    private static Color adjustColor(Color color, double alpha) {
        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();
        return new Color(r, g, b, alpha);
    }
}
