package model.cut;

import model.Edge;
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
        List<Point> cutResult = new ArrayList<>(pointsToCut);

        //iteruji přes všechny hrany v seznamu bodů ořezávaného polygonu
        for (int i = 0; i < cuttingPoints.size(); i++) {

            Point cP1 = cuttingPoints.get(i);
            Point cP2 = cuttingPoints.get((i + 1) % cuttingPoints.size());

            List<Point> in = new ArrayList<>(cutResult);
            cutResult.clear();

            for (int j = 0; j < in.size(); j++) {
                Point v1 = in.get(j);
                Point v2 = in.get((j + 1) % in.size());

                boolean v1Inside = isInside(v1, cP1, cP2);
                boolean v2Inside = isInside(v2, cP1, cP2);

                if (v1Inside && v2Inside) {
                    cutResult.add(v2);
                } else if (v1Inside) {
                    cutResult.add(intersect(v1, v2, cP1, cP2));
                } else if (v2Inside) {
                    cutResult.add(intersect(v1, v2, cP1, cP2));
                    cutResult.add(v2);
                }
            }
        }
        //vrátím výsledný polygon
        return cutResult;
    }

    private boolean isInside(Point point, Point cP1, Point cP2) {
        int dx = cP2.getX() - cP1.getX();
        int dy = cP2.getY() - cP1.getY();
        int px = point.getX() - cP1.getX();
        int py = point.getY() - cP1.getY();

        int determinant = dx * py - dy * px;
        return determinant >=0;
    }

    private Point intersect(Point v1, Point v2, Point cP1, Point cP2) {
        int x1 = v1.getX(), y1 = v1.getY();
        int x2 = v2.getX(), y2 = v2.getY();
        int x3 = cP1.getX(), y3 = cP1.getY();
        int x4 = cP2.getX(), y4 = cP2.getY();

        int devide = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        int intersectX = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / devide;
        int intersectY = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / devide;

        return new Point(intersectX, intersectY);
    }
}
