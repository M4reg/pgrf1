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
    private Point currentEndPoint;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRasterBufferedImage());
        initListeners(panel);
    }

    public void initObjects(RasterBufferedImage raster) {
        lineRasterizer = new LineRasterizerGraphics(raster);
        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        lines = new ArrayList<>();
    }

    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (drawingPolygon) {
                    polygon.addPoint(new Point(e.getX(), e.getY()));
                    // Přidání bodu do polygonu, pokud je aktivní režim kreslení polygonu
                    redraw(); // Vykreslení všeho znovu, včetně polygonu a čar
                } else {
                    // Začátek kreslení čáry
                    startPoint = new Point(e.getX(), e.getY());
                    currentEndPoint = startPoint;
                }
                panel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!drawingPolygon && startPoint != null) {
                    Line line = new Line(startPoint, new Point(e.getX(), e.getY()));
                    lines.add(line); // Přidání čáry do seznamu
                    lineRasterizer.rasterize(line); // Rasterizace konečné čáry
                    startPoint = null;
                    currentEndPoint = null;
                }
                redraw(); // Vykreslení všeho znovu
                panel.repaint();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!drawingPolygon && startPoint != null) {
                    // Aktualizace aktuálního koncového bodu během táhnutí
                    currentEndPoint = new Point(e.getX(), e.getY());

                    // Vyčištění panelu
                    panel.clear(Color.BLACK.getRGB());
                    redraw();

                    //Vykreslení náhledu čáry
                    lineRasterizer.setColor(Color.RED.getRGB());
                    lineRasterizer.rasterize(new Line(startPoint, currentEndPoint));
                }
                panel.repaint();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    panel.clear(Color.BLACK.getRGB());
                    polygon.clearPoints();
                    lines.clear();
                    panel.repaint();
                    System.out.println("Smazáno");
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    drawingPolygon = true;
                    System.out.println("Přepnuto na kreslení polygonu");
                } else if (e.getKeyCode() == KeyEvent.VK_L) {
                    drawingPolygon = false;
                    System.out.println("Přepnuto na kreslení čáry");
                }

                redraw();
                panel.repaint();
            }
        });
    }

    private void redraw() {

        panel.clear(Color.BLACK.getRGB()); // Vyčistit panel
        // Znovu vykreslit polygon, pokud má nějaké body
        if (polygon.getSize() > 0) {
            polygonRasterizer.rasterize(polygon);
        }
        // Znovu vykresli všechny existující čáry
        for (Line line : lines) {
            lineRasterizer.rasterize(line);
        }
    }
}
