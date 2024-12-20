import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * The canvas which all drawing is done on
 */
public class DrawingCanvas extends JComponent implements ActionListener{
 
  // Image which draws
  private BufferedImage image; //Stores the buffered image;
  private Graphics2D g2; //The graphics which are shown to the user;
  private int curXcoor, curYcoor, olXcoor, olYcoor;
  private float radiusX, radiusY;
  private int sprayMax, width;
  private double randAngle, tempX, tempY;
  private ArrayList<Integer> xPnts=new ArrayList<Integer>();
  private ArrayList<Integer> yPnts=new ArrayList<Integer>();
  private int nPnts;
  private Model model; //Defines the model class;
  private boolean gridlinesvisible = false; // Sets the the gridline visibility
  private ArrayList<ArrayList<Integer>> mainArray; // arrays of
  private ArrayList<Integer> undoArray; // Keep track of which been undone and not, need data for redo so cant just delete, but will skip so undoPtr can know which next
  private ArrayList<Integer> tempUndoArray;
  private int currentUndoPtr = 0; // Use this to FILL in the data as user draws, start from start
  private int UndoPtr; // Use this to keep track of which has been undone
  private Boolean startLocationSelect = true;
  private Boolean endLocationSelect=false;
  private int selectXstart, selectYstart, selectXnew, selectYnew;
  private ArrayList<Integer> copyDataArr; // hold data for last and new location for copying
  private Boolean selectBool = false;
  private Boolean pasteBool = false; // check if user has clicked on any of buttons button
  private Boolean textBool = false;
  private String textFromTxtBtn;
  private JButton submitTxtBtn;
  private JTextField textArea;
  private JFrame TextFrame;
  private JFrame AdviceFrame;
  private JFrame AdviceFrame2;
 
  /**
   * Main drawing area, handle events that occur on drawing canvas here
   */
  public DrawingCanvas(Model model) {
    this.model = model;
    this.nPnts = 0;
    model.setCurrentTool(Tools.PENCIL);
    model.setBrushSize(5);//default size to begin with

    copyDataArr = new ArrayList<Integer>(4);
    mainArray = new ArrayList<ArrayList<Integer>>(5); // initialize
    undoArray = new ArrayList<Integer>(5); // initialize

    // initialize for mainArray,undoArray and copyData Array
    for (int i = 0; i < 5; i++) {
      ArrayList<Integer> temp = new ArrayList<Integer>();
      mainArray.add(temp);
      undoArray.add(0);
    }

    for (int i = 0; i < 4; i++) {
      copyDataArr.add(0);
    }

    tempUndoArray = new ArrayList<Integer>();
    UndoPtr = -1; // start from end (index number)

    /**
     * Handles mouse events when user is moving the mouse
     */
    addMouseListener(new MouseAdapter() { //look for mouse movement (listener)
      public void mousePressed(MouseEvent e) {
        // save coordinates of x and y
        olXcoor = e.getX();
        olYcoor = e.getY();

        if (model.getCurrentTool() != Tools.SELECT) {
          model.getSelectPanel().repaint();
        }

        if(model.getCurrentTool() == Tools.FILL){
          floodFill(new Position(olYcoor,olXcoor),image,g2,image.getRGB(olXcoor, olYcoor), model.getCurrentColor());
          repaint();
        }
        if(model.getCurrentTool() == Tools.DROPPER){
          //set brush colour to pixel colour
          eyedropper(new Position(olYcoor,olXcoor), image, g2);
          repaint();
        }

        tempUndoArray = new ArrayList<Integer>(); // reset so doesnt keep stacking up old data
        // DO NOT USE .CLEAR() IT DUPLICATES DATA

        if(textBool){
          textBool = false;
          TextMenu(); //open up text menu
        }
        
        if (pasteBool) { // IF user has clicked on paste button
          try {
            BufferedImage imageNew = ImageIO.read(new File("copy.png"));
            g2.drawImage(imageNew, olXcoor, olYcoor, null); // paste where user selects
            g2.drawImage(imageNew, copyDataArr.get(0), copyDataArr.get(1), null);
            repaint();
          } catch (Exception exception) {
          }
          pasteBool = false; // Set false until next time user clicks on paste again
        }
        //Checks which tool it is and draws the object.
        if (model.getCurrentTool() == Tools.RECTANGLE){
          drawShapeFunc(0);
        }
        if(model.getCurrentTool() == Tools.CIRCLE){
          drawShapeFunc(1);
        }
        if(model.getCurrentTool() == Tools.TRIANGLE){
          drawShapeFunc(2);
        }

        if (model.getCurrentTool() == Tools.SELECT && !pasteBool) { // if true, SELECT TOOL AREA, Selecting an item is same as copying it
          //selectTool_help();
          selectToolFunctionality(); //Handles what happens when the user clicks on select
          
        }
      }

      /**
       * User releases mouse, handle necessary events here
       */
      public void mouseReleased(MouseEvent e) { // when user releases the mouse
        if (model.getCurrentTool() == Tools.SELECT && !pasteBool) { // if true, SELECT TOOL AREA, Selecting an item is same as copying it
          
          selectToolFunctionality(); //Handles what happens when the user clicks on select
        }
        //Draws the select area for the shape
        if(model.getCurrentTool() == Tools.RECTANGLE){
          drawShapeFunc(0);
        }
        if(model.getCurrentTool() == Tools.CIRCLE){
          drawShapeFunc(1); 
        }
        if(model.getCurrentTool() == Tools.TRIANGLE){
          drawShapeFunc(2);
        }
          
        if (currentUndoPtr > 4) {
          mainArray.remove(0);
          undoArray.remove(0);
          // Collections.rotate(mainArray, -1);
          mainArray.add(tempUndoArray);
          undoArray.add(0);

          if (UndoPtr < 4) {
            UndoPtr++;
          }
        } else {
          mainArray.set(currentUndoPtr, tempUndoArray);
          currentUndoPtr++;
          UndoPtr++;
        }
      }
    });
 
    addMouseMotionListener(new MouseMotionAdapter() { //when moving mouse
      
      public void mouseDragged(MouseEvent e) {
        // when user moving move get current coordinates
        curXcoor = e.getX();
        curYcoor = e.getY();
        addCoords(curXcoor, curYcoor);
 
        if (g2 != null) { //if image is avaiable to draw on
          g2.setStroke(new BasicStroke(model.getBrushSize())); //set brush size
          if(model.getCurrentTool() == Tools.PENCIL){ //drawling lines pencil, brush etc
            g2.drawLine(olXcoor, olYcoor, curXcoor, curYcoor);

            tempUndoArray.add(olXcoor); // have array per set of 4, store old and new coords
            tempUndoArray.add(olYcoor);
            tempUndoArray.add(curXcoor);
            tempUndoArray.add(curYcoor);
          }
          if(model.getCurrentTool() == Tools.SPRAYPAINT) {//sprayPaint brush - new tool/pop-up on pencil tool
            Random rand = new Random();
            width = (int)model.getBrushSize();
            sprayMax = width; //num of spray dots

            for (int i =0; i<sprayMax; i++)
            {
              //model.setBrushSize(1); //set size of points to be drawn
              radiusX= rand.nextInt((width) + 1);
              radiusY= rand.nextInt((width) + 1);

              randAngle = Math.toRadians(rand.nextInt(360));

              tempX = (radiusX*(Math.cos(randAngle))) + curXcoor;
              tempY = (radiusY*(Math.sin(randAngle))) + curYcoor;
              Color c = model.getCurrentColor();
              Color sprayCol = new Color(c.getRed(), c.getGreen(), c.getBlue(), 130);
              g2.setColor(sprayCol);
              //draw points on canvas around current mouse position at temporary x and y coords
              g2.drawLine((int)tempX, (int)tempY, (int)tempX, (int)tempY);
            }
          }
          else if(model.getCurrentTool() == Tools.ERASER){ //eraser should be an oval
            g2.drawLine(olXcoor,olYcoor,curXcoor,curYcoor);
          }

          // store current as old coordinates
          olXcoor = curXcoor;
          olYcoor = curYcoor;
          // refreshing drawing area
          repaint();
        }
      }
    });
  }
  /**
  *Called when the drawng cmponennt is needed.
  *This also overrides when called.
  */
  protected void paintComponent(Graphics g) {
    if (image == null) {
      // image which we draw, creating if null
      image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
      g2 = (Graphics2D) image.createGraphics();
      g2.setColor(Color.BLACK);
      // g2.setComposite(AlphaComposite.Src);
      // setWhite(); // sets background white
    }
    g.drawImage(image, 0, 0, null); // draws in image which were drawing
    if(gridlinesvisible) // checks if gridlines being visible is true
    {
      drawGridlines(model.getGridSize());// if condition is met then using the gridsize given in model class we use it as a parameter for drawGridlines function
      repaint();// repaint to show changes
    }
  }

  /**
  *Toggles the gridlines on or off
  */
  public void toggleGridlines(boolean isSelected)// This function exists so that when the gridlines checkbox is clicked again the gridlines can toggle off by setting the boolean false
  {
    gridlinesvisible = !gridlinesvisible; // sets it to false
  }
  /**
  *Draws the gridlines when Toggles
  */
  public void drawGridlines(int gridsize) // function to draw gridlines using width and height of canvas
  {
    g2.setColor(Color.BLACK);// default colour to draw lines is black
    for(int i=0; i < getWidth(); i+= gridsize)
    {
      g2.drawLine(i,0,i,getHeight()); // 
    }
    for(int i=0; i < getHeight(); i+= gridsize)
    {
      g2.drawLine(0,i,getWidth(),i);
    }
  }

  /**
  *Defines the selectTool start location
  */
  public void selectToolStart(){
    if (startLocationSelect) {
      selectXstart = olXcoor;
      selectYstart = olYcoor;

      startLocationSelect = false;
    }
  }

  /**
   * Creates the selection area for which the area is selected
   */ 
  public void selectToolFunctionality() { //Used for when user clicks select tool, the logic for it
    if (startLocationSelect) {
      
      selectXstart = olXcoor;
      selectYstart = olYcoor;

      startLocationSelect = false;
    }
    else {
      selectXnew = olXcoor;
      selectYnew = olYcoor;

      // Copy data into an array to hold it
      copyDataArr.set(0, selectXstart);
      copyDataArr.set(1, selectYstart);
      copyDataArr.set(2, selectXnew);
      copyDataArr.set(3, selectYnew);

      // Create image of area before rectangle drawn in
      try {
        //System.out.println("1");
        BufferedImage img = new BufferedImage(
        Math.abs(image.getWidth(null)),
        Math.abs(image.getHeight(null)),
        BufferedImage.TYPE_INT_ARGB);
        Graphics2D savingImg = img.createGraphics();
        
        if((selectXstart-selectXnew)!=0){ //IF the user clicks twice on same area( i.e not dragging mouse then)
          BufferedImage subImg = img.getSubimage(
          selectXstart,
          selectYstart,
          Math.abs(selectXstart - selectXnew),
          Math.abs(selectYstart - selectYnew));
          model.setSelectedImage(subImg);
          savingImg.drawImage(image, 0, 0, null);
          ImageIO.write(subImg, "PNG", new File("copy.png"));
          savingImg.dispose();
          
          // Draw rectangle to show area
          int transparency = 127; // 50%
          g2.setPaint(new Color(67, 84, 90, transparency)); // DRAW in rectangle to demonstrate to user what has been selected 
          SelectPanel s = model.getSelectPanel();
          s.setPoints(selectXstart, selectYstart, selectXnew, selectYnew);
          s.toggleDraw();
          //g2.fillRect(selectXstart, selectYstart, selectXnew - selectXstart, selectYnew - selectYstart);
          // g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR)); This was causing the select tool to be black, don't know who added this
          repaint();
          startLocationSelect = true;
          AdviceFrame2.dispose();
        }
        else{
          startLocationSelect=true;
        }
      } catch (IOException exception) {
        // TODO: handle exception
      }

    }
  }
  /**
  *Defines the coordinates to be then used later on
  */
  public void addCoords(int x, int y) {
    xPnts.add(Integer.valueOf(x));
    yPnts.add(Integer.valueOf(y));
    nPnts++;
  }

  /**
   * Draws the possible shapes the user chooses
   *@param i What shape to draw
   */ 
  public void drawShapeFunc(int i){
    try{
      if (startLocationSelect) {
        selectXstart = olXcoor;
        selectYstart = olYcoor;
        startLocationSelect = false;
      }
      else {
        selectXnew = olXcoor;
        selectYnew = olYcoor;

        copyDataArr.set(0, selectXstart);
        copyDataArr.set(1, selectYstart);
        copyDataArr.set(2, selectXnew);
        copyDataArr.set(3, selectYnew);

        g2.setPaint(model.getCurrentColor());
        //Checks which shape tool mode it is in
        if(i == 0){
          g2.drawRect(selectXstart, selectYstart, selectXnew - selectXstart, selectYnew - selectYstart);
        } 
        else if(i==1){
          g2.drawOval(selectXstart, selectYstart, selectXnew-selectXstart, selectYnew-selectYstart);
        }
        else if(i==2){
          double tempX = 0.5*(Math.abs(selectXstart-selectXnew)); // Finds the middle point of th base of the triangle
          int midX = (int) tempX;
          double tempY = 0.5*(Math.abs(selectYstart-selectYnew));
          int midY = (int) tempY;
          int xPoints[] = {selectXstart, midX, selectXnew};
          int yPoints[] = {selectYstart, midY, selectYnew};
          g2.drawPolygon(xPoints,yPoints,3);
          midX = 0;
          midY =0;
        }
        repaint();
        startLocationSelect = true;
        model.setCurrentTool(Tools.PENCIL);
      }
    }
    catch(Exception exception){
    }
  }

  /**
   * Sets background to white
   */
  public void setWhite() {
    try{
      g2.setPaint(Color.white);
      g2.clearRect(0, 0, image.getWidth(), image.getHeight());
      g2.fillRect(0, 0, getSize().width, getSize().height); //in canvas

      g2.setPaint(Color.black); //set back to drawing
    }catch(Exception e){
      if (image == null || g2==null) {
        // image which we draw, creating if null
        image = new BufferedImage(getWidth()+1, getHeight()+1, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) image.getGraphics();
        g2.setPaint(new Color(255,255,255,127*2));
        g2.fillRect(0, 0, getSize().width, getSize().height); //in canvas
        g2.setPaint(Color.black); //set back to drawing
      }
      g2.setPaint(model.getCurrentColor());
      repaint();
    }
    
    repaint(); //refresh
  }


  /**
   * Method which uses a queue-based floodfilling algorithm for the fill function
   * @param p position at which to draw
   * @param drawingImage the BufferedImage component associated with the canvas
   * @param g the Graphics2D object associated with the drawingImage param
   * @param oldColor the rgb value of the background color at point p
   * @param newColor the desired colour to fill with
   */
  public void floodFill(Position p,BufferedImage drawingImage, Graphics2D g,  int oldColor, Color newColor)  {
    LinkedList<Position> queue = new LinkedList<Position>();
    queue.add(p);
    int ok=1;
    while (queue.size() > 0) {
      Position newPosition = queue.poll();
      int x=newPosition.getX();
      int y=newPosition.getY(); 
      ok=1;
      
      if (x < 1 || x >= drawingImage.getWidth()-1 || y < 1 || y >= drawingImage.getHeight()-1) {
        ok=0;
      }
      if (drawingImage.getRGB(x, y) != oldColor && ok==1) {
        ok=0;
      }
      if(ok==1){
        drawingImage.setRGB(x, y, newColor.getRGB());
        queue.add(new Position(y, x - 1));
        queue.add(new Position(y, x + 1));
        queue.add(new Position(y - 1, x));
        queue.add(new Position(y + 1, x));
      }
    } 
  }

  /**
   * Eyedropper tool - sets paint brush colour to the colour of the pixel at the mouse position upon click.
   * 
   * @param p position of mouse cursor
   * @param canvas  image of the drawable canvas
   * @param g provides control over graphical editing
   */
  public void eyedropper(Position p, BufferedImage canvas, Graphics2D g)
  {
    int x = p.getX();
    int y = p.getY();
    Color droppedColor = new Color(canvas.getRGB(x,y));
    model.setCurrentColor(droppedColor);
  }

  //Tool setup functions
  /**
   * When user clicks on Pencil tool, handle events here
   */
  public void Pencil() {
    model.setCurrentTool(Tools.PENCIL);
    try{
      g2.setPaint(model.getCurrentColor());
    }catch(Exception e){
      if (image == null || g2==null) {
        // image which we draw, creating if null
        image = new BufferedImage(getWidth()+1, getHeight()+1, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) image.getGraphics();
        g2.setPaint(Color.BLACK);
      }
      g2.setPaint(Color.BLACK);
      g2.setStroke(new BasicStroke(model.getBrushSize()));
      repaint();
    }
  }

  /**
   * When user clicks on eraser, handle events here
   */
  public void Eraser() {
    // Color oldCol = model.getCurrentColor();
    model.setCurrentTool(Tools.ERASER);
    // model.setCurrentColor(Color.white);
    
    try{
      g2.setPaint(Color.white);
      g2.setStroke(new BasicStroke(model.getBrushSize()));
    }catch(Exception e){
      if (image == null || g2==null) {
        // image which we draw, creating if null
        image = new BufferedImage(getWidth()+1, getHeight()+1, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) image.getGraphics();
        g2.setPaint(Color.white);
        g2.setStroke(new BasicStroke(model.getBrushSize()));
      }
      g2.setPaint(Color.white);
      g2.setStroke(new BasicStroke(model.getBrushSize()));
      repaint();
    }

    // model.setCurrentColor(oldCol);
  }
  /**
  * Sets the current tool to Fill
  */
  public void Filler() {
    model.setCurrentTool(Tools.FILL);
  }

  /**
   * Creates the window which allows the user to choose the shape to draw
   */
  public void drawShapes() {
    Shapes newWindow = new Shapes(model);
    model.setCurrentTool(Tools.SHAPES);
  }

  /**
   * Allows user to change colour of brush
   */
  public void ChangeColor() {
    Color chosenColor = JColorChooser.showDialog(null, "Choose A Color", model.getCurrentColor()); // summons the color pallet
    model.setCurrentColor(chosenColor); // sets the currnet color to the color selected
    
    try{
      g2.setPaint(chosenColor);
    }catch(Exception e){
      if (image == null || g2==null) {
        // image which we draw, creating if null
        image = new BufferedImage(getWidth()+1, getHeight()+1, BufferedImage.TYPE_INT_RGB);
        g2 = (Graphics2D) image.getGraphics();
        g2.setPaint(chosenColor);
      }
      g2.setPaint(chosenColor);
      repaint();
    }
  }

  /**
   * When user clicks on select button this function is run
   */
  public void Select() {
    model.setCurrentTool(Tools.SELECT);
    selectBool = true;
  }

  /**
   * When user clicks on Paste button this function is run
   */
  public void Paste() {
    pasteBool = true;
  }
  /**
  *Returns the buffered Image when called
  @return image The current image
  */
  public BufferedImage getimage(){
    return image;
  }
  /**
  *Sets the current tool to the Eye Dropper
  */
  public void Eyedropper() {
    model.setCurrentTool(Tools.DROPPER);
  }

  public void sprayPaint() {
    model.setCurrentTool(Tools.SPRAYPAINT); 
  }
  /**
   * Used to import an image file and draws it to the canvas
   * @param imgFile The image to be drawn to the canvas
   */
  public void importImage(File imgFile) {
    try {
      BufferedImage importedImage = ImageIO.read(imgFile);
      Image newImage = importedImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT);
      g2.drawImage(newImage, 0, 0, null);
      repaint();
    } catch (IOException exception) {
      System.out.println("Import error: \n" + exception);
    }
  }

  /**
  *Flips the entire canvas horizontally
  */
  public void flipCanvasHorizontal() {
    g2.drawImage(image, getWidth(), 0, -getWidth(), getHeight(), null);
    repaint();
  }
  /**
  *Flips the entire canvas Vertically
  */
  public void flipCanvasVertical() {
    //Translates and scales the canvas
    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
    tx.translate(0, -image.getHeight(null));
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    Image flippedImage = op.filter(image, null);

    g2.drawImage(flippedImage, 0, 0, null);
    repaint();
  }

  /**
  *Rotates the canvas clockwise by the given number of degrees
  *@param degrees the numbe of degrees to rotate te canvas by
  */
  public void rotateCanvas(int degrees) {
    double xLoc = image.getWidth() / 2;
    double yLoc = image.getHeight() / 2;
    AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(degrees), xLoc, yLoc);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    g2.drawImage(op.filter(image, null), 0, 0, null);
    repaint();
  }

  /**
  *Flips the image on the canvas horizontally
  */
  public void flipImageHorizontal() {
    BufferedImage img = model.getSelectedImage();
    g2.drawImage(img, selectXstart+img.getWidth(), selectYnew-img.getHeight(), -img.getWidth(), img.getHeight(), null);
    repaint();
  }
  /**
  *Flps the image on the canvas vertically
  */
  public void flipImageVertical() {
    BufferedImage img = model.getSelectedImage();
    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
    tx.translate(0, -img.getHeight(null));
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    Image flippedImage = op.filter(img, null);

    g2.drawImage(flippedImage, selectXstart, selectYnew-img.getHeight(), null);
    repaint();
  }
  /**
  *Rotates the image clockwise by the given amount of degrees
  *@param degrees The number of degrees to rotate the image by
  */
  public void rotateImage(int degrees) {
    BufferedImage img = model.getSelectedImage();
    double xLoc = img.getWidth() / 2;
    double yLoc = img.getHeight() / 2;
    AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(degrees), xLoc, yLoc);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    g2.drawImage(op.filter(img, null), selectXstart, selectYnew-img.getHeight(), null);
    repaint();
  }

  /**
   * When user clicks on adding text, this is the actual JFrame information for getting text from user
   */
  public void TextMenu(){
    TextFrame = new JFrame("Input text");
    JPanel TextPanel = new JPanel();
    submitTxtBtn = new JButton("Submit");
    submitTxtBtn.addActionListener(this);
    textArea = new JTextField(16);

    TextPanel.add(textArea);
    TextPanel.add(submitTxtBtn);

    TextFrame.add(TextPanel);
    TextFrame.setSize(200, 200);
    TextFrame.setVisible(true);
  }

  /**
   * Informs the user what the Text tool does
   */
  public void Text() {
    textBool = true; //user has clicked on text button

    AdviceFrame = new JFrame("User Help");
    JPanel AdvicePanel = new JPanel();
    JLabel text = new JLabel();
    text.setText("Click on any part of the screen to select where to place text.");
    AdvicePanel.add(text);
    AdviceFrame.add(AdvicePanel);
    AdviceFrame.setSize(400,100);
    AdviceFrame.setVisible(true);
  }

  /**
   * Informs the user what the Select tool does
   */
  public void selectTool_help(){
    AdviceFrame2 = new JFrame("User Help");
    JPanel AdvicePanel = new JPanel();
    JLabel text = new JLabel();
    text.setText("Hold and Drag a area then use one of the buttons to apply to the selected area.");
    AdvicePanel.add(text);
    AdviceFrame2.add(AdvicePanel);
    AdviceFrame2.setSize(600,100);
    AdviceFrame2.setVisible(true);
  }

  /**
   * When user wants to redo after undo, handles the logic for redoing
   */
  public void Redo() {
    if (undoArray.get(UndoPtr) == 1) { // redraw it
      g2.setPaint(Color.black);
      //System.out.println("undoing");
      g2.setStroke(new BasicStroke(model.getBrushSize()));
      
      if(mainArray.get(UndoPtr).get(0)==8080){ //is text
        //System.out.println("text undo");
      }
      else{
        for (int i = 0; i < mainArray.get(UndoPtr).size(); i = i + 4) {
          g2.drawLine(
              mainArray.get(UndoPtr).get(i),
              mainArray.get(UndoPtr).get(i + 1),
              mainArray.get(UndoPtr).get(i + 2),
              mainArray.get(UndoPtr).get(i + 3));
        }

        repaint();
        undoArray.set(UndoPtr, 0);
        UndoPtr++;
        if (UndoPtr > 4) {
          UndoPtr = 4;
        }
      }
    }
    else {
      // System.out.println("find next available");
      //Find next avaiable
      //System.out.println(undoArray);
      //System.out.println(UndoPtr);
      Boolean foundNext=false;
      for (int i = 4; i > -1; i--) {
        if (undoArray.get(i) == 1) {
          foundNext=true;
          UndoPtr = i;
          break;
        }
      }
      if(foundNext){
          g2.setPaint(Color.black);
          g2.setStroke(new BasicStroke(model.getBrushSize()));
          // draw over in white now
          for (int i = 0; i < mainArray.get(UndoPtr).size(); i = i + 4) {
            g2.drawLine(
              mainArray.get(UndoPtr).get(i),
              mainArray.get(UndoPtr).get(i + 1),
              mainArray.get(UndoPtr).get(i + 2),
              mainArray.get(UndoPtr).get(i + 3));
          }

          repaint();
          undoArray.set(UndoPtr, 0);
          UndoPtr++;
          if (UndoPtr > 4) {
            UndoPtr = 4;
          }
        }
      }
  }

  /**
   * Handles logic for when the user tries to undo a drawn area
   */
  public void Undo() {
    //System.out.println(undoArray);
    if((UndoPtr<4 && UndoPtr>0) || UndoPtr>4){ //at the end or at start, no decrement
      UndoPtr--;
    }
    //System.out.println(UndoPtr);

    if (undoArray.get(UndoPtr) == 0) { // not been 'undone' yet
      g2.setPaint(Color.white);
      g2.setStroke(new BasicStroke(model.getBrushSize() + 1));

      //System.out.println(mainArray);
      if(mainArray.get(UndoPtr).get(0)==8080){ //first item is code for TEXT
        g2.setPaint(Color.white); //set to white to hide it from screen
        repaint();
        g2.setStroke(new BasicStroke(model.getBrushSize()));
        g2.setPaint(Color.black);

        undoArray.set(UndoPtr, 1);
        //UndoPtr--;

        if (UndoPtr < 0) {
          UndoPtr = 0;
        }
       // System.out.println(undoArray);
      }
      else{
        // draw over in white now
        for (int i = 0; i < mainArray.get(UndoPtr).size(); i = i + 4) {
          g2.drawLine(
            mainArray.get(UndoPtr).get(i),
            mainArray.get(UndoPtr).get(i + 1),
            mainArray.get(UndoPtr).get(i + 2),
            mainArray.get(UndoPtr).get(i + 3));
        }

        repaint();
        g2.setStroke(new BasicStroke(model.getBrushSize()));
        g2.setPaint(Color.black);

        undoArray.set(UndoPtr, 1);
        //UndoPtr--;

        if (UndoPtr < 0) {
          UndoPtr = 0;
        }
       // System.out.println(undoArray);
      }
    } 
    else {
     // find next available
     // System.out.println(undoArray);
      for (int i = 0; i < 5; i++) {
        if (undoArray.get(i) == 0) {
          UndoPtr = i;
          break;
        }
      }
      //System.out.println(UndoPtr);
      
      if(mainArray.get(UndoPtr).get(0)==8080){ //first item is code for TEXT
        g2.setPaint(Color.white);

        repaint();
        g2.setStroke(new BasicStroke(model.getBrushSize()));
        g2.setPaint(Color.black);

        undoArray.set(UndoPtr, 1);
       // UndoPtr--;

        if (UndoPtr < 0) {
          UndoPtr = 0;
        }
        //System.out.println(undoArray);
      }
      else{
        g2.setPaint(Color.white);
        g2.setStroke(new BasicStroke(model.getBrushSize() + 1));
        // draw over in white now
        for (int i = 0; i < mainArray.get(UndoPtr).size(); i = i + 4) {
          g2.drawLine(
            mainArray.get(UndoPtr).get(i),
            mainArray.get(UndoPtr).get(i + 1),
            mainArray.get(UndoPtr).get(i + 2),
            mainArray.get(UndoPtr).get(i + 3));
        }

        repaint();
        g2.setStroke(new BasicStroke(model.getBrushSize()));
        g2.setPaint(Color.black);
        undoArray.set(UndoPtr, 1);
       // UndoPtr--;
        if (UndoPtr < 0) {
          UndoPtr = 0;
        }
        //System.out.println(undoArray);
      }
    }
    //Dont move UndoPtr checks <0 and repaint here as if undoArray is full (1s) then shouldn't do anything
    //System.out.println(undoArray);
  }
  /**
  * Gets the canvas image
  * @return Returns the current image.
  */
  public BufferedImage getCanvasImage()
  {
    return this.image;
  }

  /**
   * Button click handler for inside drawing canvas
   */
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == submitTxtBtn) { // on clicking submit button on TEXT, will draw on last clicked place
      textBool=true;
      textFromTxtBtn = textArea.getText();
      g2.drawString(textFromTxtBtn, olXcoor, olYcoor);
      TextFrame.dispose();
      AdviceFrame.dispose();
      
      repaint();
     // System.out.println(undoTextArr);
      textBool=false;
    }
  }
}