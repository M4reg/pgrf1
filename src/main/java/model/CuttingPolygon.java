package model;

import rasterizer.PolygonRasterizer;

import java.awt.*;

public class CuttingPolygon extends Polygon{
    public CuttingPolygon() {
        super();
        initializeDefaultShape();
    }

    // Nastaví základní tvar a pozici ořezávacího polygonu
    private void initializeDefaultShape() {
        // Přidáme body ořezávacího polygonu tak, aby tvořily konvexní tvar
        this.addPoint(new Point(100, 400));
        this.addPoint(new Point(200, 100));
        this.addPoint(new Point(400, 500));

    }
    public void drawCuttingPolygon(PolygonRasterizer polygonRasterizer) {
        polygonRasterizer.getLineRasterizer().setColor(Color.YELLOW.getRGB());  // Nastavíme barvu hranice na modrou
        polygonRasterizer.rasterize(this);  // Vykreslí polygon
    }

}
