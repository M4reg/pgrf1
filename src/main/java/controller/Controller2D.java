package controller;

import model.Point;
import model.Polygon;
import rasterizer.*;
import view.Panel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



public class Controller2D {
    private final Panel panel;
    private LineRasterizer lineRasterizer;

    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;

        initObjects(panel.getRasterBufferedImage());

        initListeners(panel);
    }

    public void initObjects(Raster raster) {
        lineRasterizer = new LineRasterizerGraphics(raster);
        // lineRasterizer = new LineRasterizerTrivial(raster);
        lineRasterizer.setColor(0x31E628);
        //lineRasterizer.rasterize(new Line(20, 200, 500, 500));

        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
    }

    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                panel.clear(Color.BLACK.getRGB());
                polygon.addPoint(new Point(e.getX(),e.getY()));
                polygonRasterizer.rasterize(polygon);

                panel.repaint();
            }
        });
    }
}

