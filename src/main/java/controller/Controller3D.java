package controller;

import rasterizer.LineRasterizer;
import rasterizer.LineRasterizerGraphics;
import rasterizer.Raster;
import renderer.WiredRanderer;
import solids.Axes;
import solids.Cube;
import solids.Solid;
import view.Panel;

public class Controller3D implements Controller{
    private final Panel panel;
    private Raster raster;

    private LineRasterizer lineRasterizer;
    private WiredRanderer wiredRanderer;

    private Solid cube;
    private Solid axes;

    public Controller3D(Panel panel) {
        this.panel = panel;
        this.raster = panel.getRasterBufferedImage();

        lineRasterizer = new LineRasterizerGraphics(raster);

        wiredRanderer = new WiredRanderer(
                lineRasterizer,
                panel.getHeight(),
                panel.getWidth()
        );

    }

    @Override
    public void InitObjects() {
        cube = new Cube();
        axes = new Axes();
    }

    @Override
    public void InitListeners() {

    }

    public void RanderScene(){
        panel.clear(0xFFFFFF);
        wiredRanderer.renderSolid(cube);
        panel.repaint();
    }


}

