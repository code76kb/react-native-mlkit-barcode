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
  private int barcodeFormat;


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
    RelativeLayout relativeLayout = new RelativeLayout(MlKitBarcodeFragment.this.getContext());
    relativeLayout.setGravity(Gravity.CENTER);
    relativeLayout.addView(mlKitBarcodeDecoder.createScannerView());

    return relativeLayout;
//    return null;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
     Log.e(TAG, "onViewCreated: ");
      mlKitBarcodeDecoder.setBarCodeFormat(barcodeFormat);
      mlKitBarcodeDecoder.startCamera();

  }

}
