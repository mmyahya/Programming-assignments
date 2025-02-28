import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * Inlcudes the drilling machine, control, and gravity
 */
public class Machine{
    private double windowWidth;
    private double fuel = Configurations.INITIAL_FUEL_VALUE;
    private boolean obstacle = false;

    private String oldName;

    private ImageView drillMachine;
    private Image image;
    private Timeline timelineFuel;
    private ArrayList<Node> neighboringBlocks;
    private ExtraEffects movementEffects;
    public Machine(double width, double height, int groundStartY){
        this.windowWidth = width;
        image = new Image(Configurations.IMAGE_FOLDER_SOURCE_MACHINE + "drill_01.png");
        drillMachine = new ImageView(image);
        drillMachine.setLayoutX(Configurations.INITIAL_X);
        drillMachine.setLayoutY(groundStartY - 50);
        timelineFuel = new Timeline(
            new KeyFrame(Duration.millis(1), event -> {
                if(fuel > 0){
                    fuel = fuel - Configurations.FUEL_DECREASE_WITH_TIME;
                }else{
                    timelineFuel.stop();
                }
            })
        );
        timelineFuel.setCycleCount(Timeline.INDEFINITE);
        timelineFuel.play();
    }
    /**
     * This method is initiated when a key is pressed: it moves the machine and drills a block if possible by anazlying the neighboring blocks
     * @param blocks is the group layout holding all of the blocks available
     * @param keyPressed is the Keycode of the keyboard
     * @param selectedMinerals is the arraylist that holds all of the valuable blocks
     * @return the name of the block drilled, otherwise an empty string
     */
    public String control(Group blocks, String keyPressed, ArrayList<String> selectedMinerals){
        neighboringBlocks = getNeighboringBlocks(blocks);
        return drill(blocks, moveMachine(blocks, keyPressed), selectedMinerals);
    }

    /**
     * if conditions are met, the machine is displaced to a block and the block is removed while considering special cases such as lava or valuable blocks.
     * @param blocks is the group layout holding all of the blocks available
     * @param moveMade is true is the machine is able to move inside a block
     * @param selectedMinerals is the arraylist that holds all of the valuable blocks
     * @return the name of the block drilled, otherwise an empty string
     */
    private String drill(Group blocks, boolean moveMade, ArrayList<String> selectedMinerals){
        int indexToBeRemoved = getIndexOfBlockToBeDrilled(blocks);
        String name = getBlockName(blocks, indexToBeRemoved);
        if(moveMade && name != ""){
            fuel = fuel - (Configurations.FUEL_DECREASE_PER_DRILL - Configurations.FUEL_DECREASE_PER_MOVE); //100 is already removed when moved the rest is the difference between the drill and the move: 150 - 100 = 50 to substract from fuel if drilled
            blocks.getChildren().remove(indexToBeRemoved);
            if(name == "lava"){
                return "lava";
            }else if(selectedMinerals.contains(name)){
                oldName = name;
                return "valuable";
            }
        }
        return "default";
    }

    /**
     * to get the index of the potential block by comparing the coordinates of the machine with the potential block
     * @param blocks is the group layout holding all of the blocks available
     * @return the index on the potential block to be drilled or -1 if no such block exists
     */
    private int getIndexOfBlockToBeDrilled(Group blocks){
        for(int i = 0; i < blocks.getChildren().size(); i++){
            if((blocks.getChildren().get(i).getLayoutX() + "-" + blocks.getChildren().get(i).getLayoutY()).equalsIgnoreCase(getX() + "-" + getY())){
                return i;
            }
        }
        return -1;
    }

    /**
     * to get the name of the potential block by using the index of the method above
     * @param blocks is the group layout holding all of the blocks available
     * @param index is the index of the potential block to be drilled
     * @return the name of the potential block to be drilled, otherwise an empty string
     */
    private String getBlockName(Group blocks, int index){
        String name = "";
        if(index >= 0){
            name = (String) blocks.getChildren().get(index).getProperties().get("name");
        }
        return name;
    }

    /**
     * displaces the machine and changes the image according to the key pressed. 
     * Even if move is not made, fuel decreases.
     * @param blocks is the group layout holding all of the blocks available
     * @param keyPressed is the Keycode of the keyboard
     * @return true if the machine is displaced => changes position
     */
    private boolean moveMachine(Group blocks, String keyPressed){
        boolean moveMade = false;
        obstacle = preventDrilling(blocks, keyPressed);
        if(thereIsBottomBlock(blocks))  //when falling, only the image of the machine flying should be displayed
            animate(keyPressed);
        if(keyPressed == "UP"){ // FLYING
            animate(keyPressed);
            if(!obstacle && getY() > Configurations.THRESHOLD)
                drillMachine.setLayoutY(getY() - 50);
                moveMade = true;
            fuel = fuel - Math.min(Configurations.FUEL_DECREASE_PER_MOVE, fuel); //just for consistency: to ensure a non-negative value of fuel
        } else if(keyPressed == "DOWN"){
            if(!obstacle)
                drillMachine.setLayoutY(getY() + 50);
                moveMade = true;
            fuel = fuel - Math.min(Configurations.FUEL_DECREASE_PER_MOVE, fuel);

        } else if(keyPressed == "LEFT"){
            if(!obstacle && getX() > 0)
                drillMachine.setLayoutX(getX() - 50);
                moveMade = true;
            fuel = fuel - Math.min(Configurations.FUEL_DECREASE_PER_MOVE, fuel);

        } else if(keyPressed == "RIGHT"){
            if(!obstacle && getX() < windowWidth - 50)
                drillMachine.setLayoutX(getX() + 50);
                moveMade = true;
            fuel = fuel - Math.min(Configurations.FUEL_DECREASE_PER_MOVE, fuel);
        }
        return moveMade;
    }

    /**
     * Animate the movement of a machine and interrupts the previous animation if it exists
     * @param animation is the key pressed on the keyboard
     */
    private void animate(String animation){
        try{
            if(movementEffects.getAnimationName() != animation){
                movementEffects.interrupt(); //to avert lags when directly changing movements during animation
            }
        }
        catch(Exception e){} //in case no animation to interrupt
        finally{
            movementEffects = new ExtraEffects(animation, drillMachine);
        }
    }

    /**
     * checks if there are obstacles
     * OBSTACLES: stone - trying to drill in the air - a block on top of the machine
     * @param blocks is the group layout holding all of the blocks available
     * @param keyPressed is the Keycode of the keyboard
     * @return true if an obstacle is found
     */
    private boolean preventDrilling(Group blocks, String keyPressed){
        for(Node block: neighboringBlocks){
            if(keyPressed == "UP" && block.getLayoutX() == getX() && block.getLayoutY() + 50 == getY()){
                return true;
            }
            if(block.getProperties().get("name") == "stone" || !thereIsBottomBlock(blocks)){
                if(block.getProperties().get("name") == "stone"){
                }
                switch(keyPressed){
                    case "LEFT":
                        if(block.getLayoutX() + 50 == getX() && block.getLayoutY() == getY()){
                            return true;
                        }
                        break;
                    case "RIGHT":
                        if(block.getLayoutX() - 50 == getX() && block.getLayoutY() == getY()){
                            return true;
                        }
                        break;
                }
            }
            if(keyPressed == "DOWN" && block.getProperties().get("name") == "stone" && block.getLayoutX() == getX() && block.getLayoutY() - 50 == getY()){ //needed to seperate this from the above conditions because no need for bottom block check and for less code
                return true;
            }
        }
        return false;
    }

    /**
     * checks if there is a bottom block under the machine
     * @param blocks is the group layout holding all of the blocks available
     * @return true if there is a block under the machine
     */
    private boolean thereIsBottomBlock(Group blocks){
        for(Node block : getNeighboringBlocks(blocks)){
            if(block.getLayoutX() == getX() && block.getLayoutY() - 50 == getY()){
                return true;
            }
        }
        animate("UP");
        return false;
    }

    /**
     * if not bottom block then must fall down => gravity
     * @param blocks is the group layout holding all of the blocks available
     */
    public void gravity(Group blocks){ 
        if(!thereIsBottomBlock(blocks)){
            drillMachine.setImage(new Image(Configurations.IMAGE_FOLDER_SOURCE_MACHINE + "drill_24.png"));
            drillMachine.setLayoutY(drillMachine.getLayoutY() + 50);
            return;
        }
    }
    /**
     * For optimization purposes: only neighboring blocks will be checked instead of checking of the whole blocks
     * This method returns the neighboring blocks according to the position of the machine
     * @return the neighboring blocks in ArrayList
     */
    private ArrayList<Node> getNeighboringBlocks(Group blocks){
        ArrayList<Node> neighborsBlocks = new ArrayList<Node>();
            for(Node block : blocks.getChildren()){
                double blockX = block.getLayoutX();
                double blockY = block.getLayoutY();
                double x = getX();
                double y = getY();
                if(
                    (blockX - 50 == x && blockY == y) ||    //block on the right of the machine
                    (blockX + 50 == x && blockY == y) ||    //block on the left of the machine
                    (blockX == x && blockY - 50 == y) ||    //block in the top of the machine
                    (blockX == x && blockY + 50 == y)){     //block on bottom of the machine
                        neighborsBlocks.add(block);
                    }
            }
            return neighborsBlocks;
    }

    /**
     * to get the block name of the drilled valuable
     * @return the block name of the drilled valuable
     */
    public String getOldBlockName(){
        return oldName;
    }

    /**
     * to get the ImageView of the machine
     * @return the ImageView of the machine
     */
    public ImageView getDrillMachine(){
        return drillMachine;
    }

    /**
     * to get the Y coordinate of the machine
     * @return the Y coordinate of the machine
     */
    public double getY(){
        return drillMachine.getLayoutY();
    }

    /**
     * to get the X coordinate of the machine
     * @return the X coordinate of the machine
     */
    public double getX(){
        return drillMachine.getLayoutX();
    }

    /**
     * to get the fuel left of the machine
     * @return the fuel left of the machine
     */
    public double getFuel(){
        return fuel;
    }
    
    /**
     * Sets the fuel, used when game is over in lava
     * @param fuel is the new value of the fuel
     */
    public void setFuel(double fuel){
        this.fuel = fuel;
    }
}