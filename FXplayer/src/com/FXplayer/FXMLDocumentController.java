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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
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
                ((Stage) mediaView.getScene().getWindow()).setTitle(new File(uri).getName().replace("%20", " "));
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

}
