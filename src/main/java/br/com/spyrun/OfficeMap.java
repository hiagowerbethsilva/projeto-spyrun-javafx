package br.com.spyrun;

import javafx.scene.canvas.GraphicsContext;
import java.util.List;

public class OfficeMap {
    private List<MapObject> objects;

    public OfficeMap(List<MapObject> objects) {
        this.objects = objects;
    }

    public void draw(GraphicsContext g, double cameraX, double cameraY) {
        for (MapObject obj : objects) {
            obj.draw(g, cameraX, cameraY);
        }
    }
}
