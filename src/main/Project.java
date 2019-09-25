/*
Mücahit Tanacıoğlu -150115006
Mahmut Hilmi Arıkmert- 150117024

*/

package main;

/*
Including some javaFx libraries to use
*/

import java.io.File;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Co
 */
public class Project extends Application {
    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 4;
    public static final int HEIGHT = 4;
    
    String[] level =new String[5];
    
    Tile[] tiles ;//Tile is a class which extends Reactangle.
    
    Stage pointer; //Pointer to change primaryStage elements.
     
    BorderPane pointToRoot; //Pointer to change root elements.
    
    final Circle ball = new Circle (4, 15, 15);//the ball which animated at the end of each level
   
    int moveCounter=0;//move count for each level
    
    static int moveCount = 0, counter=0;//Move count is shows total move whole game, counter is a variable which sets level changes.
    
    private Parent createGameBoard(String[] inputArray)throws Exception{
       
        BorderPane root = new BorderPane();
        
        tiles = getTiles(inputArray); //getTiles returns all tiles for given level.
        
        //Adds tiles 1 by 1 to root
        for(int y = 0;y <4;y++)
            for(int x = 0;x <4;x++)
                root.getChildren().add(tiles[y*4+x]);
                
        pointToRoot=root;//creating link to root to make animation after
        
        return root;
       
    }
    
   

    private String[] readInput(String level)throws Exception{
        
       //Reading level information and stores in String array.
        
        String[] inputStr= new String[16];
       
        int k = 0;
            File input = new File("levels/"+level);
            Scanner reader = new Scanner(input);
           
            while(reader.hasNextLine()){
                inputStr[k]=reader.nextLine();
               k++;
            }
            
           reader.close();
            
            return inputStr;
    }
    
    
    private int toBoard(double pixel) {
        //return integer between 0-15 for given location.
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }
  
    public void start(Stage primaryStage)throws Exception {
        
        level[0]="level1.txt";
        level[1]="level2.txt";
        level[2]="level3.txt";  //Creating String array  contains level files names.
        level[3]="level4.txt";
        level[4]="level5.txt";
                
        Scene firstS  = new Scene(createGameBoard(readInput("level1.txt")),TILE_SIZE*4,TILE_SIZE*4); // Creating initial scene
        
        pointer=primaryStage;//Creating link to primaryStage to change levels.
        
        primaryStage.setTitle("Level: "+(counter+1)+" Move Count: "+moveCounter+" Total Move: "+moveCount); //Move count and level name set on title.
        
        primaryStage.setScene(firstS); 
        
        primaryStage.resizableProperty().set(false);
        
        primaryStage.getIcons().add(new Image("file:images/ball.png")); //Changing icon
        
        primaryStage.show();
    }

    
    public static void main(String[] args) {
        launch(args);
    }

    private Tile[] getTiles(String[] inputStr) {
       String url,name;
       Image img;
       Tile[] tiles= new Tile[16];
       
       for(int y = 0;y <4;y++){
            for(int x = 0;x <4;x++){ //creates tiles base on given level input
                
            String[] splStr = inputStr[y*4+x].split(",");
       
            name = splStr[1]+ "_" +splStr[2];
       
            url = "images/"+name+".png";
       
            img = new Image("file:"+url);
            //sets correct flags for tiles static and movable
            if(name.equalsIgnoreCase("Starter_Vertical")||name.equalsIgnoreCase("Starter_Horizontal")||name.equalsIgnoreCase("PipeStatic_Vertical")||name.equalsIgnoreCase("PipeStatic_Horizontal")||name.equalsIgnoreCase("End_Horizontal")||name.equalsIgnoreCase("End_Vertical")||name.equalsIgnoreCase("PipeStatic_01")){
                tiles[y*4+x] = new Tile(img,x,y,true,false,name);
            }else if(splStr[2].equalsIgnoreCase("Free"))
                tiles[y*4+x] = new Tile(img,x,y,false,true,name);
            else
                tiles[y*4+x] = new Tile(img,x,y,false,false,name);  
       
            }
       }
       return tiles;
    }
   
    
   public void Move(int FinalPos,Tile tile,int firstPos){//This method swap tiles as their painted location and in tile array.
       
       int tempX =(int)tiles[FinalPos].getOldX();//stores the tile to swap's x and y value as temp
       int tempY =(int)tiles[FinalPos].getOldY();
       
       Tile temp = tiles[FinalPos]; //stores tile to swap as temp 
       
       tiles[FinalPos].relocate(tile.getOldX(),tile.getOldY()); // change screen location of tile to swap
      
       tile.relocate(tempX, tempY); //change screen location of tile dragged
        
       tiles[FinalPos].setOldX(tile.getOldX());//Chamge x and y value of tile to swap with tile dragged.
       tiles[FinalPos].setOldY(tile.getOldY());
       
       
       tile.setOldX(tempX);//Chamge x and y value of tile dragged with stored first tile to swap's x and y value.
       tile.setOldY(tempY);
      
       
       tiles[FinalPos]=tile;// changes array location of tiles
       tiles[firstPos]=temp;
      
   }
  //Tile class as inner clas. Tiles are basicially the images on screen
   class Tile extends Rectangle {

   private double mouseX,mouseY,oldX,oldY;
   private boolean isstatic,isFreeMove;
   String url;
   
    public Tile(Image img, int x, int y,boolean isStatic,boolean isFreeMove,String name) {
        isstatic = isStatic;//flag for static
        this.isFreeMove=isFreeMove;//flag for free to move
        setWidth(Project.TILE_SIZE);
        setHeight(Project.TILE_SIZE);
        oldX=x * Project.TILE_SIZE;
        oldY=y * Project.TILE_SIZE;
        relocate(oldX, oldY);
        this.url=name;
        setFill(new ImagePattern(img));//fill tile with proper image
       
        setOnMousePressed(e -> {
            mouseX = e.getSceneX(); //gets X and Y value to make drag
            mouseY = e.getSceneY();
            
            
        });

        setOnMouseDragged(e -> {
            if(!isstatic && !isFreeMove){//Static and free movemant tiles cant be drag.
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);//Makes drag view
            }
        });
        
        setOnMouseReleased((MouseEvent e) -> {
            
                    
            int newX = toBoard(getLayoutY());   //gets x and y value between 3-0
            int newY = toBoard(getLayoutX());
            
            int newPos = 4*newX+newY;   //this gives last location of tile in Tile array.
            int oldPos = (int)(4*oldY+oldX)/100; //this gives firs location of tile in Tile array.
       
            boolean canmove = (  newPos==(oldPos-1) || newPos==(oldPos+1) ||newPos==(oldPos-4) || newPos==(oldPos+4));//This set only 1 unit move and no dioganal move.
            
            if(!canmove)
                relocate(getOldX(),getOldY());
            else if ((isStatic() || newX < 0 || newY < 0 || newX > WIDTH || newY > HEIGHT|| !tiles[newPos].isFreeMove || tiles[newPos].isstatic ))//this control whether tile is static or free move etc.
                relocate(getOldX(),getOldY());
            else {
                 moveCount++;
                 moveCounter++;
                 pointer.setTitle("Level: "+(counter+1)+"  Move Count: "+moveCounter+" Total Move: "+moveCount);//Title update to count moves
                Move(newPos,this,oldPos);//swap tile 
                }
            
            
            
            if(levelComplate(level[counter])){
                
                
                        ball.setFill(new ImagePattern(new Image("file:images/ball.png")));//Animated ball fill with ball.png
                       
                        Path path = new Path();
                       
                        if(counter<3) {
                            path.getElements().add(new MoveTo(50f, 50f));
                            path.getElements().add(new LineTo(50f, 350f));//The path for level 1-3
                            path.getElements().add(new LineTo(350f, 350f));
                        }else{
                            path.getElements().add(new MoveTo(50f, 50f));
                            path.getElements().add(new LineTo(50f, 250f));
                            path.getElements().add(new LineTo(250f, 250f));//The path for level 4 and 5
                            path.getElements().add(new LineTo(350f, 250f));
                            path.getElements().add(new LineTo(350f, 135f));

                            }
                        PathTransition pathTransition = new PathTransition();
                        pathTransition.setDuration(Duration.millis(4000));
                        pathTransition.setPath(path);
                        pathTransition.setNode(ball);
                        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
                        pathTransition.setCycleCount(1);
                        pathTransition.setAutoReverse(true);

                        pathTransition.play();
                        
                        pathTransition.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                           
                            
                           if (newStatus == Animation.Status.STOPPED) {
                            counter++;
                            moveCounter=0;
                           
                           if(counter==5)
                                System.exit(0);
                           try {
                                pointer.setTitle("Level: "+(counter+1)+"  Move Count: "+moveCounter+" Total Move: "+moveCount); //updating move counter
                                pointer.setScene(new Scene(createGameBoard(readInput(level[counter])),TILE_SIZE*4,TILE_SIZE*4)); //loading new level
                            }catch (Exception ex) {
                                Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
                            }
                                
                            }
                        });
                         pointToRoot.getChildren().add(ball);
            }
            
        });
       
    }
    
    public void setOldX(double k){
        oldX =k;
    }
    public double getOldX() {
        return oldX;
    }

    /**
     * @return the oldY
     */
    public void setOldY(double k){
        oldY =k;
    }
    public double getOldY() {
        return oldY;
    }

    public boolean isStatic() {
        return isstatic;
    }
    public boolean isFreeMove() {
        return isFreeMove;
    }

   
   //this method returns boolean based on given level and correct tile lineup.
    private boolean levelComplate(String level) {
        switch(level){
            case"level1.txt"://Coorect tile lineup for level 1.
                if(tiles[4].url.equalsIgnoreCase("Pipe_Vertical")&&tiles[8].url.equalsIgnoreCase("Pipe_Vertical")&&tiles[12].url.equalsIgnoreCase("Pipe_01")&&tiles[13].url.equalsIgnoreCase("Pipe_Horizontal"))
                    return true;
                else
                  return false;
            case"level2.txt"://Coorect tile lineup for level 2.
                if(tiles[4].url.equalsIgnoreCase("Pipe_Vertical")&&tiles[8].url.equalsIgnoreCase("Pipe_Vertical")&&tiles[12].url.equalsIgnoreCase("Pipe_01")&&tiles[13].url.equalsIgnoreCase("Pipe_Horizontal"))
                    return true;
                else
                  return false;  
            case"level3.txt"://Coorect tile lineup for level 3.
                if(tiles[4].url.equalsIgnoreCase("Pipe_Vertical")&&tiles[8].url.equalsIgnoreCase("Pipe_Vertical")&&tiles[12].url.equalsIgnoreCase("Pipe_01")&&tiles[13].url.equalsIgnoreCase("Pipe_Horizontal"))
                    return true;
                else
                  return false;  
            case"level4.txt"://Coorect tile lineup for level 4.
                if(tiles[8].url.equalsIgnoreCase("Pipe_01")&&tiles[9].url.equalsIgnoreCase("Pipe_Horizontal")&&tiles[10].url.equalsIgnoreCase("Pipe_Horizontal")&&tiles[11].url.equalsIgnoreCase("Pipe_00"))
                    return true;
                else
                  return false;  
            case"level5.txt"://Coorect tile lineup for level 5.
                if(tiles[4].url.equalsIgnoreCase("Pipe_Vertical")&&tiles[9].url.equalsIgnoreCase("Pipe_Horizontal")&&tiles[10].url.equalsIgnoreCase("Pipe_Horizontal")&&tiles[11].url.equalsIgnoreCase("Pipe_00"))
                    return true;
                else
                  return false;         
            default:
                return false;
                    
        }   
        
    }
}
}
