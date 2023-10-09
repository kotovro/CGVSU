package com.cgvsu.protocurvefxapp;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.util.ArrayList;

public class LagrangePolynomialController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    ArrayList<ParametrizedPoint> points = new ArrayList<ParametrizedPoint>();

    private final int POINT_RADIUS = 3;
    private ParametrizedPoint selectedPair;
    private boolean isDragged = false;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        canvas.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case MIDDLE -> handleMiddleClick(canvas.getGraphicsContext2D(), event);
                case SECONDARY ->  handleSecondaryClick(canvas.getGraphicsContext2D(), event);
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

    private void handleSecondaryClick(GraphicsContext graphicsContext, MouseEvent event) {
        final MutablePoint2D clickPoint = new MutablePoint2D(event.getX(), event.getY());
        ParametrizedPoint selectedPair = selectedPair(clickPoint);
        points.remove(selectedPair);
        recalcDistances();
        drawPolynomialCurve(graphicsContext);
    }

    private void handleMiddleClick(GraphicsContext graphicsContext, MouseEvent event) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        points = new ArrayList<ParametrizedPoint>();
    }

    private void handlePrimaryClick(GraphicsContext graphicsContext, MouseEvent event) {
        final MutablePoint2D clickPoint = new MutablePoint2D(event.getX(), event.getY());
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            selectedPair = selectedPair(clickPoint);
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (selectedPair == null && !isDragged) {
                points.add(getPointPair(clickPoint));
            } else {
                selectedPair = null;
            }
            isDragged = false;
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED){
            isDragged = true;
            if (selectedPair != null) {
                selectedPair.getValue().setX(event.getX());
                selectedPair.getValue().setY(event.getY());
                recalcDistances();
            }
        }
        drawPolynomialCurve(graphicsContext);
    }

    private void drawPolynomialCurve(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (ParametrizedPoint pair : points) {
            MutablePoint2D point = pair.getValue();
            graphicsContext.fillOval(
                    point.getX() - POINT_RADIUS, point.getY() - POINT_RADIUS,
                    2 * POINT_RADIUS, 2 * POINT_RADIUS);
        }
        if (points.size() > 1) {
            drawLagrange(graphicsContext);
        }
    }

    private ParametrizedPoint getPointPair(MutablePoint2D clickPoint) {
        if (points.size() == 0) {
            return new ParametrizedPoint(0.0, clickPoint);
        } else {
            ParametrizedPoint lastPair = points.get(points.size() - 1);
            double lastT = lastPair.getKey();
            double newT = lastT + clickPoint.distance(lastPair.getValue());
            return new ParametrizedPoint(newT, clickPoint);
        }
    }

    private void drawLagrange(GraphicsContext graphicsContext) {
        for (int i = 0; i < points.size() - 1; i++) {
            drawCurve(points.get(i), points.get(i + 1), graphicsContext);
        }
    }

    private void drawCurve(ParametrizedPoint p1, ParametrizedPoint p2, GraphicsContext graphicsContext) {
        double endT = p2.getKey();
        double curT = p1.getKey();
        MutablePoint2D curPoint = p1.getValue();
        while (curT < endT) {
            curT += 1;
            if (curT > endT) {
                curT = endT;
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
    private ParametrizedPoint selectedPair(MutablePoint2D clickPoint) {
        for (ParametrizedPoint pair: points) {
            if (clickPoint.distance(pair.getValue()) <= POINT_RADIUS) {
                return pair;
            }
        }
        return null;
    }
    private void recalcDistances() {
        if (points.size() > 0) {
            double curDistance = 0;
            points.get(0).setKey(0);
            for (int i = 1; i < points.size(); i++) {
                curDistance += points.get(i - 1).getValue().distance(points.get(i).getValue());
                points.get(i).setKey(curDistance);
            }
        }
    }
}