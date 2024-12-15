package controller;

import rasterizer.LineRasterizer;
import rasterizer.LineRasterizerGraphics;
import rasterizer.Raster;
import renderer.WiredRanderer;
import solids.*;
import view.Panel;
import transforms.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller3D implements Controller{
    private final Panel panel;
    private Raster raster;

    //Renderery
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

    //časovač pro animace
    private Timer animationTimer;

    //úhly kamery
    private double angleX = 1;
    private double angleY = 0;
    private double radius = 5;

    //otáčení myší
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
    private boolean isAnimating = false;
    private int animationFrame = 0;



    public Controller3D(Panel panel) {
        this.panel = panel;
        this.raster = panel.getRasterBufferedImage();

        //projekční matice pro perspektivní zobrazení
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

        //časovač pro animace
        animationTimer = new Timer(35, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animateCube();
                renderScene();
                animationFrame++;
            }
        });

        InitObjects();
        InitListeners();
        renderScene();

    }


    @Override
    public void InitObjects() {
        initCamera();

        //vytvoření objektů
        axes = new Axes();
        cube1 = new Cube();
        cube2 = new Cube();
        tetrahedron = new Tetrahedron();
        pyramid = new Pyramid();
        solids.clear();

        //transformace na objekty
        Mat4 scale = new Mat4Scale(0.4);
        //posunut nahoru
        Mat4 transl = new Mat4Transl(0,0,1.4);
        cube2.setModel(scale.mul(transl));
        tetrahedron.setModel(new Mat4Transl(-2.5,0,0));
        pyramid.setModel(new Mat4Transl(2.5,0,0));

        //Křivky
        Solid cubicbezier = new Cubic3D("BEZIER");
        cubicbezier.setModel(new Mat4Transl(0,0,0));
        Solid cubicferguson = new Cubic3D("FERGUSON");
        cubicferguson.setModel(new Mat4Transl(0,0,0));
        Solid cubiccoons = new Cubic3D("COONS");
        cubiccoons.setModel(new Mat4Transl(0,0,0));
        Solid parametricCurve = new ParametricCurve();
        parametricCurve.setModel(new Mat4Transl(0,0,0));

        //přidání do seznamu
        solids.add(cube1);
        solids.add(cube2);
        solids.add(tetrahedron);
        solids.add(pyramid);
        solids.add(cubicbezier);
        solids.add(cubicferguson);
        solids.add(cubiccoons);
        solids.add(parametricCurve);
        solids.add(axes);

    }
    //inicializace kamery
    private void initCamera() {
        angleX = 1;
        angleY = 0;
        radius = 5;
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
                //kontrola stisknutých kláves
                switch (e.getKeyCode()){
                    //pohyb kamery
                    case KeyEvent.VK_W: camera = camera.forward(cameraSpeed); break;
                    case KeyEvent.VK_S: camera = camera.backward(cameraSpeed); break;
                    case KeyEvent.VK_A: camera = camera.left(cameraSpeed); break;
                    case KeyEvent.VK_D: camera = camera.right(cameraSpeed); break;
                    //změna projekce, výběr, reset kamery, spuštění animace
                    case KeyEvent.VK_P: changeProjection(); break;
                    case KeyEvent.VK_R: selectNextSolid(); break;
                    case KeyEvent.VK_T: selectPreviousSolid(); break;
                    case KeyEvent.VK_C: initCamera(); break;
                    case KeyEvent.VK_N: toggleAnimation(); break;

                    //výběr režimů
                    case KeyEvent.VK_M: //pohyb
                        movingMode = !movingMode;
                        scalingMode = false;
                        rotatingMode = false;
                        break;
                    case KeyEvent.VK_V: //škálování
                        scalingMode = !scalingMode;
                        movingMode = false;
                        rotatingMode = false;
                        break;
                    case KeyEvent.VK_B: //otáčení
                        rotatingMode = !rotatingMode;
                        scalingMode = false;
                        movingMode = false;
                        break;

                    case KeyEvent.VK_SHIFT:
                        if (rotatingMode && selectedSolidIndex != -1) {
                            // Otoč okolo osy Z
                            rotateSelectedZ(Math.toRadians(5));
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu nahoru po ose Z
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
                            // Posouvání objektu dolů po ose Z
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
                            translateSelected(0, -translationFactor, 0);
                        }
                        break;

                    case KeyEvent.VK_DOWN:
                        if (rotatingMode) {
                            // Otoč okolo osy Y
                            rotateSelectedY(Math.toRadians(-5));
                        } else if (scalingMode && selectedSolidIndex != -1) {
                            //Zmenšujeme vybraný objekt
                            scaleSelected(1.0 / scaleFactor);
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose Y
                            translateSelected(0, translationFactor, 0);
                        }
                        break;

                    // Klávesy pro translaci
                    case KeyEvent.VK_LEFT:
                        if (rotatingMode) {
                            // Otoč okolo osy X
                            rotateSelectedX(Math.toRadians(5));
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose X
                            translateSelected(translationFactor, 0, 0);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (rotatingMode) {
                            // Otoč okolo osy X
                            rotateSelectedX(Math.toRadians(-5));
                        } else if (movingMode && selectedSolidIndex != -1) {
                            // Posouvání objektu po ose X
                            translateSelected(-translationFactor, 0, 0);
                        }
                        break;
                }
                renderScene(); //znovu vykresli scénu
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    isDragging = true;
                    //uložení počáteční pozice myši x,y
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1){
                    isDragging = false; //zastavení tahání myši
                }
            }
        });

        //otáčení kamery při tažení myši
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int dx = e.getX() - lastMouseX; //Pohyb myši horizontálně
                    int dy = e.getY() - lastMouseY; //Pohyb vertikálně

                    // uprava úhlů kamery na základě pohybu myši
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
            int rotation = e.getWheelRotation(); //směr otáčení kolečka +1 nebo -1
            camera = camera.addRadius(rotation * cameraSpeed * 0.5); //zoom in nebo out
            renderScene();
        });
    }

    //start stop animace
    private void toggleAnimation() {
        if(isAnimating){
            animationTimer.stop();
        }else {
            animationTimer.start();//po stisku N zapni animaci
        }
        isAnimating = !isAnimating;
    }
    //Pohyb kostky pro animaci
    private void animateCube() {
        if (cube2 == null)return; //pokud existuje
        double movement = Math.sin(animationFrame * 0.06) * 0.2; //sinusové funkce pro oscilaci kostky
        Mat4 translation = new Mat4Transl(movement,movement,0); //pohyb v ose x a y
        cube2.setModel(cube2.getModel().mul(translation));
    }

    //změna barvy tělesa na základě výběru
    private void updateSolidColors(){
        for (int i = 0; i < solids.size(); i++) {
            Solid solid = solids.get(i);

            if (solid.isAxes()){
                continue; //osy neřešíme
            }
            //změna barvy pokud je vybraný
            solid.setAllColors(i == selectedSolidIndex ? new Color(254, 138, 24) : new Color(0, 0, 0));
        }
    }

    //výběr dalšho objektu
    private void selectNextSolid(){
        if (!solids.isEmpty()){
            selectedSolidIndex = (selectedSolidIndex + 1) % solids.size();
            updateSolidColors();
        }
    }

    //výběr předchozího
    private void selectPreviousSolid(){
        if (!solids.isEmpty()){
            selectedSolidIndex = (selectedSolidIndex - 1 + solids.size()) % solids.size();
            updateSolidColors();
        }
    }

    //přepnutí mezi projekcemi
    private void changeProjection() {
        isPerspective = !isPerspective;
        if (isPerspective){
            //Perspektivní
            proj = new Mat4PerspRH(Math.toRadians(90), (double) panel.getHeight() / panel.getWidth(), 0.01,100);

        }else {
            //Ortogonální
            proj = new Mat4OrthoRH(12,9,0.01,100);
        }
        wiredRanderer.setProj(proj);
    }

    public void renderScene(){
        panel.clear(0xFFFFFF);
        wiredRanderer.setView(camera.getViewMatrix());//nastavení pohledu kamery na scénu
        wiredRanderer.renderSolids(solids);//vykreslení těles ze seznamu
        panel.repaint();
    }
    private void scaleSelected(double scaleFactor) {
        Solid selected = solids.get(selectedSolidIndex); //získání vybraného tělesa
        Vec3D position = selected.getPosition(); //získání pozice objektu
        // Posuneme objekt do středu
        Mat4 translateToOrigin = new Mat4Transl(-position.getX(), -position.getY(), -position.getZ());
        Mat4 scale = new Mat4Scale(scaleFactor); //Změna velikosti
        // Vratíme objekt zpět na původní pozici
        Mat4 translateBack = new Mat4Transl(position.getX(), position.getY(), position.getZ());
        // Nová modelová matice se změnou velikosti
        Mat4 newModelMatrix = translateToOrigin.mul(scale).mul(translateBack).mul(selected.getModel());
        // Nastavíme novou modelovou matici
        selected.setModel(newModelMatrix);
    }
    private void rotateSelected(double angle, Vec3D axis) {
        Solid selected = solids.get(selectedSolidIndex); //získání vybraného objektu
        //Otáčej okolo dané osy
        Mat4 rotation = new Mat4Rot(angle,axis);
        selected.setModel(rotation.mul(selected.getModel()));
    }
    private void rotateSelectedX(double angle) {
        rotateSelected(angle,new Vec3D(1,0,0));// osa X
    }
    private void rotateSelectedY(double angle) {
        rotateSelected(angle,new Vec3D(0,1,0));// osa Y
    }
    private void rotateSelectedZ(double angle) {
        rotateSelected(angle,new Vec3D(0,0,1));// osa Z
    }

    private void translateSelected(double x, double y, double z) {
        Solid selected = solids.get(selectedSolidIndex); //získání vybraného objektu
        Mat4 translation = new Mat4Transl(x, y, z);//vytvoření matice pro pohyb v osách
        selected.setModel(selected.getModel().mul(translation)); // Aplikujeme translaci
        selected.setPosition(selected.getPosition().add(new Vec3D(x, y, z))); //aktualizace pozice tělesa
    }
}