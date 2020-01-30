package e.aman.videoonimage;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.widget.Toast;


import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;

import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Collection;


public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener
{
    private ExternalTexture externalTexture ;
    private MediaPlayer mediaPlayer ;
    private CustomArFragment arFragment ;
    private Scene scene ;
    private ModelRenderable renderable ;
    private boolean is_image_detected = false;


    private static final String url = "http://techslides.com/demos/sample-videos/small.mp4";

 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        externalTexture = new ExternalTexture();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setSurface(externalTexture.getSurface());
        mediaPlayer.setLooping(true);

     try {
         mediaPlayer.setDataSource(url);
         mediaPlayer.setOnPreparedListener(MainActivity.this);
         mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
         mediaPlayer.prepare();
     } catch (IOException e) {
         e.printStackTrace();
     }



        ModelRenderable.builder().setSource(this , Uri.parse("video_screen.sfb")).
                build().thenAccept(modelRenderable ->
        {
            modelRenderable.getMaterial().setExternalTexture("videoTexture" , externalTexture);
            modelRenderable.getMaterial().setFloat4("keyColor" , new Color(0.01843f , 1f , 0.098f));
            renderable = modelRenderable ;
        });


        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        scene = arFragment.getArSceneView().getScene();

        scene.addOnUpdateListener(this::onUpdate);



    }

    private void onUpdate(FrameTime frameTime)
    {
        if(is_image_detected)
        {
            return;
        }
        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        for(AugmentedImage image : augmentedImages)
        {
            if(image.getTrackingState() == TrackingState.TRACKING)
            {
                if(image.getName().equals("earth"))
                {
                    Toast.makeText(getApplicationContext() , "Hit" , Toast.LENGTH_SHORT).show();
                    is_image_detected = true;
                    playVideo(image.createAnchor(image.getCenterPose()) , image.getExtentX() , image.getExtentZ());
                    return;
                }
            }
        }
    }

    private void playVideo(Anchor anchor, float extentX, float extentZ)
    {

        mediaPlayer.start();

        AnchorNode anchorNode = new AnchorNode(anchor);

        externalTexture.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture ->
        {
            anchorNode.setRenderable(renderable);
            externalTexture.getSurfaceTexture().setOnFrameAvailableListener(null);
        });

        anchorNode.setWorldScale(new Vector3(extentX , 1f , extentZ));
        scene.addChild(anchorNode);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
