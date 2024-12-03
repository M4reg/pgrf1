package controller;

import rasterizer.LineRasterizer;
import rasterizer.LineRasterizerGraphics;
import rasterizer.Raster;
import renderer.WiredRanderer;
import solids.Axes;
import solids.Cube;
import solids.Solid;
import view.Panel;
import transforms.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Controller3D implements Controller{
    private final Panel panel;
    private Raster raster;

    //Randerers
    private LineRasterizer lineRasterizer;
    private WiredRanderer wiredRanderer;

    //Solids
    private Solid cube1;
    private Solid cube2;
    private Solid axes;

    //kamera
    private Camera camera;
    private final double cameraSpeed = 0.5;
    private boolean isFirstPerson = false;

    private double angleX = 0;
    private double angleY = 0;

    public Controller3D(Panel panel) {

        this.panel = panel;
        this.raster = panel.getRasterBufferedImage();
        Mat4 proj = new Mat4PerspRH(Math.toRadians(90), (double) panel.getHeight() / panel.getWidth(),
                0.1,
                100
        );

        lineRasterizer = new LineRasterizerGraphics(raster);
        wiredRanderer = new WiredRanderer(
                lineRasterizer,
                panel.getHeight(),
                panel.getWidth(),
                new Mat4(),
                proj
        );

    }

    @Override
    public void InitObjects() {
        initCamera();
        axes = new Axes();
        cube1 = new Cube();
        cube2 = new Cube();

        Mat4 scale = new Mat4Scale(0.3);
        //posunut nahoru
        Mat4 transl = new Mat4Transl(0,0,1.5);

        cube2.setModel(scale.mul(transl));
    }
    private void initCamera() {
        camera = new Camera(new Vec3D(0,0,0), Math.PI+15,Math.PI * 0.125, 5,isFirstPerson);
    }

    @Override
    public void InitListeners() {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP){
                    angleY += 1;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN){
                    angleY -= 1;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT){
                    angleX += 1;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                    angleX -= 1;
                }
                RanderScene();
            }
        });

    }

    public void RanderScene(){
        panel.clear(0xFFFFFF);

        List<Solid> solids = new ArrayList<>();
        solids.add(cube1);
        solids.add(cube2);
        solids.add(axes);

        wiredRanderer.setView(camera.getViewMatrix());
        wiredRanderer.renderSolids(solids);
        wiredRanderer.renderSolid(axes);
        panel.repaint();
    }


}

