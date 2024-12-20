import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * Class used for layer juxtaposition. Depending on the arg the constructor is passed,
 * it either makes a JFrame which contains the overall image, or just exports the image as final.png
 */
public class LayersDialog extends JComponent {

    private JFrame frame; //the final
    private JPanel allPanel;
    BufferedImage finalImage;
    Graphics2D g2d;
    JLabel label;

    /**
     * Constructor
     * @param layers the number of layers to overlay
     * @param model the Model object associated with the window
     * @param allLayers a DrawingCanvas array which contains every layer
     * @param operation the operation code which dictates whether a new Jframe is made or the image is exported;
     * 1 = make a new JFrame, rest is export
     */
    public LayersDialog(int layers, Model model, DrawingCanvas[] allLayers, int operation) {

        frame = new JFrame("Layers Overlay");
        allPanel = new JPanel(new BorderLayout());

        finalImage = new BufferedImage(allLayers[1].getSize().width, allLayers[1].getSize().height,
                BufferedImage.TYPE_INT_ARGB);
        g2d = finalImage.createGraphics();
        // g2d.setPaint(new Color(255, 255, 255, 127 * 2));
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, allLayers[1].getSize().width, allLayers[1].getSize().height);
        for (int i = 1; i <= 5; i++) {
            BufferedImage temp = new BufferedImage(allLayers[i].getSize().width, allLayers[i].getSize().height,
                    BufferedImage.TYPE_INT_ARGB);
            temp = allLayers[i].getCanvasImage();
            g2d.drawImage(temp, 0, 0, null);
        }
        repaint();
        label = new JLabel(new ImageIcon(finalImage));
        allPanel.add(label, BorderLayout.CENTER);
        frame.setContentPane(allPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(finalImage.getWidth() + 50, finalImage.getHeight() + 50);
        // frame.setVisible(true);
        if(operation==1)
        {
            frame.dispose();
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("*.png, *.jpg, *.bmp, *.gif", "png", "jpg", "bmp", "gif"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    FileNameExtensionFilter fileNameExtensionFilter = (FileNameExtensionFilter) fc.getFileFilter();
                    if (!fileNameExtensionFilter.accept(file)) { // Checks if the filter accepts the extension entered
                        String extension = fileNameExtensionFilter.getExtensions()[0]; // Defaults to the first
                                                                                       // extension (png)
                        String filename = file.getName() + "." + extension; // Appends extension to filename
                        file = new File(file.getParent(), filename);
                    }
                    ImageIO.write(finalImage, "PNG", file);

                } catch (IOException ex) {
                    System.out.println("Failed to save image!");
                }
            }
        }
        else{
            frame.setVisible(true);
        }
    }

    /**
     * Overridden method from JComponent, needed for drawing
     * @param g a Graphics2D component associated with the image the program draws on
     */
    public void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        g.drawImage(finalImage, 0, 0, null);
    }

}
