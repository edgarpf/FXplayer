package com.FXplayer;

import it.sauronsoftware.jave.Encoder;
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
import org.apache.commons.io.FilenameUtils;

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
        fileChooser.setTitle("Choose a Media File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Media Types", "*.mkv", "*.mp4", "*.mp3", "*.ogg"),
                new FileChooser.ExtensionFilter("MKV", "*.mkv"),
                new FileChooser.ExtensionFilter("MP4", "*.mp4"),
                new FileChooser.ExtensionFilter("MP3", "*.mp3"),
                new FileChooser.ExtensionFilter("OGG", "*.ogg")
        );

        //Encoder e = new Encoder();
        
        
        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(new File(lastDirectory));
        }

        File file = fileChooser.showOpenDialog(null);
        lastDirectory = file.getParent();

        if (file != null) {
            String ext = FilenameUtils.getExtension(file.getPath());
            if (ext.equals("ogg")) {

            }

            initPlayer(file.toURI().toString());
        }
    }

    private void initPlayer(String uri) {
        if (uri == null) {
            return;
        }

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
                ((Stage) mediaView.getScene().getWindow()).setTitle(new File(uri).getName());
            }
        });
    }
}
