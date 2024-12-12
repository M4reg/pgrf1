package controller;

import rasterizer.LineRasterizer;
import rasterizer.LineRasterizerGraphics;
import rasterizer.Raster;
import renderer.WiredRanderer;
import solids.*;
import view.Panel;
import transforms.*;

import java.awt.*;
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
    private int selectedSolidIndex = -1;

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

    private double scaleFactor = 1.1;
    private double translationFactor = 0.2;
    private boolean scalingMode = false;
    private boolean movingMode = false;
    private boolean rotatingMode = false;



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

        solids.clear();

        Mat4 scale = new Mat4Scale(0.4);
        //posunut nahoru
        Mat4 transl = new Mat4Transl(0,0,1.4);
        cube2.setModel(scale.mul(transl));
        tetrahedron.setModel(new Mat4Transl(-2.5,0,0));
        pyramid.setModel(new Mat4Transl(2.5,0,0));

        Solid cubicSolid1 = new Cubic3D("BEZIER");
        cubicSolid1.setModel(new Mat4Transl(-1,1,1));
        Solid cubicSolid2 = new Cubic3D("FERGUSON");
        cubicSolid1.setModel(new Mat4Transl(-1,1,1));
        Solid cubicSolid3 = new Cubic3D("COONS");
        cubicSolid1.setModel(new Mat4Transl(-1,1,1));


        solids.add(cube1);
        solids.add(cube2);
        solids.add(tetrahedron);
        solids.add(pyramid);
        solids.add(cubicSolid1);
        solids.add(cubicSolid2);
        solids.add(cubicSolid3);
        solids.add(axes);
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
                    case KeyEvent.VK_W: camera = camera.forward(cameraSpeed); break;
                    case KeyEvent.VK_S: camera = camera.backward(cameraSpeed); break;
                    case KeyEvent.VK_A: camera = camera.left(cameraSpeed); break;
                    case KeyEvent.VK_D: camera = camera.right(cameraSpeed); break;
                    case KeyEvent.VK_P: changeProjection(); break;
                    case KeyEvent.VK_R: selectNextSolid(); break;
                    case KeyEvent.VK_T: selectPreviousSolid(); break;
                    case KeyEvent.VK_C: initCamera(); break;

                    case KeyEvent.VK_M:
                        movingMode = !movingMode;
                        scalingMode = false;
                        rotatingMode = false;
                        break;
                    case KeyEvent.VK_V:
                        scalingMode = !scalingMode;
                        movingMode = false;
                        rotatingMode = false;
                        break;
                    case KeyEvent.VK_B:
                        rotatingMode = !rotatingMode;
                        scalingMode = false;
                        movingMode = false;
                        break;

                    case KeyEvent.VK_SHIFT:
                        if (rotatingMode && selectedSolidIndex != -1) {
                            // Otoč okolo osy Z
                            rotateSelectedZ(Math.toRadians(5));
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose Z
                            translateSelected(0, 0, translationFactor);
                        } else {
                            // Posouvání kamery nahoru
                            camera = camera.up(cameraSpeed);
                        }
                        break;

                    case KeyEvent.VK_CONTROL:
                        if (rotatingMode && selectedSolidIndex != -1) {
                            // Otoč okolo osy Z
                            rotateSelectedZ(Math.toRadians(-5));
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose Z
                            translateSelected(0, 0, -translationFactor);
                        } else {
                            // Posouvání kamery dolu
                            camera = camera.down(cameraSpeed);
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (rotatingMode) {
                            // Otoč okolo osy Y
                            rotateSelectedY(Math.toRadians(5));
                        } else if (scalingMode && selectedSolidIndex != -1) {
                            //Zvětšujeme vybraný objekt
                            scaleSelected(scaleFactor);
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose Y
                            translateSelected(0, translationFactor, 0);
                        }
                        break;

                    case KeyEvent.VK_DOWN:
                        if (rotatingMode) {
                            // Otoč okolo osy Y
                            rotateSelectedY(Math.toRadians(-5));
                        } else if (scalingMode && selectedSolidIndex != -1) {
                            //Zvětšujeme vybraný objekt
                            scaleSelected(1.0 / scaleFactor);
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose Y
                            translateSelected(0, -translationFactor, 0);
                        }
                        break;
                    // Klávesy pro translaci
                    case KeyEvent.VK_LEFT:
                        if (rotatingMode) {
                            // Otoč okolo osy X
                            rotateSelectedX(Math.toRadians(5));
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose X
                            translateSelected(-translationFactor, 0, 0);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (rotatingMode) {
                            // Otoč okolo osy X
                            rotateSelectedX(Math.toRadians(-5));
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose X
                            translateSelected(translationFactor, 0, 0);
                        }
                        break;
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
    private void updateSolidColors(){
        for (int i = 0; i < solids.size(); i++) {
            Solid solid = solids.get(i);

            if (solid.isAxes()){
                continue;
            }
            solid.setAllColors(i == selectedSolidIndex ? new Color(254, 138, 24) : new Color(0, 0, 0));
        }
    }

    private void selectNextSolid(){
        if (!solids.isEmpty()){
            selectedSolidIndex = (selectedSolidIndex + 1) % solids.size();
            updateSolidColors();
        }
    }

    private void selectPreviousSolid(){
        if (!solids.isEmpty()){
            selectedSolidIndex = (selectedSolidIndex - 1 + solids.size()) % solids.size();
            updateSolidColors();
        }
    }

    private void changeProjection() {
        isPerspective = !isPerspective;
        if (isPerspective){
            proj = new Mat4PerspRH(Math.toRadians(90), (double) panel.getHeight() / panel.getWidth(), 0.01,100);

        }else {
            proj = new Mat4OrthoRH(12,9,0.01,100);
        }
        wiredRanderer.setProj(proj);
    }

    public void renderScene(){
        panel.clear(0xFFFFFF);
        wiredRanderer.setView(camera.getViewMatrix());
        wiredRanderer.renderSolids(solids);
        panel.repaint();

    }
    private void scaleSelected(double scaleFactor) {
        Solid selected = solids.get(selectedSolidIndex);
        Vec3D position = selected.getPosition();
        // Posuneme objekt do středu
        Mat4 translateToOrigin = new Mat4Transl(-position.getX(), -position.getY(), -position.getZ());
        // Aplikujeme změnu velikosti
        Mat4 scale = new Mat4Scale(scaleFactor);
        // Vratíme objekt zpět na původní pozici
        Mat4 translateBack = new Mat4Transl(position.getX(), position.getY(), position.getZ());
        // Nová modelová matice se změnou velikosti
        Mat4 newModelMatrix = translateToOrigin.mul(scale).mul(translateBack).mul(selected.getModel());
        // Nastavíme novou modelovou matici
        selected.setModel(newModelMatrix);
    }
    private void rotateSelectedX(double angle) {
        Solid selected = solids.get(selectedSolidIndex);
        // Rotate around the X-axis
        Mat4 rotation = new Mat4RotX(angle);  // Rotation matrix for the X-axis
        selected.setModel(rotation.mul(selected.getModel()));
    }
    private void rotateSelectedY(double angle) {
        Solid selected = solids.get(selectedSolidIndex);
        // Rotate around the Y-axis
        Mat4 rotation = new Mat4RotY(angle);  // Rotation matrix for the Y-axis
        selected.setModel(rotation.mul(selected.getModel()));
    }
    private void rotateSelectedZ(double angle) {
        Solid selected = solids.get(selectedSolidIndex);
        // Rotate around the Z-axis
        Mat4 rotation = new Mat4RotZ(angle);  // Rotation matrix for the Z-axis
        selected.setModel(rotation.mul(selected.getModel()));
    }

    private void translateSelected(double x, double y, double z) {
        Solid selected = solids.get(selectedSolidIndex);
        // Aplikujeme translaci
        Mat4 translation = new Mat4Transl(x, y, z);
        selected.setModel(selected.getModel().mul(translation));
        selected.setPosition(selected.getPosition().add(new Vec3D(x, y, z)));
    }

}

