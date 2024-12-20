import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
* Window class - this is the main application window where everything
* is displayed and stored as a reference
*/
public class Window extends JFrame implements ActionListener,KeyListener {
    private ToolMenu toolMenu;
    private JButton submitBrushSize;
    private JButton colorPicker;
    private JCheckBox gridlinesbtn;

    private JMenuItem saveBtn;
    private JMenuItem importBtn;
    public static JMenuItem darkBtn;
    private JMenuItem undoBtn;
    private JMenuItem redoBtn;
    private JMenuItem copyBtn;
    private JMenuItem pasteBtn;
    private JMenuItem selectBtn;

    public Color color;
    private Model model;
    //these are the layers to show
    private DrawingCanvas[] drawingArea=new DrawingCanvas[6];
    private JTabbedPane tabbedPane=new JTabbedPane();
    private JPanel[] tabs=new JPanel[6];
    private int noOfLayers;
    private int currLayer;
    private boolean darkmode;

    private GridLines gridPanel;

    /**
     * Constructor; this is responsible for making the actual window and adding the UI to it
     */
    public Window()
    {
        super("Pixel Editor");
        model = new Model(this);
        this.setSize(model.getWindowSizeX(), model.getWindowSizeY());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.addKeyListener(this);
        
        currLayer=1;
        Container content = this.getContentPane();
        BorderLayout layout = new BorderLayout();
        content.setLayout(layout);

        //Menu bar
        FlowLayout menuLayout = new FlowLayout(FlowLayout.LEFT, 5, 2);
        JMenuBar menu_bar = new JMenuBar();
        menu_bar.setLayout(menuLayout);
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu selectMenu = new JMenu("Select");
        JMenu viewMenu = new JMenu("View");
        JButton helpBtn = new JButton("Help");
        helpBtn.addActionListener(e -> new Help());
        
        submitBrushSize = new JButton("Brush size: " + model.getBrushSize());
        submitBrushSize.addActionListener(this);
        JPanel brushSizePnl = new JPanel();
        brushSizePnl.add(submitBrushSize);
        brushSizePnl.setPreferredSize(new Dimension(110, 20));
        
        colorPicker = new JButton("Choose a colour: ");
        colorPicker.addActionListener(this);
        RectDraw newRect = new RectDraw(model);
        JPanel rectPanel = new JPanel(new BorderLayout());
        rectPanel.setPreferredSize(new Dimension(20, 20));
        rectPanel.add(newRect, BorderLayout.CENTER);
        gridlinesbtn = new JCheckBox("Gridlines");
        gridlinesbtn.addActionListener(this);
        
        menu_bar.add(fileMenu);
        menu_bar.add(editMenu);
        menu_bar.add(selectMenu);
        menu_bar.add(viewMenu);
        menu_bar.add(helpBtn);
        menu_bar.add(Box.createHorizontalStrut(50));
        menu_bar.add(submitBrushSize);
        menu_bar.add(colorPicker); // Adds the clor picker button to the menubar
        menu_bar.add(rectPanel); //Adds a color changing reactangle to the menu bar
        menu_bar.add(Box.createHorizontalStrut(20));
        menu_bar.add(gridlinesbtn);
        this.setJMenuBar(menu_bar);

        //File
        JMenuItem exportFinal=new JMenuItem("Export final image");
        exportFinal.addActionListener(e -> new LayersDialog(noOfLayers, model, drawingArea, 1));
        saveBtn = new JMenuItem("Save layer");
        saveBtn.addActionListener(e -> saveBtnPressed());
        importBtn = new JMenuItem("Import image");
        importBtn.addActionListener(e -> importBtnPressed());
        fileMenu.add(saveBtn);
        fileMenu.add(importBtn);
        fileMenu.add(exportFinal);

        //Edit
        undoBtn = new JMenuItem("Undo");
        undoBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].Undo());
        redoBtn = new JMenuItem("Redo");
        redoBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].Redo());
        editMenu.add(undoBtn);
        editMenu.add(redoBtn);

        //Select
        JMenuItem selectFlipHorBtn = new JMenuItem("Mirror selection horizontal");
        selectFlipHorBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].flipImageHorizontal());
        JMenuItem selectFlipVerBtn = new JMenuItem("Mirror selection vertical");
        selectFlipVerBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].flipImageVertical());
        JMenuItem selectRotateBtn = new JMenuItem("Rotate selection 90 clockwise");
        selectRotateBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].rotateImage(90));
        copyBtn = new JMenuItem("Copy");
        pasteBtn = new JMenuItem("Paste");
        pasteBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].Paste());
        selectMenu.add(selectFlipHorBtn);
        selectMenu.add(selectFlipVerBtn);
        selectMenu.add(selectRotateBtn);
        selectMenu.add(copyBtn);
        selectMenu.add(pasteBtn);

        //View
        JMenuItem clearBtn = new JMenuItem("Clear all");
        clearBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].setWhite());
        this.noOfLayers=5;
        JMenuItem layerBtn = new JMenuItem("Layers overlay");
        layerBtn.addActionListener(e -> new LayersDialog(noOfLayers,model,drawingArea,0));
        JMenuItem flipHorBtn = new JMenuItem("Mirror canvas horizontal");
        JMenuItem flipVerBtn = new JMenuItem("Mirror canvas vertical");
        JMenuItem rotateBtn = new JMenuItem("Rotate canvas 90 clockwise");
        darkBtn = new JMenuItem("Dark Mode");
        darkBtn.addActionListener(this);
        viewMenu.add(clearBtn);
        viewMenu.add(layerBtn);
        viewMenu.add(darkBtn);
        viewMenu.add(flipHorBtn);
        flipHorBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].flipCanvasHorizontal());
        viewMenu.add(flipVerBtn);
        flipVerBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].flipCanvasVertical());
        viewMenu.add(rotateBtn);
        rotateBtn.addActionListener(e -> drawingArea[tabbedPane.getSelectedIndex()+1].rotateCanvas(90));
        

        //Create tool menu
        toolMenu = new ToolMenu(this, model.getToolCount());
        JPanel toolPanel = new JPanel();
        toolPanel.add(toolMenu);
        this.add(toolPanel, BorderLayout.WEST);

        //Create draw area
        for (int i = 0; i <= 5; i++) {
            drawingArea[i] = new DrawingCanvas(model);
            tabs[i] = new JPanel();
            tabs[i].add(drawingArea[i]);
        }
        tabbedPane=new JTabbedPane();
        tabbedPane.setOpaque(false);
        for(int i=1;i<6;i++)
        {
            tabbedPane.addTab("Layer "+Integer.toString(i), drawingArea[i]);
        }
        tabbedPane.setBounds(0, 0, 864, 600);

        gridPanel = new GridLines(model); 
        gridPanel.setBounds(0, 33, 864, 567);
        gridPanel.setOpaque(false);

        JPanel whitePanel = new JPanel();
        whitePanel.setBackground(Color.WHITE);
        whitePanel.setBounds(0, 33, 864, 567);

        SelectPanel selectPanel = new SelectPanel();
        selectPanel.setBounds(0, 33, 864, 567);
        selectPanel.setOpaque(false);
        model.setSelectPanel(selectPanel);

        JLayeredPane mainPane = new JLayeredPane();
        mainPane.add(whitePanel, Integer.valueOf(-100));
        mainPane.add(tabbedPane, Integer.valueOf(0));
        mainPane.add(gridPanel, Integer.valueOf(100));
        mainPane.add(selectPanel, Integer.valueOf(150));
        this.add(mainPane, BorderLayout.CENTER);

        this.setVisible(true);
        // drawingArea[1].setWhite();
        toolMenu.getButtons()[0].doClick(); // Choose the pencil tool by default
    }

    /*
     * Allows to create an action listener here
     */
    public void addActionListener(JMenuItem e){ // Action listener seems to work this way for JMenuItems
        e.addActionListener(this);
    }

    /**
     * Inherited from ActionListener, overridden.
     */
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==submitBrushSize){
            BrushSize b = new BrushSize(model);
        }
        if(e.getSource()==gridlinesbtn){
            gridPanel.toggleGridlines();
        }
        if(e.getSource()==colorPicker){
            drawingArea[tabbedPane.getSelectedIndex()+1].ChangeColor();
        }

        if(e.getSource() == toolMenu.getButtons()[0]){
            toolMenu.selectButton(0);
            drawingArea[tabbedPane.getSelectedIndex()+1].Pencil();
        }
        if(e.getSource() == toolMenu.getButtons()[1]){
            toolMenu.selectButton(1);
            drawingArea[tabbedPane.getSelectedIndex()+1].Eraser();
        }
        if(e.getSource() == toolMenu.getButtons()[2]){
            toolMenu.selectButton(2);
            drawingArea[tabbedPane.getSelectedIndex()+1].Filler();
        }
        if(e.getSource() == toolMenu.getButtons()[3]){
            toolMenu.selectButton(3);
            drawingArea[tabbedPane.getSelectedIndex()+1].Text();
        }
        if (e.getSource() == toolMenu.getButtons()[4]) {
            toolMenu.selectButton(4);
            drawingArea[tabbedPane.getSelectedIndex()+1].drawShapes();
        }
        if (e.getSource() == toolMenu.getButtons()[5]) {
            toolMenu.selectButton(5);
            drawingArea[tabbedPane.getSelectedIndex()+1].Eyedropper();
        }
        if (e.getSource() == toolMenu.getButtons()[6]) {
            toolMenu.selectButton(6);
            drawingArea[tabbedPane.getSelectedIndex()+1].selectTool_help();
            drawingArea[tabbedPane.getSelectedIndex()+1].Select();
            //select
        }
        if (e.getSource() == toolMenu.getButtons()[7]) {
            toolMenu.selectButton(7);
            drawingArea[tabbedPane.getSelectedIndex()+1].sprayPaint();
            //sprayPaint
        }

        if(e.getSource()==pasteBtn){
            drawingArea[tabbedPane.getSelectedIndex()+1].Paste();
        }
    }

    /**
     * Update the brush size button to the current brush size
     */
    public void updateBrushSize(float size) {
        submitBrushSize.setText("Brush size: " + size); 
    }

    public boolean getdarkmode(){ // gets the current bool for darkmode
        return darkmode;
    }
    /**
     * Displays the file chooser and allows the user to save the image
     */
    public void saveBtnPressed()
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("*.png, *.jpg, *.bmp, *.gif", "png", "jpg", "bmp", "gif"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) 
        {
            File file = fc.getSelectedFile();
            try {
                FileNameExtensionFilter fileNameExtensionFilter = (FileNameExtensionFilter) fc.getFileFilter();
                if (!fileNameExtensionFilter.accept(file)) { //Checks if the filter accepts the extension entered
                    String extension = fileNameExtensionFilter.getExtensions()[0]; //Defaults to the first extension (png)
                    String filename = file.getName() + "." + extension; //Appends extension to filename
                    file = new File(file.getParent(), filename);
                }
                ImageIO.write(drawingArea[tabbedPane.getSelectedIndex()+1].getimage(), "png", file);
                
            } catch (IOException ex) {
                System.out.println("Failed to save image!");
            }
        } 
        else 
        {
            System.out.println("No file chosen!");
        }
    }

    /**
     * Displays the file chooser and allows the user to choose an image to draw to the canvas
     */
    public void importBtnPressed() {
        JFileChooser fc = new JFileChooser(); // Creates a new file chooser
        // Filters to only accept images
        fc.setFileFilter(new FileNameExtensionFilter("*.png, *.jpg, *.bmp, *.gif", "png", "jpg", "bmp", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { // If the file is chosen
            drawingArea[tabbedPane.getSelectedIndex()+1].importImage(fc.getSelectedFile());
        }
    }

    public void setdarkmode(boolean b) { // sets bool for darkmode
        darkmode = b;
    }

    /**
     * The hotkeys for the tools are assigned here to various keys
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_1) {
            toolMenu.selectButton(0);
            drawingArea[tabbedPane.getSelectedIndex()+1].Pencil();
        } else if (keyCode == KeyEvent.VK_2) {
            toolMenu.selectButton(1);
            drawingArea[tabbedPane.getSelectedIndex()+1].Eraser();
        }
        else if (keyCode == KeyEvent.VK_3) {
            toolMenu.selectButton(2);
            drawingArea[tabbedPane.getSelectedIndex()+1].Filler();
        }
        else if (keyCode == KeyEvent.VK_4) {
            toolMenu.selectButton(3);
            drawingArea[tabbedPane.getSelectedIndex()+1].Text();
        }
        else if (keyCode == KeyEvent.VK_5) {
            toolMenu.selectButton(4);
        }
        else if (keyCode == KeyEvent.VK_6) {
            toolMenu.selectButton(5);
            drawingArea[tabbedPane.getSelectedIndex()+1].Eyedropper();  
        }
        else if (keyCode == KeyEvent.VK_7) {
            toolMenu.selectButton(6);
            drawingArea[tabbedPane.getSelectedIndex()+1].Select();
        }
        else if (keyCode == KeyEvent.VK_8) {
            toolMenu.selectButton(7);
            drawingArea[tabbedPane.getSelectedIndex()+1].sprayPaint();
        }
        else if (keyCode == KeyEvent.VK_V) {
            drawingArea[tabbedPane.getSelectedIndex()+1].Paste();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }

}
