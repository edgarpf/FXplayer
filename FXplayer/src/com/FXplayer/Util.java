
package com.FXplayer;

import javafx.scene.layout.*;
import javafx.scene.media.MediaView;


public class Util {
    
    public static boolean repeat = false;
    public static VBox root;
    public static VBox controls;
    public static MediaView mediaView;
    public static String title;
    
    public static String getPrettyDurationString(Double duration)
    {
        long totalSec = Math.round(duration);
        long hour = totalSec/3600;
        long min = (totalSec - 3600*hour)/60;
        long seg = (totalSec - 3600*hour - 60*min);
        
        String prettyHour = (hour < 10 ? "0" + hour : "" + hour);
        String prettyMin = (min < 10 ? "0" + min : "" + min);
        String prettySeg = (seg < 10 ? "0" + seg : "" + seg);
        
        return prettyHour + ":" + prettyMin + ":" + prettySeg;
    }
}
