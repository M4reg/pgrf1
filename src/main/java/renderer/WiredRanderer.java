package renderer;

import model.Line;
import rasterizer.LineRasterizer;
import solids.Solid;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

import java.awt.*;
import java.util.List;

public class WiredRanderer {

    private LineRasterizer rasterizer;
    private int height, width;
    private Mat4 view, proj;

    public WiredRanderer(LineRasterizer rasterizer, int height, int width, Mat4 view, Mat4 proj) {
        this.rasterizer = rasterizer;
        this.height = height;
        this.width = width;
        this.view = view;
        this.proj = proj;
    }

    public void renderSolid(Solid solid) {
        Mat4 mvp = new Mat4(solid.getModel()).mul(view).mul(proj);

        //iteruji po dvojicích
        for (int i = 0; i < solid.getIb().size(); i+=2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D pointA = solid.getVb().get(indexA);
            Point3D pointB = solid.getVb().get(indexB);

            pointA = pointA.mul(mvp);
            pointB = pointB.mul(mvp);

            //ořezání
            if (isInView(pointA,pointB)) {

                Point3D aDehomog = pointA.mul(1/pointA.getW());
                Point3D bDehomog = pointB.mul(1/pointB.getW());

                Vec3D pointAInWindow = transformToWindow(new Vec3D(aDehomog));
                Vec3D pointBInWindow = transformToWindow(new Vec3D(bDehomog));




                Line line = new Line(
                        (int) Math.round(pointAInWindow.getX()),
                        (int) Math.round(pointAInWindow.getY()),
                        (int) Math.round(pointBInWindow.getX()),
                        (int) Math.round(pointBInWindow.getY())
                );
                Color color = solid.getColors().get(i/2);
                rasterizer.setColor(color);
                rasterizer.rasterize(line);
            }
        }
    }
    private boolean isInView(Point3D pointA, Point3D pointB) {
        boolean boolA = pointA.getX() > -pointA.getW() &&
                pointA.getX() < pointA.getW() &&
                pointA.getY() > -pointA.getW() &&
                pointA.getY() < pointA.getW() &&
                pointA.getZ() > 0 &&
                pointA.getZ() < pointA.getW();

        boolean boolB = pointB.getX() > -pointB.getW() &&
                pointB.getX() < pointB.getW() &&
                pointB.getY() > -pointB.getW() &&
                pointB.getY() < pointB.getW() &&
                pointB.getZ() > 0 &&
                pointB.getZ() < pointB.getW();

        return boolA && boolB;
    }



    public Vec3D transformToWindow(Vec3D v) {
        return v
                .mul(new Vec3D(1,-1,1))
                .add(new Vec3D(1,1,0))
                .mul(new Vec3D((double) (width - 1) /2, (double) (height - 1) /2, 1));
    }

    public void renderSolids(List<Solid> solids) {
        for(Solid solid : solids) {
            renderSolid(solid);
        }
    }


    public void setRasterizer(LineRasterizer rasterizer) {
        this.rasterizer = rasterizer;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
}
