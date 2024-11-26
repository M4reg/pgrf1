package renderer;

import model.Line;
import rasterizer.LineRasterizer;
import solids.Solid;
import transforms.Point3D;
import transforms.Vec3D;

import java.awt.*;
import java.util.List;

public class WiredRanderer {

    private LineRasterizer rasterizer;
    private int height, width;

    public WiredRanderer(LineRasterizer rasterizer, int height, int width) {
        this.rasterizer = rasterizer;
        this.height = height;
        this.width = width;
    }

    public void renderSolid(Solid solid) {
        //iteruji po dvojicích
        for (int i = 0; i < solid.getIb().size(); i+=2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D pointA = solid.getVb().get(indexA);
            Point3D pointB = solid.getVb().get(indexB);

            Vec3D pointAInWindow = transformToWindow(new Vec3D(pointA));
            Vec3D pointBInWindow = transformToWindow(new Vec3D(pointB));

            Line line = new Line(
                    (int) Math.round(pointAInWindow.getX()),
                    (int) Math.round(pointAInWindow.getY()),
                    (int) Math.round(pointBInWindow.getX()),
                    (int) Math.round(pointBInWindow.getY())
            );

            rasterizer.setColor(Color.BLACK);
            rasterizer.rasterize(line);

        }
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
}
