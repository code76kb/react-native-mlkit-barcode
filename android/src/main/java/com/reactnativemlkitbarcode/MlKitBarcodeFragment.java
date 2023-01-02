package com.reactnativemlkitbarcode;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;

public class MlKitBarcodeFragment extends Fragment {
  public static String TAG = "MlKitBarcodeFragment:";

  private MlKitBarcodeDecoder mlKitBarcodeDecoder = null;
  private ReactContext reactContext;
  private int barcodeFormat;

  private PreviewView previewView = null;
  private int WIDTH = 0;
  private int HEIGHT = 0;
  private SimpleDraweeView simpleDraweeView = null;

  public MlKitBarcodeFragment(ThemedReactContext reactContext, int barcodeFormat) {
    this.reactContext = reactContext;
    this.barcodeFormat = barcodeFormat;
  }

  public MlKitBarcodeDecoder getScannerView(){
    return mlKitBarcodeDecoder;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    mlKitBarcodeDecoder = new MlKitBarcodeDecoder(this, reactContext);

    //    RelativeLayout relativeLayout = new RelativeLayout(MlKitBarcodeFragment.this.getContext());
//    relativeLayout.setGravity(Gravity.CENTER);
//    relativeLayout.addView(mlKitBarcodeDecoder.createScannerView());


      View view  = inflater.inflate(R.layout.fragview,container,false);
      previewView = view.findViewById(R.id.previewView);

      simpleDraweeView = view.findViewById(R.id.imgViewGif);

    ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.scanner2).build();
    DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(imageRequest).setAutoPlayAnimations(true).build();
    simpleDraweeView.setController(controller);
    mlKitBarcodeDecoder.createScannerView(previewView);
      return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
     Log.e(TAG, "onViewCreated: W:"+WIDTH+", H:"+HEIGHT);
      mlKitBarcodeDecoder.setBarCodeFormat(barcodeFormat);
      previewView.setLayoutParams(new RelativeLayout.LayoutParams(WIDTH,HEIGHT));
      simpleDraweeView.setLayoutParams(new RelativeLayout.LayoutParams(WIDTH,HEIGHT));
      mlKitBarcodeDecoder.stopAll();
      mlKitBarcodeDecoder.startCamera();

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e(TAG, "onDestroy: ....");
//    mlKitBarcodeDecoder.stopAll();
  }

  @Override
  public void onDetach() {
    super.onDetach();
    Log.e(TAG, "onDetach: ....");
//    mlKitBarcodeDecoder.stopAll();
  }

  public void updatePreviewSize(int width, int height){
    Log.e(TAG, "updatePreviewSize: ");
    WIDTH = width;
    HEIGHT = height;

//    if(previewView != null){
//      if(mlKitBarcodeDecoder != null){
//      mlKitBarcodeDecoder.stopAll();
//      mlKitBarcodeDecoder.startCamera();
//      }
//      previewView.setLayoutParams(new RelativeLayout.LayoutParams(width,height));
//    }
  }

}
