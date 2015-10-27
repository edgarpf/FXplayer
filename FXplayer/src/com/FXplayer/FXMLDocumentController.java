package com.FXplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.*;
import javafx.beans.value.*;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Duration;

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
    private Slider volume;

    @FXML
    private MediaView mediaView;

    @FXML
    private Label totalTime;

    @FXML
    private Label currentTime;

    @FXML
    public HBox player;

    @FXML
    public VBox root;

    @FXML
    private Text subtitle1;

    @FXML
    private Text subtitle2;

    @FXML
    private Text subtitle3;

    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        double height = player.getPrefHeight();
        double width = 1067 * height / 600;

        mediaView.setFitHeight(height);
        mediaView.setFitWidth(width);        
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

        if (lastDirectory != null) {
            fileChooser.setInitialDirectory(new File(lastDirectory));
        }

        File file = fileChooser.showOpenDialog(null);
        lastDirectory = file.getParent();

        if (file != null) {
            mediaView.setDisable(false);
            initPlayer(file);
        }
    }
   
    private void initPlayer(File file) {
        String uri = file.toURI().toString();
        if (uri == null) {
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer = null;
        }

        prepareMedia(uri);        
        setComponentEvents(uri);
    }

    private void prepareMedia(String uri) {
        Media media = new Media(uri);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setVolume(volume.getValue());
        volume.setDisable(false);
    }

    private static final double MIN_CHANGE = 0.5;
    
    private void setComponentEvents(String uri) {
        
        root.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.SPACE) {
                    buttonPlay.fire();
                }
            }
        });
        
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
                if (!slider.isValueChanging()) {
                    double ct = mediaPlayer.getCurrentTime().toSeconds();
                    if (Math.abs(ct - newValue.doubleValue()) > MIN_CHANGE) {
                        mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                        currentTime.setText(Util.getPrettyDurationString(mediaPlayer.getCurrentTime().toSeconds()));
                    }
                }
            }
        });

        volume.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(volume.getValue());
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
    
    private boolean repeat = false;
    
    @FXML
    public void repeat()
    {
        if(!repeat)
        {
            buttonRepeat.setStyle("-fx-background-color: #C3C3C3;");
            repeat = true;
            //buttonRepeat.setPrefHeight(41);
            //buttonRepeat.setPrefWidth(52);
            
            mediaPlayer.setOnEndOfMedia(new Runnable() {

                @Override
                public void run() {
                    System.out.println("chegou ao final");
                    mediaPlayer.seek(Duration.ZERO);
                }
            });
        }
        
        else
        {
            buttonRepeat.setStyle("-fx-background-color: transparent;");
            repeat = false;
            mediaPlayer.setOnEndOfMedia(new Runnable() {

                @Override
                public void run() {                    
                }
            });
        }
    }
}
