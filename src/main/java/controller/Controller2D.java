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
                    if (polygon.getSize() == 0) {
                        // První bod polygonu
                        polygon.addPoint(new Point(e.getX(), e.getY()));
                        startPoint = new Point(e.getX(), e.getY()); // uložíme počáteční bod pro pružnou čáru
                        currentEndPoint = startPoint;
                    } else if(polygon.getSize() >= 1){

                        // Po přidání druhého bodu již kreslíme pružné čáry
                        startPoint = new Point(e.getX(), e.getY());
                        currentEndPoint = startPoint;
                    }
                    redraw();
                }else {
                    // Pokud nekreslíme polygon, můžeme kreslit čáru
                    startPoint = new Point(e.getX(), e.getY());
                    currentEndPoint = startPoint;
                }
                panel.repaint();

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (drawingPolygon && startPoint != null){
                    // Po uvolnění tlačítka přidáme bod do polygonu
                    polygon.addPoint(new Point(e.getX(), e.getY()));

                    // Nastavení koncového bodu
                    currentEndPoint = new Point(e.getX(), e.getY());

                    // Pro druhý bod (polygon má teď 2 body) je třeba vykreslit čáru
                    if (polygon.getSize() == 2) {
                        lineRasterizer.setColor(Color.RED.getRGB());
                        lineRasterizer.rasterize(new Line(polygon.getPoint(0), polygon.getPoint(1)));
                    }

                    startPoint = null;
                    currentEndPoint = null;

                } else if (!drawingPolygon && startPoint != null) {
                    // Pokud kreslíme úsečku
                    Line line = new Line(startPoint, new Point(e.getX(), e.getY()));
                    lines.add(line); // Přidání čáry do seznamu
                    lineRasterizer.rasterize(line); // Rasterizace konečné čáry
                    startPoint = null;
                    currentEndPoint = null;
                }
                redraw(); // Aktualizace plátna
                panel.repaint();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawingPolygon && polygon.getSize() == 1 && startPoint != null)
                {
                    // Pružná čára od prvního bodu polygonu
                    currentEndPoint = new Point(e.getX(), e.getY());
                    panel.clear(Color.BLACK.getRGB());
                    redraw();

                    // Pružná čára k prvnímu bodu
                    lineRasterizer.setColor(Color.RED.getRGB());
                    lineRasterizer.rasterize(new Line(polygon.getPoint(0), currentEndPoint));

                } else if (drawingPolygon && startPoint != null && polygon.getSize() >= 2) {
                    // Pružná čára od posledního a prvního bodu polygonu
                    currentEndPoint = new Point(e.getX(), e.getY());
                    panel.clear(Color.BLACK.getRGB());
                    redraw();

                    // Pružná čára k prvnímu bodu
                    lineRasterizer.setColor(Color.RED.getRGB());
                    lineRasterizer.rasterize(new Line(polygon.getPoint(0), currentEndPoint));
                    // Pružná čára k poslednímu bodu
                    lineRasterizer.rasterize(new Line(polygon.getPoint(polygon.getSize() - 1), currentEndPoint));

                } else if (!drawingPolygon && startPoint != null) {
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

        // Pokud kreslíme polygon a máme 2 body, čára mezi body bude viditelna
        if (drawingPolygon && polygon.getSize() == 2) {
            lineRasterizer.setColor(Color.RED.getRGB());
            lineRasterizer.rasterize(new Line(polygon.getPoint(0), polygon.getPoint(1)));
        }

        // Znovu vykresli všechny existující čáry
        for (Line line : lines) {
            lineRasterizer.rasterize(line);
        }

    }
}
