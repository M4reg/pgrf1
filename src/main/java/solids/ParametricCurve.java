package solids;

import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class ParametricCurve extends Solid{
    private List<Point3D> curvepoints = new ArrayList<>();

    public ParametricCurve(){
        createParametricCurve();
    }
    private void createParametricCurve(){
        int points = 100;
        //Procházení hodnot t a výpočet bodů na křivce
        for (int i = 0; i < points; i++) {
            double t = i / (double) points;
            double x = t;
            double y = t * t;
            double z = t * t * t;

            curvepoints.add(new Point3D(x,y,z));
        }
        vb.addAll(curvepoints);
        //přidání indexů pro vykreslení křivky
        for (int i = 0; i < curvepoints.size()-1; i++) {
            addIndices(i,i+1);//spojení
        }

        model = new Mat4Identity();
    }
}
