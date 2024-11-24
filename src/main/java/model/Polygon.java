package model;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<Point> points;

    public Polygon() {
        //nacitani
        this.points = new ArrayList<>();
    }

    public List<Point> getPoints() {
        return points;
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

    public void clearPoints() {
        points.clear();
    }
}
