import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.*;

/*
 * Handles the ui for the shape chooser and then sets the tool once clicked
 */
public class Shapes implements ActionListener {
    private Model model;
    private JFrame frame = new JFrame();
    private JButton recButton;
    private JButton circButton;
    private JButton triButton;
    public boolean rect = false;
    private boolean circ = false;
    private int i = 0;

    // Adds button to the window to allow the user to change between different shapes
    public Shapes(Model model){
        this.model = model;

        frame = new JFrame("Choose shape");
        frame.setLayout(new GridLayout(3,1));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 300);

        recButton = new JButton("Rectangle");
        circButton = new JButton("Circle");
        triButton = new JButton("Triangle");
        frame.add(recButton);
        frame.add(circButton);
        frame.add(triButton);
        
        frame.setVisible(true);

        recButton.addActionListener(this);
        triButton.addActionListener(this);
        circButton.addActionListener(this);
    }
    
    public boolean drawRect(){
        return rect;
    }
    public boolean falseRect(){
        rect = false;
        return rect;
    }
    public void drawCirc(){
        frame.dispose();
    }
    //Listens to which button the user clicks and reacts accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == recButton){
            model.setCurrentTool(Tools.RECTANGLE);
            frame.dispose();    
        }
        else if(e.getSource() == circButton){
            model.setCurrentTool(Tools.CIRCLE);
            frame.dispose();
        }
        else if(e.getSource() == triButton){
            model.setCurrentTool(Tools.TRIANGLE);
            frame.dispose();
        }
        
    }
}
