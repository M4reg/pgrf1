package model;

import java.util.ArrayList;

public class Polygon {
    private final ArrayList<Point> points;
    private boolean isDrawing;

    public Polygon() {
        //nacitani
        this.points = new ArrayList<>();
        this.isDrawing = false;
    }

    public void addPoint(Point p) {
        this.points.add(p);
    }

    //ziskani souradnic  ktere jiz mame ulozene
    public Point getPoint(int index) {
        return this.points.get(index);
    }

    //velikost pole
    public int getSize() {
        return this.points.size();
    }
    public void clearPoints(){
        points.clear();
    }
    public void startDrawing(){
        this.isDrawing = true;
    }
    public void stopDrawing(){
        this.isDrawing = false;
    }

    public boolean isDrawingPolygon(){
        return this.isDrawing;
    }
}
