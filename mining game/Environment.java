import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The environment of the game: Underworld and the sky
 */
public class Environment {
    private int blocksNumberX = Configurations.WINDOW_WIDTH / Configurations.BLOCK_WIDTH;
    private int blocksNumberY = (Configurations.WINDOW_HEIGHT - Configurations.GROUND_START_Y) / Configurations.BLOCK_HEIGHT;
    private int numberOfValuableBlocks;  //20
    private int numberOfLavaBlock;
    private int numberOfStoneBlock;

    private String[][] minerals;    //[[name, worth, weight], [name, worth, weight]...]
    private String[] inputLines;

    private ArrayList<String> selectedValuablesTypes = new ArrayList<String>();
    private ArrayList<String> selectedValuableCoordinates = new ArrayList<String>();
    private ArrayList<String> lavaCoordinates = new ArrayList<String>();
    private ArrayList<String> optionalStoneCoordinates = new ArrayList<String>();

    private Group underground = new Group();

    public Environment(){
        Random rand = new Random();
        FileInput input = new FileInput();
        inputLines = input.readFile("assets/atributes_of_valuables.txt", true, true);

        minerals = new String[inputLines.length][3];
        for(int i = 0; i < inputLines.length; i++){     //store all minerals in the input file in minerals String[][]
            minerals[i][0] = inputLines[i].split("\t")[0];  //name
            minerals[i][1] = inputLines[i].split("\t")[1];  //worth
            minerals[i][2] = inputLines[i].split("\t")[2];  //weight
        }
        
        int nbTypesOfMinerals = Math.max(Configurations.MINIMUM_TYPE_MINERALS_NUMBER, rand.nextInt(minerals.length)); //select at least three types of minerals
        numberOfValuableBlocks = (int) Math.max(nbTypesOfMinerals, Math.round((blocksNumberX - 2) * (blocksNumberY - 1) * (Configurations.MINERAL_BLOCKS_PERCENTAGE / 100.0))); //to make sure with low width and height the number of blocks are at least one per type
        numberOfLavaBlock = (int) Math.round((blocksNumberX - 2) * (blocksNumberY - 1) * (Configurations.LAVA_BLOCKS_PERCENTAGE / 100.0));
        numberOfStoneBlock = (int) Math.round((blocksNumberX - 2) * (blocksNumberY - 1) * ((Math.random() * Configurations.STONE_BLOCKS_MAXIMUM_PERCENTAGE) / 100.0));     //from 0 to the maximum percentage
        for(int i = 0; i < nbTypesOfMinerals; i++){ // store the types of selected minerals
            int indexMineral;
            do{
                indexMineral = rand.nextInt(minerals.length);
            }while(selectedValuablesTypes.contains(minerals[indexMineral][0]));
            selectedValuablesTypes.add(minerals[indexMineral][0]);
        }

        int alreadyInlcudedValuableBlocks = 0;
        randomizeCoordinates(selectedValuableCoordinates, numberOfValuableBlocks);
        randomizeCoordinates(lavaCoordinates, numberOfLavaBlock);
        randomizeCoordinates(optionalStoneCoordinates, numberOfStoneBlock);
        for(int y = 0; y < blocksNumberY; y++){     // Generate the environment - Add blocks using solid_blocks_x and solid_blocks_y as coordiantes references
            for(int x = 0; x < blocksNumberX; x++){
                ImageView imageView = new ImageView();
                Image img;
                if(y == 0){ //FIRST ROW OF GROUND
                    img = new Image(Configurations.IMAGE_FOLDER_SOURCE_UNDERGROUND + Configurations.FIRST_ROW_IMAGES);
                    imageView.setImage(img);
                    imageView.getProperties().put("name","soil");
                } else if(selectedValuableCoordinates.contains(x + "-" + y)){
                    if(alreadyInlcudedValuableBlocks < selectedValuablesTypes.size()){  //to make sure that at least one valuable block is included 
                        img = new Image(Configurations.IMAGE_FOLDER_SOURCE_UNDERGROUND + "valuable_" + selectedValuablesTypes.get(alreadyInlcudedValuableBlocks) + ".png");
                        imageView.setImage(img);
                        imageView.getProperties().put("name",selectedValuablesTypes.get(alreadyInlcudedValuableBlocks));
                    }else{  // put any random special block then
                        int index = rand.nextInt(selectedValuablesTypes.size());
                        img = new Image(Configurations.IMAGE_FOLDER_SOURCE_UNDERGROUND + "valuable_" + selectedValuablesTypes.get(index) + ".png");
                        imageView.setImage(img);
                        imageView.getProperties().put("name",selectedValuablesTypes.get(index));
                    }
                    alreadyInlcudedValuableBlocks++;
                    //IF ALL PUT THEN START RANDOM; CHECK FIRST CONDITION BY INCLUDING EVERYTHING
                } else if(lavaCoordinates.contains(x + "-" + y)){ //Lava blocks
                    int index = rand.nextInt(Configurations.LAVA.length);
                    img = new Image(Configurations.IMAGE_FOLDER_SOURCE_UNDERGROUND + Configurations.LAVA[index]);
                    imageView.setImage(img);
                    imageView.getProperties().put("name","lava");
                } else if( // Stone blocks
                    (optionalStoneCoordinates.size() > 0 && optionalStoneCoordinates.contains(x + "-" + y)) || //inside the ground
                    y == blocksNumberY - 1 || //bottom border
                    x == 0 || //left border
                    x == blocksNumberX - 1 // right border
                    ){
                    int index = rand.nextInt(Configurations.OBSTACLES.length);
                    img = new Image(Configurations.IMAGE_FOLDER_SOURCE_UNDERGROUND + Configurations.OBSTACLES[index]);
                    imageView.setImage(img);
                    imageView.getProperties().put("name","stone");
                } else{
                    int index = rand.nextInt(Configurations.SOIL.length);
                    img = new Image(Configurations.IMAGE_FOLDER_SOURCE_UNDERGROUND + Configurations.SOIL[index]);
                    imageView.setImage(img);
                    imageView.getProperties().put("name","soil");
                }
                imageView.setLayoutX(x * img.getWidth()); // convert to normal coordinate system
                imageView.setLayoutY(y * img.getHeight() + Configurations.GROUND_START_Y);
                underground.getChildren().add(imageView);
            }
        }
    }
    /**
     * to randomize the coordinates of valuable, lava, and stone blocks. 1 unit is defined as a block NOT pixels: only for this class
     * these blocks cannot be in the top, bottom, left, or right of the window.
     * 
     * @param blocksStorage is the ArrayList that will store the coordinates of the blocks
     * @param numberOfBlocks is the number of blocks that their coordinates will be randomized
     */
    private void randomizeCoordinates(ArrayList<String> blocksStorage, int numberOfBlocks){
        for(int i = 0; i < numberOfBlocks; i++){
            while(true){
                int x = (int) (Math.random() * (blocksNumberX - 2) + 1); // 0 < x < windowWidth - 50 => blocksNumberX - 2
                int y = (int) (Math.random() * (blocksNumberY - 2) + 1);
                String potentialCoordinates = x + "-" + y;
                if(!selectedValuableCoordinates.contains(potentialCoordinates) && !lavaCoordinates.contains(potentialCoordinates) && !optionalStoneCoordinates.contains(potentialCoordinates)){
                    blocksStorage.add(potentialCoordinates);
                    break;
                }
            }
        }
    }
    /**
     * to get the layout Group or the world(without the sky) of the game
     * @return the layout Group holding all of the blocks
     */
    public Group getUnderground(){
        return underground;
    }
    
    /**
     * gets the selected valuables
     * @return the selected valuables
     */
    public ArrayList<String> getSelectedValuables(){
        return selectedValuablesTypes;
    }

    /**
     * returns the blue rectangle representing the sky
     * @return the sky of the world
     */
    public Rectangle getSky(){
        return (new Rectangle(Configurations.WINDOW_WIDTH, Configurations.GROUND_START_Y + 3, Color.rgb(29,24,115)));
    }

    /**
     * Using the name of a valuable block, this method gets the value or the weight for calculating the money and haul
     * @param name is the name of the block
     * @param infoIndex is the index of the desired information: 1 for money and 2 for weight
     * @return money or weight of a valuable block
     */
    public double getMineralInfo(String name, int infoIndex){
        String[] selectedMineral = new String[3];
        for(String[] mineral: minerals){
            if(mineral[0].equalsIgnoreCase(name)){
                selectedMineral = mineral;
            }
        }
        return Double.parseDouble(selectedMineral[infoIndex]);
    }
}