package model.filler;

import model.Edge;
import model.Line;
import model.Point;
import model.Polygon;
import rasterizer.LineRasterizer;
import rasterizer.PolygonRasterizer;

import java.util.ArrayList;
import java.util.List;

public class ScanLIne implements Filler {

    private LineRasterizer rasterizer;
    private Polygon polygon;
    private PolygonRasterizer polygonRasterizer;

    public ScanLIne(LineRasterizer rasterizer, Polygon polygon, PolygonRasterizer polygonRasterizer) {
        this.rasterizer = rasterizer;
        this.polygon = polygon;
        this.polygonRasterizer = polygonRasterizer;
    }

    @Override
    public void fill() {
        scanLineFill();
    }

    private void scanLineFill() {
        List<Edge> edges = new ArrayList<>();
        //projdu vsechny pointy polygonu a pro kazde 2 pointy vytvorim hranu

        for (int i = 0; i < polygon.getSize(); i++) {
            Point p1 = polygon.getPoint(i);
            Point p2 = polygon.getPoint((i + 1) % polygon.getSize());

            Edge edge = new Edge(p1, p2);

            //hranu ulozim do seznamu
            if(!edge.isHorizontal()) {
                edge.orientate();
                edges.add(edge);
            }
        }
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

        for (int y = yMin; y < yMax; y++) {
            List<Integer> intersections = new ArrayList<>();

            for (Edge edge : edges) {
                if(edge.intersectionExist(y)){
                    int xIntersection = edge.getIntersection(y);
                    intersections.add(xIntersection);
                }
            }


            intersections.sort(Integer::compareTo);

            for (int i = 0; i < intersections.size() - 1; i ++) {
                int xStart = intersections.get(i);
                int xEnd = intersections.get(i+1);
                Line line = new Line(xStart, y, xEnd, y, 1);
                rasterizer.rasterize(line);
            }
        }
        polygonRasterizer.rasterize(polygon);
    }
}
