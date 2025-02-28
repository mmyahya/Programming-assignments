import javafx.scene.text.Font;

/**
 * This class for the CONSTANT configurations or parameters used for the rest of the classes.
 * This class was needed for easier overlook
 */
public final class Configurations {
    public static final String[] OBSTACLES = {"obstacle_01.png","obstacle_02.png","obstacle_03.png"};
    public static final String[] SOIL = {"soil_01.png","soil_02.png","soil_03.png","soil_04.png","soil_05.png"};
    public static final String[] LAVA = {"lava_01.png","lava_02.png","lava_03.png"};

    public static final String IMAGE_FOLDER_SOURCE_UNDERGROUND = "assets/underground/";
    public static final String IMAGE_FOLDER_SOURCE_MACHINE = "assets/drill/";
    public static final String FIRST_ROW_IMAGES = "top_02.png";

    public static final double MINERAL_BLOCKS_PERCENTAGE = 5; //This must be increased in case of decreasing the height and width
    public static final double LAVA_BLOCKS_PERCENTAGE = 2;
    public static final double STONE_BLOCKS_MAXIMUM_PERCENTAGE = 2; //maximum because stone might not exist inside the blocks
    public static final double INITIAL_X = 250;
    public static final double THRESHOLD = 5; //to set limits for the machine not to pass borders
    public static final double FUEL_DECREASE_PER_MOVE = 100;
    public static final double FUEL_DECREASE_PER_DRILL = 150;
    public static final double FUEL_DECREASE_WITH_TIME = 0.001;
    public static final double INITIAL_FUEL_VALUE = 20000;

    public static final int GROUND_START_Y = 200;
    public static final int WINDOW_HEIGHT = 800;
    public static final int WINDOW_WIDTH = 1100;
    public static final int BLOCK_WIDTH = 50;
    public static final int BLOCK_HEIGHT = 50;
    public static final int MINIMUM_TYPE_MINERALS_NUMBER = 4;
    public static final int WORLD_ANIMATION_DURATION = 100;
    public static final int FUEL_DECREASE_DURATION = 1;

    public static final Font TOP_TEXT_LABEL_FONT = new Font(20);
    
}
