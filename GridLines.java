import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;

/**
 * Handles drawing the grid lines based on the grid size
 */
public class GridLines extends JPanel {

    private boolean showGrid = false;
    private boolean cleared = true;
    private int gridsize;
    private Model model;

    public GridLines(Model m) {
        this.model = m;
        this.gridsize = model.getGridSize();
    }

    public void toggleGridlines() {
        showGrid = !showGrid;
        if (showGrid == false)
            cleared = false;
        
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintChildren(g);
        if (showGrid) {
            g.setColor(Color.BLACK);
            for (int i = 0; i < getWidth(); i += gridsize) {
                g.drawLine(i, 0, i, getHeight());
            }
            for (int i = 0; i < getHeight(); i += gridsize) {
                g.drawLine(0, i, getWidth(), i);
            }
        }
        else if (!cleared){
            g.clearRect(0, 0, 850, 567);
            repaint();
            cleared = true;
        }
    }
}
