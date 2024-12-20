import java.awt.Graphics;
import javax.swing.*;

/*
 * Shows a little box so that the user can see which color they have chosen
 */
public class RectDraw extends JPanel {
    private Model model;
    public RectDraw(Model model){
        this.model = model;
    }
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);  
      g.drawRect(0,0,30,30);  
      g.setColor(model.getCurrentColor());
      g.fillRect(0,0,30,30);  
      repaint();
    }
}