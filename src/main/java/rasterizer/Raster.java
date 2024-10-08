package rasterizer;

public interface Raster {

    //setPixel,getPixel, clear, setClearColor, getWidth, getheight

    void Clear();

    void setClearColor(int color);

    int getWidth();

    int getHeight();

    int getPixel(int x, int y);

    void setPixel(int x, int y, int color);


}
