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
    private boolean alignmentMode = false; //Pro sledování režimu kreslení
    private final int thickness = 5;
    private final double tolerance = 22.5;
    private boolean isShiftPressed = false; //Pro sledování režimu zarovnávání

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRasterBufferedImage());
        initListeners(panel);
    }

    public void initObjects(RasterBufferedImage raster) {
        lineRasterizer = new LineRasterizerGraphics(raster);
        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer, thickness);
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
                        currentEndPoint = startPoint; //nastavení aktuálního koncového bodu

                    } else if (polygon.getSize() >= 1) {
                        // Po přidání druhého bodu již kreslíme pružné čáry
                        startPoint = new Point(e.getX(), e.getY());
                        currentEndPoint = startPoint;
                    }
                    redraw();
                } else {
                    // Pokud nekreslíme polygon, můžeme kreslit čáru
                    startPoint = new Point(e.getX(), e.getY());
                    currentEndPoint = startPoint;

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (drawingPolygon && startPoint != null) {
                    // Po uvolnění tlačítka přidáme bod do polygonu
                    polygon.addPoint(new Point(e.getX(), e.getY()));

                    // Nastavení koncového bodu
                    currentEndPoint = new Point(e.getX(), e.getY());

                    // Pro druhý bod (polygon má teď 2 body) je třeba vykreslit čáru
                    if (polygon.getSize() == 2) {
                        lineRasterizer.setColor(Color.GREEN.getRGB());
                        lineRasterizer.rasterize(new Line(polygon.getPoint(0), polygon.getPoint(1), thickness));
                    }

                    startPoint = null;
                    currentEndPoint = null;

                } else if (!drawingPolygon && startPoint != null) {
                    // Pokud kreslíme úsečku
                    if (isShiftPressed) {
                        // Použijeme zarovnaný koncový bod
                        Line line = new Line(startPoint, currentEndPoint, thickness); // Použijeme zarovnaný bod
                        lines.add(line); // Přidání čáry do seznamu
                        lineRasterizer.rasterize(line); // Rasterizace konečné čáry
                    } else {
                        // Pokud nekreslíme s Shiftem, použijeme aktuální pozici myši
                        Line line = new Line(startPoint, new Point(e.getX(), e.getY()), thickness);
                        lines.add(line); // Přidání čáry do seznamu
                        lineRasterizer.rasterize(line); // Rasterizace konečné čáry
                    }
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
                if (drawingPolygon && polygon.getSize() == 1 && startPoint != null) {
                    // Pružná čára od prvního bodu polygonu
                    currentEndPoint = new Point(e.getX(), e.getY());
                    panel.clear(Color.BLACK.getRGB());
                    redraw();

                    // Pružná čára k prvnímu bodu
                    lineRasterizer.setColor(Color.GREEN.getRGB());
                    lineRasterizer.rasterize(new Line(polygon.getPoint(0), currentEndPoint, 1));

                } else if (drawingPolygon && startPoint != null && polygon.getSize() >= 2) {
                    // Pružná čára od posledního a prvního bodu polygonu
                    currentEndPoint = new Point(e.getX(), e.getY());
                    panel.clear(Color.BLACK.getRGB());
                    redraw();

                    // Pružná čára k prvnímu bodu
                    lineRasterizer.setColor(Color.GREEN.getRGB());
                    lineRasterizer.rasterize(new Line(polygon.getPoint(0), currentEndPoint, 1));
                    // Pružná čára k poslednímu bodu
                    lineRasterizer.rasterize(new Line(polygon.getPoint(polygon.getSize() - 1), currentEndPoint, 1));

                } else if (!drawingPolygon && startPoint != null) {

                    // Pokud je Shift stisknutý, najdeme nejbližší polohu čáry
                    if (isShiftPressed) {
                        // Určujeme novou koncovou pozici
                        int x = e.getX();
                        int y = e.getY();

                        // Vypočítáme vzdálenost mezi startovním a aktuálním bodem
                        double dx = x - startPoint.getX();
                        double dy = y - startPoint.getY();
                        double angle = Math.atan2(dy, dx); // úhel v radiánech
                        double distance = Math.sqrt(dx * dx + dy * dy); // vzdálenost

                        // Převod úhlu na stupně
                        double angleDeg = Math.toDegrees(angle);

                        //Nejbližší úhel který je násobek 45
                        int nearestAngle = (int) Math.toDegrees(angleDeg / 45) * 45;

                        if (Math.abs(angleDeg - nearestAngle) > tolerance) {
                            nearestAngle = (int) Math.round(angleDeg / 45) * 45;//zaokrouhlení na nejblížší úhel
                        }

                        // Převod zarovnaného úhlu zpět na radiány
                        double radians = Math.toRadians(nearestAngle);

                        // Výpočet nového koncového bodu na základě zarovnaného úhlu

                        //určuje jak daleko se posuneme v horizontálním směru od startpoint na základě úhlu
                        int newX = (int) Math.round(startPoint.getX() + distance * Math.cos(radians));
                        //určuje jak daleko se posuneme v vertikálním směru od startpoint na základě úhlu
                        int newY = (int) Math.round(startPoint.getY() + distance * Math.sin(radians));
                        currentEndPoint = new Point(newX, newY);

                    } else {
                        // Normální chování, aktualizace koncového bodu
                        currentEndPoint = new Point(e.getX(), e.getY());
                    }

                    // Vyčištění panelu
                    panel.clear(Color.BLACK.getRGB());
                    redraw();

                    // Vykreslení náhledu čáry
                    lineRasterizer.setColor(Color.GREEN.getRGB());
                    lineRasterizer.rasterize(new Line(startPoint, currentEndPoint, 1));
                }
                panel.repaint();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {//Klávesa pro mazání
                    panel.clear(Color.BLACK.getRGB());
                    polygon.clearPoints();
                    lines.clear();
                    panel.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_P) {//klávesa pro kreslení polygonu
                    drawingPolygon = true;
                } else if (e.getKeyCode() == KeyEvent.VK_L) {//klávesa pro kreslení čáry
                    drawingPolygon = false;
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {//Pro kreslení čáry se zarovnáním
                    alignmentMode = !alignmentMode; // Přepni režim zarovnání
                    if (alignmentMode) {
                        isShiftPressed = true;
                        panel.updateStav(isShiftPressed);
                    } else {
                        isShiftPressed = false;
                        panel.updateStav(isShiftPressed);
                    }
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

        // Pokud kreslíme polygon a máme 2 body, čára mezi body bude viditelná
        if (drawingPolygon && polygon.getSize() == 2) {
            lineRasterizer.setColor(Color.GREEN.getRGB());
            lineRasterizer.rasterize(new Line(polygon.getPoint(0), polygon.getPoint(1), thickness));
        }

        // Znovu vykresli všechny existující čáry
        for (Line line : lines) {
            lineRasterizer.rasterize(line);
        }

    }
}
