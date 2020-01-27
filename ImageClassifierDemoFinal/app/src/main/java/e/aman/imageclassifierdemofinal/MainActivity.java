package e.aman.imageclassifierdemofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;


import java.util.Collection;


public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener
{

    private Button animate_button;
    public CustomArFragment arFragment;
    int done = 0 ;

    CreateModel createModel ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createModel = new CreateModel(this);



        animate_button = (Button)findViewById(R.id.animate_button);
  arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this);


    }

    @Override
    public void onUpdate(FrameTime frameTime)
    {
        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);
        for(AugmentedImage image : images)
        {
            if(done == 0)
            {
                if(image.getTrackingState() == TrackingState.TRACKING)
                {
                    if(image.getName().equalsIgnoreCase("image"))
                    {
                        done = 1 ;

                        Anchor anchor = image.createAnchor(image.getCenterPose());
                        createModel.create3DModel(anchor ,arFragment ,animate_button);
                    }
                }
            }

        }
    }


    public void setupDatabase(Config config, Session session) {
        Bitmap image_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.plane);
        AugmentedImageDatabase database = new AugmentedImageDatabase(session);
        database.addImage("image", image_bitmap);
        config.setAugmentedImageDatabase(database);
    }



}
