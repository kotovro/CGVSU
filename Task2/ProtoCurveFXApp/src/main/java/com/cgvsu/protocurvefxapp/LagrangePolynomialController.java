package com.cgvsu.protocurvefxapp;

import javafx.fxml.FXML;
//import javafx.geometry.MutablePoint2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;

public class LagrangePolynomialController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    ArrayList<Pair<MutablePoint2D, MutablePoint2D>> points = new ArrayList<Pair<MutablePoint2D, MutablePoint2D>>();

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
        points = new ArrayList<Pair<MutablePoint2D, MutablePoint2D>>();
    }

    private void handlePrimaryClick(GraphicsContext graphicsContext, MouseEvent event) {
        final MutablePoint2D clickPoint = new MutablePoint2D(event.getX(), event.getY());
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            selectedPoint = selectedPoint(clickPoint);
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (selectedPoint == null && !isDragged) {
                points.add(getPointPair(clickPoint));
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
        for (Pair<MutablePoint2D, MutablePoint2D> pair : points) {
            MutablePoint2D point = getPointFromPair(pair);
            graphicsContext.fillOval(
                    point.getX() - POINT_RADIUS, point.getY() - POINT_RADIUS,
                    2 * POINT_RADIUS, 2 * POINT_RADIUS);
        }
        if (points.size() > 1) {
            drawLagrange(graphicsContext);
        }
    }

    private Pair<MutablePoint2D, MutablePoint2D> getPointPair(MutablePoint2D clickPoint) {
        if (points.size() == 0) {
            return new Pair<>(new MutablePoint2D(0, clickPoint.getX()), new MutablePoint2D(0, clickPoint.getY()));
        } else {
            Pair<MutablePoint2D, MutablePoint2D> lastPair = points.get(points.size() - 1);
            double lastT = lastPair.getKey().getX();
            double newT = lastT + clickPoint.distance(getPointFromPair(lastPair));
            return new Pair<>(new MutablePoint2D(newT, clickPoint.getX()), new MutablePoint2D(newT, clickPoint.getY()));
        }
    }

    private void drawLagrange(GraphicsContext graphicsContext) {
        for (int i = 0; i < points.size() - 1; i++) {
            drawCurve(points.get(i), points.get(i + 1), graphicsContext);
        }
    }

    private void drawCurve(Pair<MutablePoint2D, MutablePoint2D> p1, Pair<MutablePoint2D, MutablePoint2D> p2, GraphicsContext graphicsContext) {
        double endX = p2.getKey().getX();
        double curT = p1.getKey().getX();
        MutablePoint2D curPoint = getPointFromPair(p1);
        while (curT < endX) {
            curT += 1;
            if (curT > endX) {
                curT = endX;
            }
            MutablePoint2D newPoint = solvePolynomial(curT);
            graphicsContext.strokeLine(curPoint.getX(), curPoint.getY(), newPoint.getX(), newPoint.getY());
            curPoint = newPoint;
        }
    }

    private MutablePoint2D solvePolynomial(double t) {
        int i = 0;
        double resultX = 0;
        double resultY = 0;
        for (Pair<MutablePoint2D, MutablePoint2D> pairI: points) {
            double productX = 1;
            double productY = 1;
            int j = 0;
            for (Pair<MutablePoint2D, MutablePoint2D> pairJ: points) {
                if (i != j) {
                    productX *= (t - pairJ.getKey().getX()) / (pairI.getKey().getX() - pairJ.getKey().getX());
                    productY *= (t - pairJ.getValue().getX()) / (pairI.getValue().getX() - pairJ.getValue().getX());
                }
                j++;
            }
            resultX += productX * pairI.getKey().getY();
            resultY += productY * pairI.getValue().getY();
            i++;
        }
        return new MutablePoint2D(resultX, resultY);
    }
    private MutablePoint2D selectedPoint(MutablePoint2D clickPoint) {
//        for (MutablePoint2D point: points) {
//            if (clickPoint.distance(point) <= POINT_RADIUS) {
//                return point;
//            }
//        }
        return null;
    }
    private MutablePoint2D getPointFromPair(Pair<MutablePoint2D, MutablePoint2D> pair) {
        double x = pair.getKey().getY();
        double y = pair.getValue().getY();
        return new MutablePoint2D(x, y);
    }
}