package solids;

import transforms.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solid {
    protected List<Point3D> vb = new ArrayList<>();
    protected List<Integer> ib = new ArrayList<>();
    protected Col color = new Col(0xffffff);

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

}
