package solids;

import transforms.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solid {
    protected List<Point3D> vb = new ArrayList<>();
    protected List<Integer> ib = new ArrayList<>();
    protected Col color = new Col(0xffffff);
    protected List<Color> colors = new ArrayList<>();
    protected Mat4 model;

    //list pro barvy
    //boolean isactive po kliknuti na teleso se z nej stane oznacene a zmeni barvu

    protected void addIndices(Integer...indices)
    {
        ib.addAll(Arrays.asList(indices));
    }

    public List<Point3D> getVb() {
        return vb;
    }

    public List<Integer> getIb() {
        return ib;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }
}
