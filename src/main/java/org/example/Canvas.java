package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Canvas {

    private JFrame frame;
    private JPanel panel;//pro pridani komponent na okno
    private BufferedImage img;

    public Canvas(int width, int height) {
        frame = new JFrame();
        frame.setLayout(new BorderLayout());

        frame.setTitle("Canvas");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };

        panel.setPreferredSize(new Dimension(width, height));
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    public void present(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }

    public void clear() {
        Graphics g = img.getGraphics();
        g.setColor(new Color(0x000000));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
    }

    public void draw() {
        clear();
        for (int i = 10; i <= 100; i++) {
            img.setRGB(i,55, 0xFFFFFF);
            img.setRGB(55,i, 0xFFFFFF);
            img.setRGB(i,i, 0xFFFFFF);
            img.setRGB(i, 110 - i, 0xFFFFFF);
        }
        trivialni(400, 400, 50, 50);
    }

    public void start() {
        draw();
        panel.repaint();
    }


    private void trivialni(int x1, int y1, int x2, int y2) {

        if (x1>x2){
            int tmp = x1;
            x1 = x2;
            x2 = tmp;
        }

        float k = (float) (y2 - y1) / (float) (x2 - x1);
        float q = (float) y1 - k * x1;

        for (int x = x1; x <= x2; x++) {
            int y = Math.round(k * x + q);
            img.setRGB(x, y, 0xFFFFFF);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Canvas(800, 600).start();
            }
        });
    }
}