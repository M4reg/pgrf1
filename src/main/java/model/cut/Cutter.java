package model.cut;

import model.Point;
import java.util.ArrayList;
import java.util.List;

public class Cutter {


    /**
     * @param cuttingPoints orezavaci polygon (cutter)
     * @param pointsToCut   orezavany polygon (originalni polygon)
     * @return novy orezany polygon
     */
    public List<Point> cut(List<Point> cuttingPoints, List<Point> pointsToCut) {
        //kopie ořezávaného polygonu
        List<Point> cutResult = new ArrayList<>(pointsToCut);

        //iteruji přes všechny hrany v seznamu bodů ořezávaného polygonu
        for (int i = 0; i < cuttingPoints.size(); i++) {

            //body po sobě jdoucích ořezávaného polygonu
            Point cP1 = cuttingPoints.get(i);
            Point cP2 = cuttingPoints.get((i + 1) % cuttingPoints.size());

            //kopie výsledného polygonu před ořezáním
            List<Point> in = new ArrayList<>(cutResult);
            cutResult.clear();

            //iterujeme přes všechny hrany ořezávaného polygonu
            for (int j = 0; j < in.size(); j++) {
                Point v1 = in.get(j);
                Point v2 = in.get((j + 1) % in.size());

                //zjištění, zda bod v1 a v2 leží uvnitř ořezávacího polygonu
                boolean v1Inside = isInside(v1, cP1, cP2);
                boolean v2Inside = isInside(v2, cP1, cP2);

                //oba leží ubnitř přidáme druhý bod
                if (v1Inside && v2Inside) {
                    cutResult.add(v2);
                } else if (v1Inside) { //v1 uvnitř v2 je venku přidáme průsečík
                    cutResult.add(intersect(v1, v2, cP1, cP2));

                } else if (v2Inside) { // v2 je uvnitř a v1 je venku přidáme průsečík a v2
                    cutResult.add(intersect(v1, v2, cP1, cP2));
                    cutResult.add(v2);
                }
            }
        }
        //vrátím výsledný polygon
        return cutResult;
    }
    /**
     * @param point bod který testujeme
     * @param cP1   první bod ořezávací hrany
     * @param cP2   druhý bod ořezávací hrany
     * @return true pokud je bod uvnitř ořezávacího polygonu
     */
    private boolean isInside(Point point, Point cP1, Point cP2) {
        int dx = cP2.getX() - cP1.getX();
        int dy = cP2.getY() - cP1.getY();
        int px = point.getX() - cP1.getX();
        int py = point.getY() - cP1.getY();

        //výpočet determinanty pro orientaci bodu vzhledem k hraně
        int determinant = dx * py - dy * px;

        //pokud je determinant >=0 je uvnitř
        return determinant >= 0;
    }
    /**
     * @param v1 první bod ořezávané hrany
     * @param v2 druhý bod ořezávané hrany
     * @param cP1 první bod ořezávací hrany
     * @param cP2 druhý bod ořezávací hrany
     * @return bod průsečíku
     */
    private Point intersect(Point v1, Point v2, Point cP1, Point cP2) {
        int x1 = v1.getX(), y1 = v1.getY();
        int x2 = v2.getX(), y2 = v2.getY();
        int x3 = cP1.getX(), y3 = cP1.getY();
        int x4 = cP2.getX(), y4 = cP2.getY();

        //výpočet pro určení průsečíků dvou čar
        int devide = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        int intersectX = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / devide;
        int intersectY = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / devide;

        return new Point(intersectX, intersectY);
    }
}
