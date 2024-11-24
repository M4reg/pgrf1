package rasterizer;

import model.CuttingPolygon;
import model.Line;
import model.Point;
import model.Polygon;

import java.awt.*;

public class PolygonRasterizer {

    private LineRasterizer lineRasterizer;
    private int thickness;

    public PolygonRasterizer(LineRasterizer lineRasterizer, int thickness) {
        this.lineRasterizer = lineRasterizer;
        this.thickness = thickness;
    }


    //Vykresli polygon pokud ma vic jak 3 vrcholy
    public void rasterize(Polygon polygon) {
        if (polygon.getSize() < 3) {
            return;
        } else {

            Color color = Color.RED;
            if (polygon instanceof CuttingPolygon){
                color = ((CuttingPolygon) polygon).getColor();
            }

            lineRasterizer.setColor(color.getRGB());
            //cyklus pro vykreslení všechy stran polygonu
            for (int i = 0; i < polygon.getSize(); i++) {
                int indexA = i; // aktuální bod
                int indexB = i + 1; // následující bod


                if (indexB == polygon.getSize()) {
                    indexB = 0; // uzavření polygonu
                }
                Point A = polygon.getPoint(indexA);
                Point B = polygon.getPoint(indexB);
                lineRasterizer.rasterize(new Line(A, B, thickness));
            }

        }

    }
    public void setColor(int color) {
        lineRasterizer.setColor(color); // Nastaví barvu na lineRasterizer
    }
}
