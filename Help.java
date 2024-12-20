import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
* Help page for users to understand various tools and give the hotkeys that the tools can use
*/
public class Help{

    private JFrame frame = new JFrame("Help Page");
    private JPanel panel = new JPanel();
    private JLabel toolsLabel = new JLabel("<html>Tools:<br> A tool must be clicked before the hotkeys work to prevent mistyping</html>");
    private JLabel penciltool = new JLabel("Pencil Tool - Hotkey(1)");
    private JLabel pencildescription = new JLabel("  - Allows you to draw, change colour and is resizable");
    private JLabel erasertool = new JLabel("Eraser Tool - Hotkey(2)");
    private JLabel eraserdescription = new JLabel("  - Erase drawings");
    private JLabel filltool = new JLabel("Fill Tool - Hotkey(3)");
    private JLabel filldescription = new JLabel("  - Fill any shapes drawn or the full canvas");
    private JLabel texttool = new JLabel("Text Tool - Hotkey(4)");
    private JLabel textdescription = new JLabel("  - Allows you to write text and choose where to place it");
    private JLabel shapestool = new JLabel("Shapes Tool - Hotkey(5)");
    private JLabel shapesdescription = new JLabel("  - A dropdown of various shapes that are drawable");
    private JLabel eyedroppertool = new JLabel("Eye Dropper - Hotkey(6)");
    private JLabel eyedropperdescription = new JLabel("  - Get the RGB colour of the element clicked on");
    private JLabel selectTool = new JLabel("<html>Select Tool - Hotkey(7)<br>" +
    "  - Select an area which operations can be performed on</html>");
    private JLabel sprayTool = new JLabel("<html>Spray Tool - Hotkey(7)<br>" +
            "  - Draw in a more rough and transparent way</html>");
    private JLabel hotkeys = new JLabel("Other Hotkeys:");
    private JLabel hotkeysdescription = new JLabel("Paste - V");
    private JLabel information = new JLabel("<html><br>File:<br>- Save layer - Saves the current layer as an image<br>  " + 
    "  - Import image - Imports the image into the current layer<br>  " +
    "  - Export final image - Exports all of the layers combined into one image<br>  " + 
    "Edit:<br>  - Undo - Removes the last thing drawn<br>" +
    "  - Redo - Draws the last thing that was undone back to the screen<br>" + 
    "Select:<br>  - Has various tools for manipulation of the currently selected area from the select tool<br>" +
    "View:<br>  - Clear all - Clears the current layer<br>" +
    "  - Layers overlay - Shows what the final image with all layers combined will look like<br>" +
    "  - Dark mode - Toggles whether dark mode on and off<br>" + 
    "  - Various tools for manipulating the whole canvas</html>");

    public Help(){
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(toolsLabel);
        panel.add(penciltool);
        panel.add(pencildescription);
        panel.add(erasertool);
        panel.add(eraserdescription);
        panel.add(filltool);
        panel.add(filldescription);
        panel.add(texttool);
        panel.add(textdescription);
        panel.add(shapestool);
        panel.add(shapesdescription);
        panel.add(eyedroppertool);
        panel.add(eyedropperdescription);
        panel.add(selectTool);
        panel.add(sprayTool);
        panel.add(hotkeys);
        panel.add(hotkeysdescription);
        panel.add(information);

        frame.setContentPane(panel);
        frame.setSize(600,700);
        frame.setVisible(true);
    }
    
}
