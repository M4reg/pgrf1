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

    private LineRasterizer lineRasterizer;
    private WiredRanderer wiredRanderer;

    private Solid cube;
    private Solid axes;

    private double angleX = 0;
    private double angleY = 0;

    public Controller3D(Panel panel) {

        this.panel = panel;
        this.raster = panel.getRasterBufferedImage();

        lineRasterizer = new LineRasterizerGraphics(raster);

        wiredRanderer = new WiredRanderer(
                lineRasterizer,
                panel.getHeight(),
                panel.getWidth()
        );

    }

    @Override
    public void InitObjects() {
        cube = new Cube();
        axes = new Axes();
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
        wiredRanderer.renderSolid(cube);
        panel.repaint();
    }


}

