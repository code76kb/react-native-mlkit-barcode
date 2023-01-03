package com.reactnativemlkitbarcode;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;

public class MlKitBarcodeFragment extends Fragment {
  public static String TAG = "MlKitBarcodeFragment:";

  private MlKitBarcodeDecoder mlKitBarcodeDecoder = null;
  private ReactContext reactContext;
  private int barcodeFormat;

  private int WIDTH = 0;
  private int HEIGHT = 0;

  private PreviewView previewView = null;
  private View viewLine = null;

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

      View view  = inflater.inflate(R.layout.fragview,container,false);
      previewView = view.findViewById(R.id.previewView);
      viewLine = view.findViewById(R.id.lineView);
    mlKitBarcodeDecoder.createScannerView(previewView);
      return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
     Log.e(TAG, "onViewCreated: W:"+WIDTH+", H:"+HEIGHT);
      mlKitBarcodeDecoder.setBarCodeFormat(barcodeFormat);
      previewView.setLayoutParams(new RelativeLayout.LayoutParams(WIDTH,HEIGHT));
      viewLine.setLayoutParams(new RelativeLayout.LayoutParams(WIDTH,8));
      mlKitBarcodeDecoder.startCamera();
      playScanningAnimation();
  }

  public void updatePreviewSize(int width, int height){
    Log.e(TAG, "updatePreviewSize: ");
    WIDTH = width;
    HEIGHT = height;
  }

  private void playScanningAnimation(){
    ObjectAnimator animation = ObjectAnimator.ofFloat(viewLine, "translationY", HEIGHT);
    animation.setDuration(2000);
    animation.setRepeatCount(-1);
    animation.setRepeatMode(ValueAnimator.REVERSE);
    animation.start();
  }

}
