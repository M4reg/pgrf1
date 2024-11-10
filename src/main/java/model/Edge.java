package model;

public class Edge {
    private Point p1, p2;

    public Edge(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    //vrat true pokud je cara vodorovna
    public boolean isHorizontal() {
        return p1.getY() == p2.getY();
    }

    public void orientate(){
        //prehodit podle y1 a y2 -> prohodit
        if(p1.getY() > p2.getY()){
            Point tmp = p1;
            p1 = p2;
            p2 = tmp;
        }
    }

    //Zjistit zda existuje prusecik scanLine s hrano
    public boolean intersectionExist(int y){
        return y >= p1.getY() && y < p2.getY();
    }

    //Vypocita a vrati x ovou souradnici prusecik scanline
    public int getIntersection(int y){
        if (isHorizontal()){
            return p1.getX();
        }
        // Získání souřadnic bodů
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();

        double k = (double)(x2 - x1) / (y2 - y1);
        return (int) (x1 + k * (y - y1));
    }
}
