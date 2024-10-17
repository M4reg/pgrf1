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

    private boolean isShiftPressed = false;

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
                        lineRasterizer.rasterize(new Line(polygon.getPoint(0), polygon.getPoint(1)));
                    }

                    startPoint = null;
                    currentEndPoint = null;

                } else if (!drawingPolygon && startPoint != null) {
                    // Pokud kreslíme úsečku
                    if (isShiftPressed) {
                        // Použijeme zarovnaný koncový bod
                        Line line = new Line(startPoint, currentEndPoint); // Použijeme zarovnaný bod
                        lines.add(line); // Přidání čáry do seznamu
                        lineRasterizer.rasterize(line); // Rasterizace konečné čáry
                    } else {
                        // Pokud nekreslíme s Shiftem, použijeme aktuální pozici myši
                        Line line = new Line(startPoint, new Point(e.getX(), e.getY()));
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
                    lineRasterizer.rasterize(new Line(polygon.getPoint(0), currentEndPoint));

                } else if (drawingPolygon && startPoint != null && polygon.getSize() >= 2) {
                    // Pružná čára od posledního a prvního bodu polygonu
                    currentEndPoint = new Point(e.getX(), e.getY());
                    panel.clear(Color.BLACK.getRGB());
                    redraw();

                    // Pružná čára k prvnímu bodu
                    lineRasterizer.setColor(Color.GREEN.getRGB());
                    lineRasterizer.rasterize(new Line(polygon.getPoint(0), currentEndPoint));
                    // Pružná čára k poslednímu bodu
                    lineRasterizer.rasterize(new Line(polygon.getPoint(polygon.getSize() - 1), currentEndPoint));

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

                        // Zarovnání na 0°, 45°, 90°, 180° (a jejich násobky)
                        int nearestAngle;
                        if (angleDeg >= -22.5 && angleDeg < 22.5) {
                            nearestAngle = 0;   // Vodorovná doprava
                        } else if (angleDeg >= 22.5 && angleDeg < 67.5) {
                            nearestAngle = 45;  // 45°
                        } else if (angleDeg >= 67.5 && angleDeg < 112.5) {
                            nearestAngle = 90;  // 90° (svislá nahoru)
                        } else if (angleDeg >= 112.5 && angleDeg < 157.5) {
                            nearestAngle = 135; // 135° (úhlopříčka nahoru/doleva)
                        } else if (angleDeg >= 157.5 || angleDeg < -157.5) {
                            nearestAngle = 180; // 180° (vodorovná doleva)
                        } else if (angleDeg >= -157.5 && angleDeg < -112.5) {
                            nearestAngle = 225; // 225° (úhlopříčka dolů/doleva)
                        } else if (angleDeg >= -112.5 && angleDeg < -67.5) {
                            nearestAngle = 270; // 270° (svislá dolů)
                        } else if (angleDeg >= -67.5 && angleDeg < -22.5) {
                            nearestAngle = 315; // 315° (úhlopříčka dolů/doprava)
                        } else {
                            nearestAngle = 0; // fallback
                        }

                        // Převod zarovnaného úhlu zpět na radiány
                        double radians = Math.toRadians(nearestAngle);

                        // Výpočet nového koncového bodu na základě zarovnaného úhlu
                        int newX = (int) (startPoint.getX() + distance * Math.cos(radians));
                        int newY = (int) (startPoint.getY() + distance * Math.sin(radians));
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
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    alignmentMode = !alignmentMode; // Přepni režim zarovnání
                    if (alignmentMode) {
                        System.out.println("Režim zarovnání aktivován");
                        isShiftPressed = true;
                        panel.updateStav(isShiftPressed);
                    } else {
                        System.out.println("Režim zarovnání deaktivován");
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

        // Pokud kreslíme polygon a máme 2 body, čára mezi body bude viditelna
        if (drawingPolygon && polygon.getSize() == 2) {
            lineRasterizer.setColor(Color.GREEN.getRGB());
            lineRasterizer.rasterize(new Line(polygon.getPoint(0), polygon.getPoint(1)));
        }

        // Znovu vykresli všechny existující čáry
        for (Line line : lines) {
            lineRasterizer.rasterize(line);
        }

    }
}
