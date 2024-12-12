package solids;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Cubic3D extends Solid {

    private List<Point3D> points = new ArrayList<>();
    private List<Point3D> curvepoints = new ArrayList<>();


    public Cubic3D(String cubicName) {
        switch (cubicName){
            case "BEZIER":
                points.add(new Point3D(-1,-1,-1));
                points.add(new Point3D(0.5, 0.2, -0.5));
                points.add(new Point3D(0.5, -0.2, 0.5));
                points.add(new Point3D(1, 1, 1));
                create(Cubic.BEZIER);
                break;
            case "COONS":
                points.add(new Point3D(-1,-1,-1));
                points.add(new Point3D(-0.5, 2, 0.5));
                points.add(new Point3D(0.5, 2, -0.5));
                points.add(new Point3D(1, 1, 1));
                create(Cubic.COONS);
                break;
            case "FERGUSON":
                points.add(new Point3D(-1, -1, -1));
                points.add(new Point3D(1, 1, 1));
                points.add(new Point3D(1.5, 1.5, 1));
                points.add(new Point3D(-1, 1, 1));
                create(Cubic.FERGUSON);
                break;
        }
    }
    private void create(Mat4 matrix) {
            Cubic cubic = new Cubic(
                    matrix,
                    points.get(0),
                    points.get(1),
                    points.get(2),
                    points.get(3)
            );
            int points = 500;
        for (int i = 0; i < points; i++) {
            double t = i / (double) points;
            curvepoints.add(cubic.compute(t));
        }
        vb.addAll(curvepoints);
        for (int i = 0; i < curvepoints.size()-1; i++) {
            addIndices(i,i+1);
        }
        model = new Mat4Identity();
    }
}
