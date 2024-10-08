package view;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private static final int WIDTH = 800, HEIGHT = 600;

    public Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // rastr
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void clear(int color){

    }
}
