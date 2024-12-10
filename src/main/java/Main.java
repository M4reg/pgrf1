import controller.Controller3D;
import view.Window;
import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Window window = new Window();
                new Controller3D(window.getPanel());
            }
        });


    }
}