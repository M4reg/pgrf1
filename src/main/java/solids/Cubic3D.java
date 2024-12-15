package solids;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Cubic3D extends Solid {

    private List<Point3D> points = new ArrayList<>(); //Seznam pro body křivky
    private List<Point3D> curvepoints = new ArrayList<>(); //Seznam pro uchování vypočítaných bodů na křivce

    public Cubic3D(String cubicName) {
        switch (cubicName){
            case "BEZIER":
                points.add(new Point3D(-1,-1,-1));
                points.add(new Point3D(-0.5, 0.5, -0.5));
                points.add(new Point3D(0.5, -0.5, 0.5));
                points.add(new Point3D(1, 1, 1));
                create(Cubic.BEZIER);
                break;
            case "COONS":
                points.add(new Point3D(-6,6,-6));
                points.add(new Point3D(0, 0, 0));
                points.add(new Point3D(0, 0, 0));
                points.add(new Point3D(6, 6, 6));
                create(Cubic.COONS);
                break;
            case "FERGUSON":
                points.add(new Point3D(-1, -1, 1));
                points.add(new Point3D(1, -1, -1));
                points.add(new Point3D(0, 2, 0));
                points.add(new Point3D(0, -2, 0));
                create(Cubic.FERGUSON);
                break;
        }
    }
    //Vytvoření křivky zvolené matice
    private void create(Mat4 matrix) {
        Cubic cubic = new Cubic(
                    matrix,
                    points.get(0),
                    points.get(1),
                    points.get(2),
                    points.get(3)
        );
        //počet bodl na křivce
        int points = 500;
        //výpočet bodů na křivce pro hodnoty t od 0 do 1
        for (int i = 0; i < points; i++) {
            double t = i / (double) points;
            curvepoints.add(cubic.compute(t));
        }

        vb.addAll(curvepoints);
        //přidání indexů pro vykreslení křivky
        for (int i = 0; i < curvepoints.size()-1; i++) {
            addIndices(i,i+1); //spojení mezi body pro vykreslení čáry
        }
        model = new Mat4Identity();
    }
}
