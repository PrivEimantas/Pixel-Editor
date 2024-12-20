import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * The driver class - responsible for starting the application and switching between 
 * light and dark mode
 */
public class Driver {

    public static void main(String[] args) {

        System.setProperty("flatlaf.menuBarEmbedded", "false");
        try {
            UIManager.setLookAndFeel(new FlatLightLaf()); // sets default colour as flatlightlaf
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        Window w = new Window(); //creates a new window
        w.darkBtn.addActionListener(new ActionListener() { //listens for the darkbutton in window class
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == w.darkBtn){
                    try {
                        if(w.getdarkmode() == false){
                            UIManager.setLookAndFeel( new FlatDarkLaf() ); // if current darkmode is false it is set to true and flatdarklaf is set
                            w.setdarkmode(true);
                        } else {
                            UIManager.setLookAndFeel( new FlatLightLaf() ); // else keep it to flatlightlaf and set darkmode to false
                            w.setdarkmode(false);
                        }
                    }
                    catch( Exception ex ) {
                        System.err.println( "Failed to initialize LaF" );
                    }
                }
                SwingUtilities.updateComponentTreeUI(w); // updates the UI based on darkmode changes
            }
        });
    }
}