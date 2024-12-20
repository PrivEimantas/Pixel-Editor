import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import javax.swing.BoxLayout;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * The menu used for selecting the brush size
 */
public class BrushSize extends JComponent implements ActionListener{
    private JFrame frame;
    private JPanel panel;
    private JTextField brushSizeInput;
    private Model model;
    private JSlider sizeSlider;
    private JLabel label;
    private JButton submitBtn;
    private int maxSize = 50;

    public BrushSize(Model m) {
        this.model = m;
        
        //Setup the window
        frame = new JFrame("Change Brush Size");
        frame.setLocationRelativeTo(null);
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        brushSizeInput = new JTextField(Float.toString(model.getBrushSize()),10);
        JPanel inputPanel = new JPanel();
        inputPanel.setPreferredSize(new Dimension(50, 20));
        inputPanel.add(brushSizeInput);
        sizeSlider = new JSlider(0, maxSize);
        sizeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        sizeSlider.addChangeListener(e -> changedSize(e));
        sizeSlider.setMajorTickSpacing(10);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        label = new JLabel(" ");
        label.setForeground(Color.RED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> buttonPressed());
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(inputPanel);
        panel.add(sizeSlider);
        panel.add(label);
        panel.add(submitBtn);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Called when the submit button is pressed
     */
    public void buttonPressed() {
        float size;
        try {
            size = Float.parseFloat(brushSizeInput.getText());
        } catch(Exception e) {
            label.setText("Please enter a valid decimal number");
            return;
        }
        
        if (size > 0 && size <= maxSize) {
            model.setBrushSize(size);
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
        else {
            label.setText("Please enter a value between 0 and " + maxSize);
        }
    }

    /**
     * Called when the brush size slider is changed
     */
    public void changedSize(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        brushSizeInput.setText(Integer.toString(source.getValue()));
    }

    public void actionPerformed(ActionEvent e){
    }
}
