package model.filler;

import model.Point;
import rasterizer.Raster;
import view.Panel;

import java.util.Stack;

public class SeedFill implements Filler{

    private Raster raster;
    private int x,y;
    private int backgroundColor, fillColor;

    public SeedFill(Raster raster, int x, int y, int fillColor) {
        this.raster = raster;
        this.x = x;
        this.y = y;
        this.backgroundColor = raster.getPixel(x,y);
        this.fillColor = fillColor;
    }

    @Override
    public void fill() {
        seedFill(x,y);
    }

    private void seedFill(int x, int y){

        //TO DO: doplnit podminku o hranice obrazovky
        if ((x <= 0) || (y <= 0) || (x >= raster.getWidth()) || (y >= raster.getHeight())) {
            return;
        }
        //x,y sourazdnice pixelu kam uzivatel kliknul
        int pixelColor = raster.getPixel(x, y);

            //podminka pro ukonceni rekurze
            if(pixelColor != backgroundColor) {
                return;
            }

            raster.setPixel(x, y, fillColor);

            seedFill(x+1,y);
            seedFill(x-1,y);
            seedFill(x,y+1);
            seedFill(x,y-1);
    }
}
