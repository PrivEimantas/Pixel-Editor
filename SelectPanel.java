import java.awt.Color;
import java.awt.Graphics;
import javax.swing.*;

/**
 * Handles drawing the selection box on a separate layer
 */
public class SelectPanel extends JPanel {
    private boolean drawRect = false;
    private int selectXstart;
    private int selectYstart;
    private int selectXnew;
    private int selectYnew;

    public void paintComponent(Graphics g) {
        super.paintChildren(g);
        if (drawRect) {
            g.setColor(new Color(67, 84, 90, 100));
            g.fillRect(selectXstart, selectYstart, selectXnew - selectXstart, selectYnew - selectYstart);
            drawRect = false;
        }
    }

    /**
     * Sets the points where the box will be drawn
     */
    public void setPoints(int selectXstart, int selectYstart, int selectXnew, int selectYnew) {
        this.selectXstart = selectXstart;
        this.selectYstart = selectYstart;
        this.selectXnew = selectXnew;
        this.selectYnew = selectYnew;
    }

    /**
     * Toggles the drawing box
     */
    public void toggleDraw() {
        drawRect = !drawRect;
    }
}
