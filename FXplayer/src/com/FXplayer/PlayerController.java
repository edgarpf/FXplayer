package com.FXplayer;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.*;
import javafx.application.Platform;
import javafx.beans.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;

public class PlayerController implements Initializable {

    private String lastDirectory;

    @FXML
    private VBox controls;

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
    private HBox player;

    @FXML
    private VBox root;

    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Util.controls != null) {
            returnToWindowMode();
        }

        adjustSize();
    }

    private void adjustSize() {
        double height = player.getPrefHeight();
        double width = 1067 * height / 600;
        mediaView.setFitHeight(height);
        mediaView.setFitWidth(width);
    }

    private void returnToWindowMode() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                syncronizeControl();
            }
        });

        mediaPlayer = Util.mediaView.getMediaPlayer();
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.play();
        mediaView.setDisable(false);

    }

    private void syncronizeControl() {
        buttonPlay.setDisable(false);

        if (mediaPlayer.getStatus() == Status.PLAYING) {
            buttonPlay.setStyle("-fx-graphic: url(\"pause.png\");");
        } else {
            mediaPlayer.pause();
        }
        buttonRepeat.setDisable(false);
        if (Util.repeat) {
            buttonRepeat.setStyle("-fx-background-color: #C3C3C3;");
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
            });
        }

        buttonStop.setDisable(false);
        slider.setDisable(false);
        slider.setMax(mediaPlayer.getTotalDuration().toSeconds());
        totalTime.setText(Util.getPrettyDurationString(mediaPlayer.getTotalDuration().toSeconds()));
        volume.setDisable(false);
        volume.setValue(mediaPlayer.getVolume());

        root.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.SPACE) {
                buttonPlay.fire();
            }
        });

        mediaPlayer.currentTimeProperty().addListener((Observable observable) -> {
            if (!slider.isValueChanging()) {
                slider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                currentTime.setText(Util.getPrettyDurationString(mediaPlayer.getCurrentTime().toSeconds()));
            }
        });

        slider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (!slider.isValueChanging()) {
                double ct = mediaPlayer.getCurrentTime().toSeconds();
                if (Math.abs(ct - newValue.doubleValue()) > MIN_CHANGE) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                    currentTime.setText(Util.getPrettyDurationString(mediaPlayer.getCurrentTime().toSeconds()));
                }
            }
        });

        volume.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            mediaPlayer.setVolume(volume.getValue());
        });

    }

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a Media File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Media Types", "*.mp4", "*.mp3"),
                new FileChooser.ExtensionFilter("MP4", "*.mp4"),
                new FileChooser.ExtensionFilter("MP3", "*.mp3")
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

        if (Util.repeat) {
            Util.repeat = false;
            buttonRepeat.setStyle("-fx-background-color: transparent;");
        }

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

        root.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.SPACE) {
                buttonPlay.fire();
            }
        });

        mediaPlayer.setOnReady(() -> {
            String title = new File(uri).getName().replace("%20", " ");
            ((Stage) mediaView.getScene().getWindow()).setTitle(title);
            Util.title = title;
            totalTime.setText(Util.getPrettyDurationString(mediaPlayer.getTotalDuration().toSeconds()));
            slider.setDisable(false);
            slider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            buttonStop.setDisable(false);
            buttonPlay.setDisable(false);
            buttonRepeat.setDisable(false);
            buttonPlay.setStyle("-fx-graphic: url(\"pause.png\");");
        });

        mediaPlayer.currentTimeProperty().addListener((Observable observable) -> {
            if (!slider.isValueChanging()) {
                slider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                currentTime.setText(Util.getPrettyDurationString(mediaPlayer.getCurrentTime().toSeconds()));
            }
        });

        slider.valueChangingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //if (!slider.isValueChanging()) {
            double ct = mediaPlayer.getCurrentTime().toSeconds();
            if (Math.abs(ct - slider.getValue()) > MIN_CHANGE) {
                mediaPlayer.seek(Duration.seconds(slider.getValue()));
                currentTime.setText(Util.getPrettyDurationString(mediaPlayer.getCurrentTime().toSeconds()));
            }
            //}
        });

        slider.valueProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
            double ct = mediaPlayer.getCurrentTime().toSeconds();
            if (Math.abs(ct - slider.getValue()) > MIN_CHANGE) {
                mediaPlayer.seek(Duration.seconds(slider.getValue()));
                currentTime.setText(Util.getPrettyDurationString(mediaPlayer.getCurrentTime().toSeconds()));
            }
        });

        volume.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            mediaPlayer.setVolume(volume.getValue());
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
        Duration d = new Duration(slider.getValue());
        mediaPlayer.seek(d);
        currentTime.setText(Util.getPrettyDurationString(slider.getValue()));
    }

    @FXML
    public void repeat() {
        if (!Util.repeat) {
            buttonRepeat.setStyle("-fx-background-color: #C3C3C3;");
            Util.repeat = true;
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
            });
        } else {
            buttonRepeat.setStyle("-fx-background-color: transparent;");
            Util.repeat = false;
            mediaPlayer.setOnEndOfMedia(() -> {
            });
        }
    }

    @FXML
    public void changeViewMode(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                changeToFullScreenMode();
                Util.root = root;
                Util.controls = controls;
                Util.mediaView = mediaView;
            }
        }
    }

    private void changeToFullScreenMode() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        root.getChildren().add(mediaView);
        root.getChildren().add(controls);
        final Scene scene = new Scene(root, 960, 540);
        mediaView.setPreserveRatio(true);
        scene.setFill(Color.BLACK);
        Main.s.setScene(scene);
        Main.s.setFullScreen(true);
        controls.setTranslateY(Main.s.getHeight() - 80);
        final DoubleProperty width = mediaView.fitWidthProperty();
        final DoubleProperty height = mediaView.fitHeightProperty();
        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
        Main.s.show();

        controls.setOpacity(0);

        controls.setOnMouseEntered(e -> {
            controls.setOpacity(1);
        });

        controls.setOnMouseExited(e -> {
            controls.setOpacity(0);
        });

        mediaView.setOnMouseClicked((MouseEvent e) -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                if (e.getClickCount() == 2) {
                    try {
                        Scene s = Main.s.getScene();
                        Parent r = FXMLLoader.load(getClass().getResource("Player.fxml"));
                        Scene scene1 = new Scene(r);
                        double h = Main.s.getHeight();
                        double w = Main.s.getWidth();
                        Main.s.setScene(scene1);
                        Main.s.getIcons().add(new Image("icon.png"));
                        Main.s.setTitle(Util.title);
                        Main.s.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
                        Main.s.setMaximized(true);
                        Main.s.setHeight(h);
                        Main.s.setWidth(w);
                    } catch (IOException ex) {
                        Logger.getLogger(PlayerController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
    }
}
