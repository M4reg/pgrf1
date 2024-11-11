package model.filler;

import model.Point;
import rasterizer.Raster;

import java.util.Stack;

public class SeedFill implements Filler {

    private Raster raster;
    private int x, y;
    private int backgroundColor, fillColor;

    public SeedFill(Raster raster, int x, int y, int fillColor) {
        this.raster = raster;
        this.x = x;
        this.y = y;
        this.backgroundColor = raster.getPixel(x, y);
        this.fillColor = fillColor;
    }

    @Override
    public void fill() {
        seedFill(x, y);
    }

    private void seedFill(int x1, int y1) {

        Stack<Point> stack = new Stack<>();
        stack.push(new Point(x1, y1)); //Do zásobníku vložíme bod kam uživatel kliknul

        while (!stack.isEmpty()) {
            Point point = stack.pop();
            int x = point.getX();
            int y = point.getY();

            //podminku o hranice obrazovky
            if ((x <= 0) || (y <= 0) || (x >= raster.getWidth()-1) || (y >= raster.getHeight()-1)) {
                continue;
            }

            //x,y sourazdnice pixelu kam uzivatel kliknul
            int pixelColor = raster.getPixel(x, y);

            //podminka pro ukonceni
            if (pixelColor == fillColor || pixelColor != backgroundColor) {
                continue;
            }

            raster.setPixel(x, y, fillColor);

            stack.push(new Point(x + 1, y));
            stack.push(new Point(x - 1, y));
            stack.push(new Point(x, y + 1));
            stack.push(new Point(x, y - 1));
        }
    }
}
