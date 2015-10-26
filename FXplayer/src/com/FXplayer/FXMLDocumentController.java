package com.FXplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;

public class FXMLDocumentController implements Initializable {

    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();        
        File file = fileChooser.showOpenDialog(null);        
        if (file != null) {
            //mediaView.
            initPlayer(file.toURI().toString());
        }
    }
    
    private void initPlayer (String uri) {
        if (uri == null)
            return;
        
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        Media media = new Media(uri);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaView.setMediaPlayer(mediaPlayer);
        
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
            }
        });
    }
}
