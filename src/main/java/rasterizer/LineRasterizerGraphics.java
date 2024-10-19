package rasterizer;

import java.awt.*;

public class LineRasterizerGraphics extends LineRasterizer{
    public LineRasterizerGraphics(Raster raster) {
        super(raster);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2, int thickness) {
        Graphics2D g2d = (Graphics2D) ((RasterBufferedImage) raster).getImage().getGraphics();
        g2d.setColor(this.color);

        // Nastavení tloušťky čáry
        g2d.setStroke(new BasicStroke(thickness));

        // Vykreslení čáry
        g2d.drawLine(x1, y1, x2, y2);
    }

}
