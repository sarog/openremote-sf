package org.openremote.demo.rtpstream;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Very simple demonstration of playing a video via RTSP on Android.
 * 
 * This is very unlikely to successfully play a video stream on
 * an emulator.
 * 
 * So far, it has been very difficult to troubleshoot why certain
 * streams won't play.  Typically, a dialog box will show up
 * with "Sorry, this video cannot be played."
 * 
 * @author Andrew D. Ball <aball@osintegrators.com>
 */
public class PlayRTPStream extends Activity {	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        // This stream won't play on my Motorola Cliq (running Android 2.1).
        // video codec: H.264, audio codec: mpeg4-generic
        // String videoUrl = "rtsp://video3.americafree.tv/AFTVComedyH2641000.sdp";
        
        // This stream does play on my Motorola Cliq (running Android 2.1).
        // video codec: MP4V-ES, audio codec: MP4A-LATM
        // String videoUrl = "rtsp://video3.multicasttech.com/AFTVComedy3GPP96.sdp";
        
        // video codec: H.264, audio codec: MPEG4-GENERIC
        String videoUrl = "rtsp://streaming1.osu.edu/media2/ufsap/ufsap.mov";
        
        TextView videoUrlLabel = (TextView) findViewById(R.id.videoUrlLabel);
        videoUrlLabel.setText(videoUrl);
                
        VideoView vv = (VideoView) findViewById(R.id.videoView);
        
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(vv);
        vv.setMediaController(mediaController);

        Uri videoUri = Uri.parse(videoUrl);        
        vv.setVideoURI(videoUri);
        vv.start();
    }
}