package controller;

import model.*;
import model.Point;
import model.Polygon;
import model.cut.Cutter;
import model.filler.ScanLIne;
import model.filler.SeedFill;
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
    private CuttingPolygon cuttingPolygon;
    private boolean isDrawingPentagonActive = false;
    private Point center;
    private java.util.List<Pentagon> pentagons = new ArrayList<>();
    private boolean drawingLine = false;
    private Pentagon pentagon;
    private java.util.List<SeedFill> seedFills = new ArrayList<>();


    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRasterBufferedImage());
        initListeners(panel);
        redraw();
    }

    public void initObjects(RasterBufferedImage raster) {
        lineRasterizer = new LineRasterizerGraphics(raster);
        polygon = new Polygon();
        cuttingPolygon = new CuttingPolygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer, thickness);
        lines = new ArrayList<>();
    }

    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1 && isDrawingPentagonActive){
                    center = new Point(e.getX(), e.getY());
                    pentagon = null;
                }
                if (!drawingPolygon && e.getButton() == MouseEvent.BUTTON3) {
                    int clickedColor = panel.getRasterBufferedImage().getPixel(e.getX(),e.getY());

                    //pokud klikneme na hranici objektu nevyplňuj
                    if (clickedColor == Color.RED.getRGB()){
                        return;
                    }

                    SeedFill seedFill = new SeedFill(
                            panel.getRasterBufferedImage(),
                            e.getX(),
                            e.getY(),
                            Color.CYAN.getRGB());
                    seedFills.add(seedFill);
                    seedFill.fill();
                    redraw();
                    panel.repaint();
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
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
                        lineRasterizer.setColor(Color.RED.getRGB());
                        redraw();
                    } else if(drawingLine){
                        // Pokud nekreslíme polygon, můžeme kreslit čáru
                        lineRasterizer.setColor(Color.GREEN.getRGB());
                        startPoint = new Point(e.getX(), e.getY());
                        currentEndPoint = startPoint;

                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (isDrawingPentagonActive){
                        int radius = (int) Math.sqrt(Math.pow(e.getX() - center.getX(), 2) + Math.pow(e.getY() - center.getY(), 2));
                        double angle = Math.atan2(e.getY() - center.getY(), e.getX() - center.getX());
                        pentagon = new Pentagon(center.getX(), center.getY(), radius);
                        pentagon.setRotationAngle(angle);
                        pentagons.add(pentagon);
                        redraw();
                        panel.repaint();
                    }

                    if (drawingPolygon && startPoint != null) {
                        // Po uvolnění tlačítka přidáme bod do polygonu
                        polygon.addPoint(new Point(e.getX(), e.getY()));

                        // Nastavení koncového bodu
                        currentEndPoint = new Point(e.getX(), e.getY());

                        // Pro druhý bod (polygon má teď 2 body) je třeba vykreslit čáru
                        if (polygon.getSize() == 2) {
                            lineRasterizer.setColor(Color.RED.getRGB());
                            lineRasterizer.rasterize(new Line(polygon.getPoint(0), polygon.getPoint(1), thickness));
                        }

                        if (polygon.getSize() >= 3) {

                            ScanLIne scanLineFiller = new ScanLIne(lineRasterizer, polygon, polygonRasterizer, Color.CYAN.getRGB());
                            redraw();
                            scanLineFiller.fill();
                            panel.repaint();
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

            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Zpracovávej pouze tažení levým tlačítkem
                if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) {
                    return;
                }
                    if (isDrawingPentagonActive && center != null) {
                        // Dynamické vykreslování pentagonu během tažení myši
                        int radius = (int) Math.sqrt(Math.pow(e.getX() - center.getX(), 2) + Math.pow(e.getY() - center.getY(), 2));
                        double angle = Math.atan2(e.getY() - center.getY(), e.getX() - center.getX());

                        if (pentagon == null) {
                            pentagon = new Pentagon(center.getX(), center.getY(), radius);
                        } else {
                            pentagon.setRadius(radius);
                            pentagon.setRotationAngle(angle);
                        }
                        panel.clear(Color.BLACK.getRGB());
                        redraw();
                        pentagon.draw(polygonRasterizer);
                        panel.repaint();
                    }
                    if (drawingPolygon && polygon.getSize() == 1 && startPoint != null) {
                        // Pružná čára od prvního bodu polygonu
                        currentEndPoint = new Point(e.getX(), e.getY());
                        panel.clear(Color.BLACK.getRGB());
                        redraw();

                        // Pružná čára k prvnímu bodu
                        lineRasterizer.setColor(Color.red.getRGB());
                        lineRasterizer.rasterize(new Line(polygon.getPoint(0), currentEndPoint, 1));

                    } else if (drawingPolygon && startPoint != null && polygon.getSize() >= 2) {
                        // Pružná čára od posledního a prvního bodu polygonu
                        currentEndPoint = new Point(e.getX(), e.getY());
                        panel.clear(Color.BLACK.getRGB());
                        redraw();

                        // Pružná čára k prvnímu bodu
                        lineRasterizer.setColor(Color.RED.getRGB());
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
                        lineRasterizer.setColor(Color.RED.getRGB());
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
                    if (pentagon != null) {
                        pentagon.clearPoints(); // Vymazání bodů pentagonu, pokud existuje
                    }
                    seedFills.clear();
                    pentagons.clear();
                    lines.clear();
                    panel.repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_P) {//klávesa pro kreslení polygonu
                    drawingPolygon = true;
                    drawingLine = false;
                    isDrawingPentagonActive = false;
                }else if (e.getKeyCode() == KeyEvent.VK_O) {//klávesa pro kreslení polygonu
                    drawingPolygon = false;
                    drawingLine = false;
                    isDrawingPentagonActive = true;
                } else if (e.getKeyCode() == KeyEvent.VK_L) {//klávesa pro kreslení čáry
                    isDrawingPentagonActive = false;
                    drawingLine = true;
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
        cuttingPolygon.drawCuttingPolygon(polygonRasterizer);

        // Znovu vykreslit polygon, pokud má nějaké body
        if (polygon.getSize() > 0) {
            polygonRasterizer.rasterize(polygon);
        }

        // Pokud kreslíme polygon a máme 2 body, čára mezi body bude viditelná
        if (drawingPolygon && polygon.getSize() == 2) {
            lineRasterizer.setColor(Color.RED.getRGB());
            lineRasterizer.rasterize(new Line(polygon.getPoint(0), polygon.getPoint(1), thickness));
        }

        if (polygon.getSize() >=3){
            ScanLIne scanLineFiller = new ScanLIne(lineRasterizer, polygon, polygonRasterizer, Color.CYAN.getRGB());
            scanLineFiller.fill();
        }

        // Znovu vykresli všechny existující čáry
        for (Line line : lines) {
            lineRasterizer.setColor(Color.RED.getRGB());
            lineRasterizer.rasterize(line);
        }


        for (Pentagon pentagon : pentagons) {
                pentagon.draw(polygonRasterizer);
        }

        for (SeedFill fill : seedFills) {
            fill.fill();
        }
    }
}
