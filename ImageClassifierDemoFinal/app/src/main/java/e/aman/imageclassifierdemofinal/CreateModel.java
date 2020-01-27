package e.aman.imageclassifierdemofinal;

import android.net.Uri;
import android.widget.Button;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.SkeletonNode;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class CreateModel
{

    MainActivity mainActivity ;
    int count= 0 ;
    private ModelAnimator modelAnimator;


    public CreateModel(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity ;
    }


    public void create3DModel(Anchor anchor , ArFragment arFragment  , Button animate_button)
    {
        ModelRenderable.builder().setSource(mainActivity.getApplicationContext(),Uri.parse("skeletal.sfb"))
                .build().thenAccept(modelRenderable ->
        {

            AnchorNode anchorNode = new AnchorNode(anchor);
            TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
            node.getScaleController().setMaxScale(0.20f);
            node.getScaleController().setMinScale(0.02f);
            node.setRenderable(modelRenderable);
            node.setParent(anchorNode);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            node.select();

            SkeletonNode skeletonNode = new SkeletonNode();
            skeletonNode.setParent(node);
            skeletonNode.setRenderable(modelRenderable);

            arFragment.getArSceneView().getScene().addChild(anchorNode);


            animate_button.setOnClickListener(view -> {animateModel(modelRenderable);});

        });

    }

    private void animateModel(ModelRenderable modelRenderable)
    {
        if(modelAnimator != null && modelAnimator.isRunning())
        {
            modelAnimator.end();
        }


        int animation_count = modelRenderable.getAnimationDataCount();
        if(count == animation_count)
        {
            count = 0 ;
        }
        AnimationData animationData = modelRenderable.getAnimationData(count);
        modelAnimator = new ModelAnimator(animationData , modelRenderable);
        modelAnimator.start();
        count++;
    }

}
