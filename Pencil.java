import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Pencil {
    private Model model;
    private Graphics2D g;
    private BufferedImage image;
    private DrawingCanvas parent;

    public Pencil(Model m, Graphics2D g, BufferedImage image, DrawingCanvas parent) {
        this.model = m;
        this.g = g;
        this.image = image;
        this.parent = parent;
    }

    public void setup() {
        model.setCurrentTool(Tools.PENCIL);
        try {
            g.setPaint(model.getCurrentColor());
        } catch (Exception e) {
            if (image == null || g == null) {
                // image which we draw, creating if null
                image = new BufferedImage(parent.getWidth() + 1, parent.getHeight() + 1, BufferedImage.TYPE_INT_RGB);
                g = (Graphics2D) image.getGraphics();
                g.setPaint(model.getCurrentColor());
            }
            g.setPaint(model.getCurrentColor());
            parent.repaint();
        }
    }
}