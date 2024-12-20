import java.awt.Color;
import java.awt.image.*;
import javax.swing.*;

/**
 * Handles the data and settings used by the rest of the project
 */
public class Model {
    private Window window;
    private int windowSizeX = 1000;
    private int windowSizeY = 700;

    private Tools currentTool = Tools.PENCIL;
    private float brushSize = 5;
    private Color currentColor = Color.black;
    private int toolCount = 8;
    private int gridsize = 27;

    private BufferedImage selectedImage;
    private SelectPanel selectPanel;

    public Model(Window w) {
        this.window = w;
    }

    public int getWindowSizeX() {
        return this.windowSizeX;
    }
    
    public int getWindowSizeY() {
        return this.windowSizeY;
    }

    public Tools getCurrentTool() {
        return currentTool;
    }

    public float getBrushSize() {
        return brushSize;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public int getToolCount() {
        return toolCount;
    }

    public BufferedImage getSelectedImage() {
        return selectedImage;
    }

    public void setCurrentTool(Tools tool) {
        this.currentTool = tool;
    }

    public void setBrushSize(float brushSize) {
        this.brushSize = brushSize;
        window.updateBrushSize(brushSize);
    }

    public void setCurrentColor(Color c) {
        this.currentColor = c;
    }

    public void setSelectedImage(BufferedImage img) {
        this.selectedImage = img;
    }

    public int getGridSize() {
        return this.gridsize;
    }

    public void setSelectPanel(SelectPanel s) {
        this.selectPanel = s;
    }

    public SelectPanel getSelectPanel() {
        return selectPanel;
    }
}
