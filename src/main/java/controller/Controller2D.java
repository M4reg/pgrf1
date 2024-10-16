package controller;

import model.Line;
import model.Point;
import model.Polygon;
import rasterizer.*;
import view.Panel;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class Controller2D {
    private final Panel panel;
    private LineRasterizer lineRasterizer;

    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;
    private boolean drawingPolygon = false;
    private Point startPoint;
    private ArrayList<Line> lines;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRasterBufferedImage());
        initListeners(panel);
    }

    public void initObjects(RasterBufferedImage raster) {
        lineRasterizer = new LineRasterizerGraphics(raster);
        lineRasterizer.setColor(0x31E628);

        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);

        lines = new ArrayList<>();

    }

    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (drawingPolygon) {
                    panel.clear(Color.BLACK.getRGB());
                    polygon.addPoint(new Point(e.getX(), e.getY()));
                    polygonRasterizer.rasterize(polygon);

                    for(Line line : lines){
                        lineRasterizer.rasterize(line);

                    }

                }else {
                    if (startPoint == null){ //první klik
                        startPoint = new Point(e.getX(), e.getY());
                    }else{
                        Line line = new Line(startPoint, new Point(e.getX(),e.getY()));
                        lines.add(line);
                        lineRasterizer.rasterize(line);
                        startPoint = null;
                    }
                }
                panel.repaint();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C){
                    panel.clear(Color.BLACK.getRGB());
                    polygon.clearPoints();
                    lines.clear();
                    panel.repaint();
                    System.out.println("Smazáno");
                }else if (e.getKeyCode() == KeyEvent.VK_P){
                    drawingPolygon = true;
                    System.out.println("Přepnuto na kreslení polygonu");
                } else if (e.getKeyCode() == KeyEvent.VK_L) {
                    drawingPolygon = false;
                    System.out.println("Přepnuto na kreslení čáry");
                }
            }
        });
    }
}

