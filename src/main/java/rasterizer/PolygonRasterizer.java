package rasterizer;

import model.Line;
import model.Point;
import model.Polygon;

public class PolygonRasterizer {
    
    private LineRasterizer lineRasterizer;
    private int thickness;
    
    public PolygonRasterizer(LineRasterizer lineRasterizer, int thickness) {
        this.lineRasterizer = lineRasterizer;
        this.thickness = thickness;
    }



    //Vykresli polygon pokud ma vic jak 3 vrcholy
    public void rasterize(Polygon polygon) {
        if (polygon.getSize() < 3){
            return;
        }else {
            /*cyklus, který projde body
            * {
            * nactu indexA = i
            * nactu indexB = i + 1
            * pokud indexB == size() - budeme nacitat tak dlouho dokud index b nebude rovna velikosti
            * indexB bude prvnim bodem
            * nactu objekt Point A
            * nactu objekt Point B
            * vykreslim je pres LineRasterizer
            * }
            * */

            for (int i = 0; i <  polygon.getSize(); i++) {
                int indexA = i;
                int indexB = i+1;

                if(indexB == polygon.getSize()){
                    indexB= 0;
                }
                Point A = polygon.getPoint(indexA);
                Point B = polygon.getPoint(indexB);
                lineRasterizer.rasterize(new Line(A,B, thickness));
            }

        }

    }
    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }
    public void setThickness(int thickness){
        this.thickness = thickness;
    }
    
}
