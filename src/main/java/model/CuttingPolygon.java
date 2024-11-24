package model;

import java.awt.*;

public class CuttingPolygon extends Polygon{
    private Color color;
    public CuttingPolygon() {
        super();
        this.color = Color.YELLOW;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
