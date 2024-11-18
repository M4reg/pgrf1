package model;

import rasterizer.PolygonRasterizer;

import java.awt.*;

public class Pentagon extends Polygon{
    private int centerX, centerY, radius;
    private double rotationAngle = 0;

    public Pentagon(int centerX, int centerY, int radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        calculatePoints();
    }

    @Override
    public void clearPoints() {
        super.clearPoints();
    }

    public void setRadius(int radius) {
        this.radius = radius;
        calculatePoints();
    }

    public void setRotationAngle(double angle) {
        this.rotationAngle = angle;
        calculatePoints();
    }

    private void calculatePoints(){
        clearPoints();

        double angleStep = Math.toRadians(72); // Úhel mezi vrcholy (360° / 5)
        for (int i = 0; i < 5; i++) {
            double angle = rotationAngle+ i  * angleStep;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            // Přidání bodu do seznamu bodů polygonu (děděno z třídy Polygon)
            addPoint(new Point(x, y));
        }
    }
    // Metoda pro vykreslení pentagonu pomocí PolygonRasterizeru
    public void draw(PolygonRasterizer rasterizer) {
        rasterizer.setColor(Color.RED.getRGB());
        rasterizer.rasterize(this);
    }
}
