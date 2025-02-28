import java.math.RoundingMode;
import java.text.DecimalFormat;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This is a project of a mining game inspired from : Motherload Game
 * @author Mohamed Yahya Mansouri
 * @version 1.0
 */
public class Main extends Application {
    private boolean gameOver = false;
    private boolean flying = false;
    private double moneyBank;
    private double collectedHaul;
    private Timeline timelineFuelDisplayer;
    private Timeline timelinePhysics;

    private void gameOver(String type, Pane pane, double moneyBank){
        pane.getChildren().clear();
        Text gameOver = new Text("Game Over".toUpperCase());
        gameOver.setFont(Font.font("Arial", 50));
        gameOver.setFill(Color.WHITE);
        gameOver.setX((pane.getWidth() - gameOver.getBoundsInLocal().getWidth()) / 2);
        gameOver.setY((pane.getHeight() - gameOver.getBoundsInLocal().getHeight()) / 2);
        pane.getChildren().add(gameOver);
        if(type == "fuel"){
            Text collectedMoneyOutput = new Text("Collected Money: " + (moneyBank == Math.round(moneyBank) ? new DecimalFormat("0.#").format(moneyBank) : moneyBank));
            collectedMoneyOutput.setFont(Font.font("Arial", 50));
            collectedMoneyOutput.setFill(Color.WHITE);
            collectedMoneyOutput.setX((pane.getWidth() - collectedMoneyOutput.getBoundsInLocal().getWidth()) / 2);
            collectedMoneyOutput.setY((pane.getHeight() - collectedMoneyOutput.getBoundsInLocal().getHeight()) / 2 + gameOver.getBoundsInLocal().getHeight());
            pane.setBackground(new Background(new BackgroundFill(Color.rgb(2,98,4), null, null)));
            pane.getChildren().add(collectedMoneyOutput);
        }else{
            moneyBank = 0;
            collectedHaul = 0;
            pane.setBackground(new Background(new BackgroundFill(Color.rgb(141,0,2), null, null)));
        }
    }
    @Override
    /**
     * Starts creating the game initializing everything
     * @param primaryStage is the stage of the game
     */
    public void start(Stage primaryStage){
        Pane pane = new Pane();
        VBox info = new VBox();
        Environment environment = new Environment();
        Machine machine = new Machine(Configurations.WINDOW_WIDTH, Configurations.WINDOW_HEIGHT, Configurations.GROUND_START_Y);
        Group blocks = environment.getUnderground();
        Rectangle sky = environment.getSky();
        Label haul = new Label("Haul: " + (int)collectedHaul); //so it starts 0 not 0.0
        Label money = new Label("Money: " + (int)moneyBank); //so it starts 0 not 0.0
        Label fuelLabelTitle = new Label("Fuel: " + machine.getFuel());   

        pane.setStyle("-fx-background-color:#A2522D"); //Undergound color behind the blocks
        pane.getChildren().add(sky);
        pane.getChildren().add(blocks);

        haul.setFont(Configurations.TOP_TEXT_LABEL_FONT);
        haul.setTextFill(Color.WHITE);
        money.setFont(Configurations.TOP_TEXT_LABEL_FONT);
        money.setTextFill(Color.WHITE);
        
        Scene scene = new Scene(pane, Configurations.WINDOW_WIDTH, Configurations.WINDOW_HEIGHT);
        
        // MACHINE CODE START
        timelinePhysics = new Timeline(
            new KeyFrame(Duration.millis(Configurations.WORLD_ANIMATION_DURATION), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e){
                    if(!flying){    //gravity is always applied except when flying
                        machine.gravity(blocks);
                    }
                    scene.setOnKeyPressed(event -> { //CHANGE THIS ITS WRONG TO SET IT LIKE THIS
                        if(event.getCode() == KeyCode.UP){
                            flying = true;
                        }else{
                            flying = false;
                        }
                    
                        String response = "";   //response from pressing the keyboard to handle lava and valuable blocks
                        if(!gameOver)
                            try{
                                response = machine.control(blocks, event.getCode().toString(), environment.getSelectedValuables());
                            } catch(IllegalArgumentException error){
                                System.out.println(error.getMessage());
                            }
                        switch(response){
                            case "lava":
                                gameOver = true; 
                                machine.setFuel(0);
                                gameOver(response, pane, 0);
                                timelinePhysics.stop();
                                break;
                            case "valuable":
                                moneyBank += environment.getMineralInfo(machine.getOldBlockName(), 1); //get money value for the corresponsing block
                                collectedHaul += environment.getMineralInfo(machine.getOldBlockName(), 2);  //get weight value for the corresponding block
                                haul.setText("Haul: " + (collectedHaul == Math.round(collectedHaul) ? new DecimalFormat("0.#").format(collectedHaul) : collectedHaul));
                                money.setText("Money: " + (moneyBank == Math.round(moneyBank) ? new DecimalFormat("0.#").format(moneyBank) : moneyBank));
                                break;
                        }
                        });
                        scene.setOnKeyReleased(event -> {   //when key up is released => not flying
                            if(event.getCode() == KeyCode.UP){
                                flying = false;
                            }
                        });
                }
            })
        );
        timelinePhysics.setCycleCount(Timeline.INDEFINITE);
        timelinePhysics.play();

        fuelLabelTitle.setFont(Configurations.TOP_TEXT_LABEL_FONT);
        fuelLabelTitle.setTextFill(Color.WHITE);
        timelineFuelDisplayer = new Timeline(
            new KeyFrame(Duration.millis(Configurations.FUEL_DECREASE_DURATION), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e){
                    DecimalFormat df = new DecimalFormat("#.###");
                    df.setRoundingMode(RoundingMode.DOWN);
                    fuelLabelTitle.setText("Fuel: " + df.format(machine.getFuel()));
                    if(gameOver){
                        timelineFuelDisplayer.stop(); 
                    }
                    if(machine.getFuel() <= 0){
                        gameOver("fuel", pane, moneyBank);
                        timelineFuelDisplayer.stop(); 
                    }
                }
            })
        );
        timelineFuelDisplayer.setCycleCount(Timeline.INDEFINITE);
        timelineFuelDisplayer.play();
        info.getChildren().add(fuelLabelTitle);
        info.getChildren().add(haul);
        info.getChildren().add(money);
        pane.getChildren().add(info);
        pane.getChildren().add(machine.getDrillMachine());
        primaryStage.setTitle("HU-LOAD");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /**
     * The main method for exeuction
     * @param args for any command arguments
     */
    public static void main(String[] args) { 
        launch(args);
    }
}
