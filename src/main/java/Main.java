import controller.Controller3D;
import view.Window;
import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Window window = new Window();
                Controller3D controller3D = new Controller3D(window.getPanel());
                controller3D.InitListeners();
                controller3D.InitObjects();
                controller3D.RanderScene();
            }
        });


    }
}