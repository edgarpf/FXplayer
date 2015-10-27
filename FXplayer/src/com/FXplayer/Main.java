package com.FXplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Player.fxml"));
        
        Scene scene = new Scene(root);       
        stage.setScene(scene);
        stage.getIcons().add(new Image("icon.png"));
        stage.setTitle("FXplayer");
        stage.setMaximized(true); 
        stage.setMinHeight(550);
        stage.setMinWidth(600);
        //stage.setFullScreen(true);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
