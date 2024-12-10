package solids;

import transforms.Mat4Identity;
import transforms.Mat4Transl;
import transforms.Point3D;

import java.awt.*;

public class Pyramid extends Solid{
    public Pyramid(){
        vb.add(new Point3D(1, 1, 0));   // V0 - pravý horní roh základny
        vb.add(new Point3D(-1, 1, 0));  // V1 - levý horní roh základny
        vb.add(new Point3D(-1, -1, 0)); // V2 - levý dolní roh základny
        vb.add(new Point3D(1, -1, 0));  // V3 - pravý dolní roh základny
        vb.add(new Point3D(0, 0, 1));       // V4 - vrchol jehlanu (nad středem základny)

        addIndices(
                0, 1, // Hrany základny
                1, 2,
                2, 3,
                3, 0,

                0, 4, // Spojení V0 s vrcholem
                1, 4, // Spojení V1 s vrcholem
                2, 4, // Spojení V2 s vrcholem
                3, 4  // Spojení V3 s vrcholem
        );

        for (int i = 0; i < getIb().size() / 2 ; i++) {
            colors.add(Color.BLACK);
        }
        model = new Mat4Identity();
    }
}
