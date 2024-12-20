import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.*;

/*
 * Left side of screen contains buttons, creation of such buttons done here
 */
public class ToolMenu extends JPanel {

    private Window window;
    private int toolCount;

    private JButton[] buttons;
    /*
    0 = pencil
    1 = eraser
    2 = fill
    3 = text
    4 = shape
    5 = eye dropper
    6 = select
    7 = sprayPaint 
    */

    /**
     * Constructor - sets the number of buttons and the window in which to make them
     * @param w the window in which the buttons should be displayed
     * @param toolCount the number of buttons to be created
     */
    public ToolMenu(Window w, int toolCount)
    {
        this.window = w;
        this.toolCount = toolCount;
        buttons = new JButton[toolCount];
        createToolMenu();
    }
    /**
     * A method used to create all the buttons in the toolMenu according to the constructor parameters
     */
    public void createToolMenu(){ //method builds toolmenu

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS); //Create grid layout and set it
        this.setLayout(layout);

        String[] fileNames = {
                "pencil.png",
                "eraser.png",
                "fill.png",
                "text.png",
                "shapes.png",
                "dropper.png",
                "select.png",
                "sprayPaint.png"
        };        
    
        //Create and setup buttons

        String[] tooltips = {"Pencil", "Eraser", "Fill with colour", "Text", "Draw Shapes", "EyeDropper", "Select tool", "Spray Paint"};

        for(int i = 0; i < toolCount; i++){
            try{
                Icon icon = new ImageIcon(fileNames[i]);
                buttons[i] = new JButton(icon);
                buttons[i].setBackground(Color.WHITE);
                buttons[i].addActionListener(window);
                buttons[i].addKeyListener(window);
                Image img = ImageIO.read(window.getClass().getResource("./images/" + fileNames[i]));
                buttons[i].setIcon(new ImageIcon(img.getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
                buttons[i].setMaximumSize(new Dimension(75,75));
                this.add(buttons[i]);
            }
            catch(Exception ex){
                System.out.println(ex);
            }

            buttons[i].setToolTipText(tooltips[i]);
        }
    }
    
    public JButton[] getButtons() {
        return buttons;
    }

    public void resetButtons() {
        for (int i = 0; i < toolCount; i++) {
            buttons[i].setBackground(Color.WHITE);
        }
    }

    public void selectButton(int buttonNum) {
        resetButtons();
        buttons[buttonNum].setBackground(new Color(201, 237, 245));
    }
}