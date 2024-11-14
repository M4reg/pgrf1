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
    private List<Point> cut(List<Point> cuttingPoints, List<Point> pointsToCut) {
        List<Point> cutResult = new ArrayList<>(pointsToCut);

        //iteruji přes všechny hrany v seznamu bodů ořezávaného polygonu
        for (int i = 0; i < pointsToCut.size(); i++) {

            //definuji hranu orezevaci polygonu
            Point cP1 = cuttingPoints.get(i);
            Point cP2 = cuttingPoints.get((i + 1) % cuttingPoints.size());

            Edge edge = new Edge(cP1, cP2);

            List<Point> in = new ArrayList<>(cutResult);
            cutResult.clear();

            Point v1 = in.get(in.size() - 1);

            for (Point v2 : in) {
                boolean v2Inside = isInside(v2, edge);
                boolean v1Inside = isInside(v1, edge);

                if (v2Inside) {
                    if (!v1Inside) {
                        cutResult.add(intersect(v1,v2,edge));
                    }
                    cutResult.add(v2);
                }else if (v1Inside) {
                    cutResult.add(intersect(v1,v2,edge));
                }
                v1 = v2;
            }

        }

        //vrátím výsledný polygon
        return cutResult;
    }

    private boolean isInside(Point point, Edge edge) {
        int a = edge.getP2().getX() - edge.getP1().getY();
        int b = edge.getP1().getX() - edge.getP2().getY();
        int c = edge.getP2().getX() - edge.getP1().getX() - edge.getP1().getX() * edge.getP1().getY();

        int num = a * point.getX() + b * point.getY() + c;

        return num >= 0;
    }

    private Point intersect(Point v1, Point v2, Edge edge) {
        int x1 = v1.getX(), y1 = v1.getY();
        int x2 = v2.getX(), y2 = v2.getY();
        int x3 = edge.getP1().getX(), y3 = edge.getP1().getY();
        int x4 = edge.getP2().getX(), y4 = edge.getP2().getY();

        int devide = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (devide == 0) {
            return null;
        }

        int intersectX = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / devide;
        int intersectY = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / devide;

        return new Point(intersectX, intersectY);
    }
}
