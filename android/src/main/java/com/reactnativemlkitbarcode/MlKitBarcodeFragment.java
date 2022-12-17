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
import androidx.fragment.app.Fragment;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;

public class MlKitBarcodeFragment extends Fragment {
  public static String TAG = "MlKitBarcodeFragment:";

  private MlKitBarcodeDecoder mlKitBarcodeDecoder = null;
  private ReactContext reactContext;
  private int width;
  private int height;

  public MlKitBarcodeFragment(int width, int height, ThemedReactContext reactContext) {
    this.reactContext = reactContext;
    this.width = width;
    this.height = height;
  }

  public MlKitBarcodeDecoder getScannerView(){
    return mlKitBarcodeDecoder;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    mlKitBarcodeDecoder = new MlKitBarcodeDecoder(this, reactContext);
    RelativeLayout relativeLayout = new RelativeLayout(MlKitBarcodeFragment.this.getContext());
    relativeLayout.setGravity(Gravity.CENTER);
    relativeLayout.addView(mlKitBarcodeDecoder.createScannerView(width,height));

    return relativeLayout;
//    return null;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mlKitBarcodeDecoder.startCamera();
    Log.e(TAG, "onViewCreated: ");
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.e(TAG, "onPause: ");
  }

  @Override
  public void onStop() {
    super.onStop();
    Log.e(TAG, "onStop: ");
  }

  @Override
  public void onDetach() {
    super.onDetach();
    Log.e(TAG, "onDetach: ");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e(TAG, "onDestroy: ....");
//    if(scannerView != null)
//      scannerView.stopAll();
  }

}
