package com.FXplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class Main extends Application {
    
    public static Stage s;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Player.fxml"));
        
        s = stage;
        Scene scene = new Scene(root);       
        stage.setScene(scene);
        stage.getIcons().add(new Image("icon.png"));
        stage.setTitle("FXplayer");
        stage.setMaximized(true);  
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
