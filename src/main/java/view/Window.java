package view;

import javax.swing.*;

public class Window extends JFrame {

    private final Panel panel;

    //konstruktor
    public Window() {
        panel = new Panel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Canvas");
        setResizable(false);
        add(panel);
        setVisible(true);
        pack();
        panel.setFocusable(true);
        panel.grabFocus();
    }

    public Panel getPanel() {
        return panel;
    }
}
