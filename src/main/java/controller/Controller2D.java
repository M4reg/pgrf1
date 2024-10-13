package controller;

import model.Line;
import rasterizer.LineRasterizer;
import rasterizer.LineRasterizerGraphics;
import rasterizer.LineRasterizerTrivial;
import rasterizer.Raster;
import view.Panel;
import java.awt.*;


public class Controller2D {
    private final Panel panel;
    private LineRasterizer lineRasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;

        initObjects(panel.getRasterBufferedImage());
        // init Listener
    }

    public void initObjects(Raster raster) {
        lineRasterizer = new LineRasterizerGraphics(raster);
        // lineRasterizer = new LineRasterizerTrivial(raster);
        lineRasterizer.setColor(0x31E628);
        lineRasterizer.rasterize(new Line(20, 200, 500, 500));

    }
}

