import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Includes specila effects for the movemenet of the machine
 */
public class ExtraEffects {
    private int indexImageStart; //start number of an image drill_<indexImageStart>.png
    private int indexImageEnd; // end number of an image    drill_<indexImageEnd>.png
    private Timeline animation;
    private String animationName;
    public ExtraEffects(String animationName, ImageView drillMachine){
        this.animationName = animationName;
        switch(animationName){
            case "UP":
                indexImageStart = 23;
                indexImageEnd = 27;
                break;
            case "RIGHT":
                indexImageStart = 55;
                indexImageEnd = 60;
                break;
            case "LEFT":
                indexImageStart = 8;
                indexImageEnd = 1;
                break;
            case "DOWN":
                indexImageStart = 41;
                indexImageEnd = 44;
                break;
            default:
                return;
        }
        drillMachine.setImage(new Image(Configurations.IMAGE_FOLDER_SOURCE_MACHINE + "drill_" + (indexImageStart < 10 ? "0" + indexImageStart : indexImageStart) + ".png")); // so the image changes before the movement being started
        animate(drillMachine);
    }

    /**
     * Runs some animation for a movement of the machine
     * @param drillMachine
     */
    private void animate(ImageView drillMachine){
        animation = new Timeline(
        new KeyFrame(Duration.millis(40), event -> {
            indexImageStart = indexImageStart + (indexImageEnd > indexImageStart ? 1 : -1);
            drillMachine.setImage(new Image(Configurations.IMAGE_FOLDER_SOURCE_MACHINE + "drill_" + (indexImageStart < 10 ? "0" + indexImageStart : indexImageStart) + ".png"));
            if(indexImageStart == indexImageEnd){
                interrupt();
            }
        })
        );
        animation.setCycleCount(Math.abs(indexImageEnd - indexImageStart));
        animation.play();
    }
    /**
     * Stops the current animation in play
     */
    public void interrupt(){
        animation.stop();
    }

    /**
     * Gets the animation name which is the key pressed on the last or the current animation played
     * @return the key pressed on the last or the current animation played
     */
    public String getAnimationName(){
        return animationName;
    }
}
