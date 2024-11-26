package rasterizer;

import java.awt.*;

public class LineRasterizerGraphics extends LineRasterizer {
    public LineRasterizerGraphics(Raster raster) {
        super(raster);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) ((RasterBufferedImage) raster).getImage().getGraphics();
        g.setColor(this.color);
        g.drawLine(x1, y1, x2, y2);
    }

}
