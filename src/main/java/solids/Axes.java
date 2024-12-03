package solids;

import transforms.Col;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.awt.*;

public class Axes extends Solid{
    public Axes() {
        //X osa
        vb.add(new Point3D(0,0,0)); //počáteční bod osy X
        vb.add(new Point3D(1,0,0)); //konečný bod osy X
        addIndices(0,1); //definice linie pro osu X
        colors.add(Color.red);

        color = new Col(0xff0000); // červená barva pro osu x

        //Y osa
        vb.add(new Point3D(0,0,0));
        vb.add(new Point3D(0,1,0));
        addIndices(2,3);
        colors.add(Color.green);

        //Z osa
        vb.add(new Point3D(0,0,0));
        vb.add(new Point3D(0,0,1));
        addIndices(4,5);
        colors.add(Color.blue);

        model = new Mat4Identity();
    }
}
