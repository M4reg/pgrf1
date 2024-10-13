package rasterizer;

public class LineRasterizerTrivial extends LineRasterizer {
    public LineRasterizerTrivial(Raster raster) {
        super(raster);
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {


        // Pokud jsou x1 a x2 stejné, jedná se o vertikální přímku
        if (x1 == x2) {

            // Zajistíme, že kreslíme od menšího y k většímu
            if (y1 > y2) {
                int tmp = y1;
                y1 = y2;
                y2 = tmp;
            }
            // Kreslíme vertikální přímku
            for (int y = y1; y <= y2; y++) {
                raster.setPixel(x1, y, color.getRGB());
            }

        } else
        if (x1 > x2) {
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }

        float k = (float) (y2 - y1) / (float) (x2 - x1);
        float q = (float) y1 - k * x1;

        for (int x = x1; x <= x2; x++) {
            int y = Math.round(k * x + q);
            raster.setPixel(x, y, color.getRGB());
        }
    }
}
