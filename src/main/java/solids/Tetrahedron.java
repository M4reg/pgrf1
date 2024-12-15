package solids;

import transforms.Mat4Identity;
import transforms.Point3D;

import java.awt.*;

public class Tetrahedron extends Solid{
    public Tetrahedron(){
        vb.add(new Point3D(1, 1, 1));
        vb.add(new Point3D(-1, -1, 1));
        vb.add(new Point3D(-1, 1, -1));
        vb.add(new Point3D(1, -1, -1));
        addIndices(
                0,1,
                0,2,
                0,3,
                1,2,
                1,3,
                2,3
        );

        for (int i = 0; i < getIb().size()/2; i++) {
            colors.add(Color.BLACK);
        }
        model = new Mat4Identity();
    }
}
