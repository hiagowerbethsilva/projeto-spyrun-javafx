package br.com.spyrun;

import javafx.scene.shape.Rectangle;

public interface Movable {
    double getX();
    double getY();
    void setX(double x);
    void setY(double y);
    Rectangle getBounds();
}
