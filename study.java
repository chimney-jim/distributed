import java.io.* ;
import java.lang.*;
import java.util.*;
import java.net.*;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.NativeLibrary;

class study{
                          
    public static void main(String[] args){

        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName, LibVlc.class);

        String media = args[0];
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(args);
        FullScreenStrategy fullScreenStrategy = new DefaultFullScreenStrategy(mainFrame);
        EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer(fullScreenStrategy);
       
        Canvas canvas = new Canvas();
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        mediaPlayer.setVideoSurface(videoSurface);

        String[] mediaOptions = {};
        mediaPlayer.playMedia(media, mediaOptions);
    }
}
