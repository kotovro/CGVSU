package com.cgvsu.protocurvefxapp;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;

public class LagrangePolynomialController {

    @FXML
    AnchorPane anchorPane;
    @FXML
    private Canvas canvas;

    ArrayList<ParametrizedPoint> points = new ArrayList<>();

    private final int POINT_RADIUS = 3;
    private ParametrizedPoint selectedPoint;
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
        DrawCurveUtils.drawPolynomialCurve(graphicsContext, points, canvas, POINT_RADIUS);
    }

    private void handleMiddleClick(GraphicsContext graphicsContext, MouseEvent event) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        points = new ArrayList<>();
    }

    private void handlePrimaryClick(GraphicsContext graphicsContext, MouseEvent event) {
        final MutablePoint2D clickPoint = new MutablePoint2D(event.getX(), event.getY());
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            selectedPoint = selectedPair(clickPoint);

        }  else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED){
            isDragged = true;
            if (selectedPoint != null) {
                selectedPoint.getValue().setX(event.getX());
                selectedPoint.getValue().setY(event.getY());
                recalcDistances();
            }
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            if (selectedPoint == null && !isDragged) {
                points.add(getPointPair(clickPoint));
            }

            selectedPoint = null;
            isDragged = false;
        }
        DrawCurveUtils.drawPolynomialCurve(graphicsContext, points, canvas, POINT_RADIUS);
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