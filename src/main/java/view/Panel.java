package view;
import rasterizer.RasterBufferedImage;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {

    private RasterBufferedImage rasterBufferedImage;



    private static final int WIDTH = 800, HEIGHT = 600;

    public Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        //Aby panel mohl přijímat klávesové události
        setFocusable(true);
        requestFocusInWindow();

        rasterBufferedImage = new RasterBufferedImage(WIDTH, HEIGHT);
        rasterBufferedImage.setClearColor(Color.BLACK.getRGB());

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        rasterBufferedImage.repaint(g);

        //nápověda
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN,14));
        String help = "Pro smazání stiskni [C]  Pro kreslení přímku [L]  Pro kreslení polygonu [P]";
        String modifiedhelp = help.replace("  ", "  " + " ".repeat(15));
        g.drawString(modifiedhelp, 10, getHeight()-10);

    }

    public void clear(int color){
        rasterBufferedImage.setClearColor(color);
        rasterBufferedImage.clear();
    }

    public RasterBufferedImage getRasterBufferedImage(){
        return rasterBufferedImage;
    }

}
