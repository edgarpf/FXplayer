package com.FXplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

    private String lastDirectory;
    
    @FXML
    private MediaView mediaView;

    private MediaPlayer mediaPlayer;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    public void openFile() {
        
        
        FileChooser fileChooser = new FileChooser();        
        
        if(lastDirectory != null)
            fileChooser.setInitialDirectory(new File(lastDirectory));
                    
        File file = fileChooser.showOpenDialog(null);    
        lastDirectory = file.getParent();
        
        if (file != null)
            initPlayer(file.toURI().toString());
        
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
                ((Stage)mediaView.getScene().getWindow()).setTitle(new File(uri).getName());
            }
        });
    }
}
