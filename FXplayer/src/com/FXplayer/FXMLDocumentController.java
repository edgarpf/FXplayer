package com.FXplayer;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.AudioInfo;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncoderProgressListener;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.MultimediaInfo;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;

public class FXMLDocumentController implements Initializable {

    private String lastDirectory;

    @FXML
    private Button buttonPlay;

    @FXML
    private Button buttonStop;

    @FXML
    private Button buttonRepeat;

    @FXML
    private Slider slider;

    @FXML
    private MediaView mediaView;

    @FXML
    private Label totalTime;

    @FXML
    private Label currentTime;

    @FXML
    public HBox player;

    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //mediaView.setFitHeight(mediaView.getScene().getHeight() - 45);
        //mediaView.setFitWidth(mediaView.getScene().getWidth() - 45);
        //player.setMinHeight(player.getScene().getHeight()-45);
        player.setPrefHeight(600);
    }

    @FXML
    public void openFile() throws IllegalArgumentException, EncoderException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Media File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Media Types", "*.mkv", "*.mp4", "*.mp3", "*.ogg"),
                new FileChooser.ExtensionFilter("MKV", "*.mkv"),
                new FileChooser.ExtensionFilter("MP4", "*.mp4"),
                new FileChooser.ExtensionFilter("MP3", "*.mp3"),
                new FileChooser.ExtensionFilter("OGG", "*.ogg")
        );

        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(new File(lastDirectory));
        }

        File file = fileChooser.showOpenDialog(null);
        lastDirectory = file.getParent();

        if (file != null) {
            //MultimediaInfo mi = new MultimediaInfo();                                    
            String ext = FilenameUtils.getExtension(file.getPath());
            /*
             Encoder e = new Encoder();
            
             MultimediaInfo mi = e.getInfo(file);
             AudioInfo ai = mi.getAudio();
             System.out.println(ai.getDecoder());            
             String[] s = e.getSupportedDecodingFormats();
             String[] s1 = e.getSupportedEncodingFormats();
             String[] s3 = e.getAudioEncoders();
             String[] s4 = e.getVideoEncoders();
             String[] s5 = e.getAudioDecoders();
             */
            if (ext.equals("ogg") || ext.equals("mkv")) {
                /*
                 EncodingAttributes ea = new EncodingAttributes();
                 ea.setFormat("mp3"); 
                
                 AudioAttributes aa = new AudioAttributes();
                 aa.setCodec("pcm_alaw");
                 aa.setVolume(256);
                 ea.setAudioAttributes(aa);
                 ea.setVideoAttributes(null);                
                 e.encode(file,file,ea, new EncoderProgressListener() {

                 @Override
                 public void sourceInfo(MultimediaInfo mi) {
                 }

                 @Override
                 public void progress(int i) {
                 System.out.println("" + i);
                 }

                 @Override
                 public void message(String string) {
                 }
                 });
                 //initPlayer(f.toURI().toString());
                 */
            }

            initPlayer(file);
        }
    }

    private static final double MIN_CHANGE = 0.5 ;
    
    private void initPlayer(File file) {
        String uri = file.toURI().toString();
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
                ((Stage) mediaView.getScene().getWindow()).setTitle(new File(uri).getName().replace("%20", " "));
                totalTime.setText(Util.getPrettyDurationString(mediaPlayer.getTotalDuration().toSeconds()));
                slider.setDisable(false);
                slider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                buttonStop.setDisable(false);
                buttonPlay.setDisable(false);
                buttonRepeat.setDisable(false);
                buttonPlay.setStyle("-fx-graphic: url(\"pause.png\");");
            }
        });

        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                if (!slider.isValueChanging()) {
                    slider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    currentTime.setText(Util.getPrettyDurationString(mediaPlayer.getCurrentTime().toSeconds()));
                }
            }
        });

        slider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (! slider.isValueChanging()) {
                double ct= mediaPlayer.getCurrentTime().toSeconds();
                if (Math.abs(ct - newValue.doubleValue()) > MIN_CHANGE) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));                                        
                    currentTime.setText(Util.getPrettyDurationString(mediaPlayer.getCurrentTime().toSeconds()));
                }
            }
            }
        });

        slider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue)
                {
                    mediaPlayer.seek(Duration.seconds(slider.getValue()));
                    currentTime.setText(Util.getPrettyDurationString(slider.getValue()));
                }
            }
        });
    }

    @FXML
    public void changeMouseIconToHand() {
        mediaView.getScene().setCursor(Cursor.HAND);
    }

    @FXML
    public void changeMouseIconToPointer() {
        mediaView.getScene().setCursor(Cursor.DEFAULT);
    }

    @FXML
    public void stop() {

        mediaPlayer.stop();
        buttonPlay.setStyle("-fx-graphic: url(\"play.png\");");
        slider.setValue(0);

    }

    @FXML
    public void pause() {
        boolean playing = mediaPlayer.getStatus().equals(Status.PLAYING);

        if (playing) {
            mediaPlayer.pause();
            buttonPlay.setStyle("-fx-graphic: url(\"play.png\");");
        } else {
            mediaPlayer.play();
            buttonPlay.setStyle("-fx-graphic: url(\"pause.png\");");
        }
    }

    @FXML
    public void changeTime() {
        System.out.println("hahaha");
        Duration d = new Duration(slider.getValue());
        mediaPlayer.seek(d);
        currentTime.setText(Util.getPrettyDurationString(slider.getValue()));
    }
}
