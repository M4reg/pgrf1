package controller;

import rasterizer.LineRasterizer;
import rasterizer.Raster;
import view.Panel;

public class Controller2D {
    private final Panel panel;

    public Controller2D(Panel panel) {
        this.panel = panel;

        //inicial objektu
        //inicial Listener
    }

    public void initObjects(Raster raster) {
        //lineRasterizer = new LineRasterizer(raster);
        lineRasterizer = new LineRasterizer(raster);
    }
}
