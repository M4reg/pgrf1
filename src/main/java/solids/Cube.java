package solids;

import transforms.Point3D;

public class Cube extends Solid {
    public Cube() {

        vb.add(new Point3D(-0.1,-0.1,0.1));     //0
        vb.add(new Point3D(0.1,-0.1,0.1));      //1
        vb.add(new Point3D(0.1,0.1,0.1));       //2
        vb.add(new Point3D(-0.1,0.1,0.1));      //3
        vb.add(new Point3D(-0.1,-0.1,-0.1));    //4
        vb.add(new Point3D(0.1,-0.1,-0.1));     //5
        vb.add(new Point3D(0.1,0.1,-0.1));      //6
        vb.add(new Point3D(-0.1,0.1,-0.1));     //7

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
    }
}