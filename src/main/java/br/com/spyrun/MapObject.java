package br.com.spyrun;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapObject {
    public double x, y, width, height;
    public Color color;
    private Rectangle bounds;

    public MapObject(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public void draw(GraphicsContext g, double cameraX, double cameraY) {
        g.setFill(color);
        g.fillRect(x - cameraX, y - cameraY, width, height);
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
}
