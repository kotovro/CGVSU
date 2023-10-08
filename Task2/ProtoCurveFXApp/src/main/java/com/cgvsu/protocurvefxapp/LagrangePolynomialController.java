package com.cgvsu.protocurvefxapp;

import javafx.fxml.FXML;
//import javafx.geometry.MutablePoint2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class LagrangePolynomialController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    ArrayList<MutablePoint2D> points = new ArrayList<MutablePoint2D>();
    private final int POINT_RADIUS = 3;
    private MutablePoint2D selectedPoint;
    private boolean isDragged = false;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        canvas.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case MIDDLE -> handleMiddleClick(canvas.getGraphicsContext2D(), event);
            }
        });
        canvas.setOnMousePressed(event -> {
            switch (event.getButton()) {
                case PRIMARY -> handlePrimaryClick(canvas.getGraphicsContext2D(), event);
            }
        });
        canvas.setOnMouseReleased(event -> {
            switch (event.getButton()) {
                case PRIMARY -> handlePrimaryClick(canvas.getGraphicsContext2D(), event);
            }
        });
        canvas.setOnMouseDragged(event -> {
            switch (event.getButton()) {
                case PRIMARY -> handlePrimaryClick(canvas.getGraphicsContext2D(), event);
            }
        });
    }

    private void handleMiddleClick(GraphicsContext graphicsContext, MouseEvent event) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        points = new ArrayList<MutablePoint2D>();
    }

    private void handlePrimaryClick(GraphicsContext graphicsContext, MouseEvent event) {
        final MutablePoint2D clickPoint = new MutablePoint2D(event.getX(), event.getY());
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            selectedPoint = selectedPoint(clickPoint);
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (selectedPoint == null && !isDragged) {
                points.add(clickPoint);
            } else {
                selectedPoint = null;
            }
            isDragged = false;
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED){
            isDragged = true;
            if (selectedPoint != null) {
                selectedPoint.setX(event.getX());
                selectedPoint.setY(event.getY());
            }
        }

//        } else {
//            selectedPoint
//        }
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (MutablePoint2D point : points) {
            graphicsContext.fillOval(
                    point.getX() - POINT_RADIUS, point.getY() - POINT_RADIUS,
                    2 * POINT_RADIUS, 2 * POINT_RADIUS);
        }
        if (points.size() > 0) {
            drawLagrange(graphicsContext);
            //graphicsContext.strokeLine(lastPoint.getX(), lastPoint.getY(), clickPoint.getX(), clickPoint.getY());
        }
    }

    private void drawLagrange(GraphicsContext graphicsContext) {
        for (int i = 0; i < points.size() - 1; i++) {
            drawCurve(points.get(i), points.get(i + 1), graphicsContext);
        }
    }

    private void drawCurve(MutablePoint2D p1, MutablePoint2D p2, GraphicsContext graphicsContext) {
        double startX = Math.min(p1.getX(), p2.getX());
        double endX = Math.max(p1.getX(), p2.getX());
        double curX = startX;
        MutablePoint2D curPoint = (startX == p1.getX()) ? p1 : p2;
        while (curX < endX) {
            curX += 1;
            if (curX > endX) {
                curX = endX;
            }
            MutablePoint2D newPoint = solvePolynomial(curX);
            graphicsContext.strokeLine(curPoint.getX(), curPoint.getY(), newPoint.getX(), newPoint.getY());
            curPoint = newPoint;
        }
    }

    private MutablePoint2D solvePolynomial(double x) {
        int i = 0;
        double result = 0;
        for (MutablePoint2D pointI: points) {
            double product = 1;
            int j = 0;
            for (MutablePoint2D pointJ: points) {
                if (i != j) {
                    product *= (x - pointJ.getX()) / (pointI.getX() - pointJ.getX());
                }
                j++;
            }
            result += product * pointI.getY();
            i++;
        }
        return new MutablePoint2D(x, result);
    }
    private MutablePoint2D selectedPoint(MutablePoint2D clickPoint) {
        for (MutablePoint2D point: points) {
            if (clickPoint.distance(point) <= POINT_RADIUS) {
                return point;
            }
        }
        return null;
    }
}