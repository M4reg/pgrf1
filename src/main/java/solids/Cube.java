package solids;

import transforms.Mat4Identity;
import transforms.Point3D;

public class Cube extends Solid {
    public Cube() {

        vb.add(new Point3D(-0.3,-0.3,0.3));     //0
        vb.add(new Point3D(0.3,-0.3,0.3));      //1
        vb.add(new Point3D(0.3,0.3,0.3));       //2
        vb.add(new Point3D(-0.3,0.3,0.3));      //3
        vb.add(new Point3D(-0.3,-0.3,-0.3));    //4
        vb.add(new Point3D(0.3,-0.3,-0.3));     //5
        vb.add(new Point3D(0.3,0.3,-0.3));      //6
        vb.add(new Point3D(-0.3,0.3,-0.3));     //7

        //typologie
        addIndices(
                0,1, //Spodní základna
                1,2,
                2,3,
                3,0,
                4,5, //Horní základna
                5,6,
                6,7,
                7,4,
                0,4, //svislé hrany spojující základnu
                1,5,
                2,6,
                3,7
        );

        model = new Mat4Identity();
    }
}
