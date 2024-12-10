package controller;

import rasterizer.LineRasterizer;
import rasterizer.LineRasterizerGraphics;
import rasterizer.Raster;
import renderer.WiredRanderer;
import solids.*;
import view.Panel;
import transforms.*;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller3D implements Controller{
    private final Panel panel;
    private Raster raster;

    //Randerers
    private LineRasterizer lineRasterizer;
    private WiredRanderer wiredRanderer;

    private List<Solid> solids = new ArrayList<>();

    //Solids
    private Solid cube1, cube2, axes, tetrahedron, pyramid;

    //kamera
    private Camera camera;
    private final double cameraSpeed = 0.5;
    private boolean isFirstPerson = false;

    private double angleX = 1;
    private double angleY = 0;
    private double radius = 5;

    private boolean isDragging = false;
    private int lastMouseX, lastMouseY;
    private final double sensitivity = 0.005;

    private boolean isPerspective = true;
    private Mat4 proj;

    public Controller3D(Panel panel) {
        this.panel = panel;
        this.raster = panel.getRasterBufferedImage();

        proj = new Mat4PerspRH(
                Math.toRadians(90),
                (double) panel.getHeight() / panel.getWidth(),
                0.01,
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
        InitObjects();
        InitListeners();
        renderScene();

    }

    @Override
    public void InitObjects() {
        initCamera();
        axes = new Axes();
        cube1 = new Cube();
        cube2 = new Cube();
        tetrahedron = new Tetrahedron();
        pyramid = new Pyramid();

        Mat4 scale = new Mat4Scale(0.4);
        //posunut nahoru
        Mat4 transl = new Mat4Transl(0,0,1.4);
        cube2.setModel(scale.mul(transl));
        tetrahedron.setModel(new Mat4Transl(-2,0,0));
        pyramid.setModel(new Mat4Transl(2,0,0));

        solids.add(cube1);
        solids.add(cube2);
        solids.add(axes);
        solids.add(tetrahedron);
        solids.add(pyramid);

    }
    private void initCamera() {
        camera = new Camera(new Vec3D(0,0,0),
                Math.PI + angleX,
                Math.PI * -0.125 + angleY,
                radius,
                isFirstPerson
        );
    }

    @Override
    public void InitListeners() {
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_A: camera = camera.left(cameraSpeed); break;
                    case KeyEvent.VK_S: camera = camera.backward(cameraSpeed); break;
                    case KeyEvent.VK_D: camera = camera.right(cameraSpeed); break;
                    case KeyEvent.VK_W: camera = camera.forward(cameraSpeed); break;
                    case KeyEvent.VK_SHIFT: camera = camera.up(cameraSpeed); break;
                    case KeyEvent.VK_CONTROL: camera = camera.down(cameraSpeed); break;
                    case KeyEvent.VK_P: toggleProjection(); break;
                }
                renderScene();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    isDragging = true;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    isDragging = false;
                }
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;

                    angleX -= dx * sensitivity;
                    angleY -= dy * sensitivity;

                    // Omezíme vertikální úhel, aby kamera nemohla "převrátit"
                    angleY = Math.max(-Math.PI / 2 + 0.1, Math.min(Math.PI / 2 - 0.1, angleY));
                    camera = camera.withAzimuth(Math.PI + angleX).withZenith(Math.PI * -0.125 + angleY);
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();

                    renderScene();
                }
            }
        });

        panel.addMouseWheelListener(e->{
            int rotation = e.getWheelRotation();
            camera = camera.addRadius(rotation * cameraSpeed * 0.5);
            renderScene();
        });
    }

    private void toggleProjection() {
        isPerspective = !isPerspective;
        if (isPerspective){
            proj = new Mat4PerspRH(Math.toRadians(90), (double) panel.getHeight() / panel.getWidth(), 0.01,100);

        }else {
            proj = new Mat4OrthoRH(8,6,0.01,100);
        }
        wiredRanderer.setProj(proj);
    }

    public void renderScene(){
        panel.clear(0xFFFFFF);
        wiredRanderer.setView(camera.getViewMatrix());
        wiredRanderer.renderSolids(solids);
        panel.repaint();

    }


}

