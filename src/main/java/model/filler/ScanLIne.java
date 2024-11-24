package model.filler;

import model.Edge;
import model.Line;
import model.Point;
import model.Polygon;
import rasterizer.LineRasterizer;
import rasterizer.PolygonRasterizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ScanLIne implements Filler {

    private LineRasterizer rasterizer;
    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;
    private int fiilColor;

    public ScanLIne(LineRasterizer rasterizer, Polygon polygon, PolygonRasterizer polygonRasterizer, int fillColor) {
        this.rasterizer = rasterizer;
        this.polygon = polygon;
        this.polygonRasterizer = polygonRasterizer;
        this.fiilColor = fillColor;
    }

    @Override
    public void fill() {
        scanLineFill();
    }

    private void scanLineFill() {
        List<Edge> edges = new ArrayList<>();
        //projdu všechny pointy polygonu a pro každé 2 pointy vytvořím hranu

        for (int i = 0; i < polygon.getSize(); i++) {
            Point p1 = polygon.getPoint(i);
            Point p2 = polygon.getPoint((i + 1) % polygon.getSize());

            Edge edge = new Edge(p1, p2);

            //hranu uložím do seznamu pokud není horizontální
            if(!edge.isHorizontal()) {
                edge.orientate();
                edges.add(edge);
            }
        }
        //Uložení min a max hodnoty Y v oblasti kde bude probíhat scan-line
        int yMin = polygon.getPoint(0).getY();
        int yMax = yMin;

        for(int i = 0 ; i < polygon.getSize(); i++){
            int y = polygon.getPoint(i).getY();
            if (y < yMin){
                yMin = y;
            }
            if (y > yMax){
                yMax = y;
            }
        }
        rasterizer.setColor(Color.CYAN.getRGB());


        for (int y = yMin; y <= yMax; y++) {
            //seznam průsečíků
            List<Integer> intersections = new ArrayList<>();

            //zjišťujeme pro každou hranu polygonu zda má průsečík s aktuální horizontální čárou
            for (Edge edge : edges) {
                if(edge.intersectionExist(y)){
                    int xIntersection = edge.getIntersection(y);
                    intersections.add(xIntersection);
                }
            }
            //seřazení průsečíků podle X souřadnic
            intersections.sort(Integer::compareTo);

            //procházení všech úrůsečíků po dvou, pro vykreslení horiznotální čáry mezi nimi
            for (int i = 0; i < intersections.size() - 1; i +=2) {
                int xStart = intersections.get(i);//počátek
                int xEnd = intersections.get(i+1);//konec
                Line line = new Line(xStart, y, xEnd, y, 1);//vytvoření čáry
                rasterizer.rasterize(line);
            }
        }
        rasterizer.setColor(Color.RED.getRGB());
        polygonRasterizer.rasterize(polygon);
    }
}
