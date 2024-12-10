package solids;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Cubic3D extends Solid {

    private List<Point3D> points = new ArrayList<>();

    public Cubic3D(String cubicName) {
        switch (cubicName){
            case "BEZIER":
                points.add(new Point3D(-1,-1,-1));
                points.add(new Point3D(-1,-1,-1));
                points.add(new Point3D(-1,-1,-1));
                points.add(new Point3D(-1,-1,-1));
                create(Cubic.BEZIER);
                break;


            default:
                break;
        }
    }
    private void create(Mat4 matrix) {
        // TODO: dokoncit

        // Cubic compute() přenést do vertex bufferů a index bufferů pomocí cyklů
    }
}
