package com.reactnativemlkitbarcode;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

public class MlkitBarcodeViewManager extends ViewGroupManager<FrameLayout> {
  public static final String REACT_CLASS = "MlkitBarcodeViewManager";

  private final String TAG = "MlkitBCViewManager :";

  private final String COMMAND_CREATE = "create";
  private final String COMMAND_DESTROY = "destroy";
  private Choreographer.FrameCallback  frameCallback;
  private MlKitBarcodeFragment scannerFrag;
  private ThemedReactContext reactContext;

  private int barcodeFormat = 0;

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  public MlkitBarcodeViewManager() {
    super();
  }

  @Override
  @NonNull
  public FrameLayout createViewInstance(ThemedReactContext reactContext) {
    this.reactContext = reactContext;
    FrameLayout frameLayout = new FrameLayout(reactContext);
    return frameLayout;
  }

  @Override
  public void receiveCommand(@NonNull FrameLayout root, String commandId, @Nullable ReadableArray args) {
    super.receiveCommand(root, commandId, args);
    int reactNativeViewId = args.getInt(0);
    switch (commandId) {
      case COMMAND_CREATE:{
        createFragment(root, reactNativeViewId);
        break;
      }
      case COMMAND_DESTROY:
        removeFragment();
        break;
      default: {}
    }
  }

  @ReactProp(name="barcodeFormat")
  public void setBarcodeFormat( View view,int barcodeFormat){
//    Log.e(TAG, "setBarcodeFormat: "+ barcodeFormat);
    this.barcodeFormat = barcodeFormat;
  }


  public void createFragment(FrameLayout root, int reactNativeViewId) {
    ViewGroup parentView = (ViewGroup) root.findViewById(reactNativeViewId);
    this.setupLayout(parentView);
    this.scannerFrag = new MlKitBarcodeFragment(reactContext, barcodeFormat);
    FragmentActivity activity = (FragmentActivity) this.reactContext.getCurrentActivity();
    activity.getSupportFragmentManager()
      .beginTransaction()
      .replace(reactNativeViewId, this.scannerFrag)
      .commit();
  }

  public void removeFragment(){
    try {
      if(scannerFrag != null && scannerFrag.getScannerView() != null)
        scannerFrag.getScannerView().stopAll();
      Choreographer.getInstance().removeFrameCallback(this.frameCallback);
      FragmentActivity activity = (FragmentActivity) this.reactContext.getCurrentActivity();
      activity.getSupportFragmentManager().beginTransaction().remove(this.scannerFrag).commit();
    }
    catch (Exception e){
      Log.e(TAG, "removeFragment: Error :"+e.toString());
    }
  }

  public void setupLayout(View view) {
    try {
      this.frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
          manuallyLayoutChildren(view);
          view.getViewTreeObserver().dispatchOnGlobalLayout();
          Choreographer.getInstance().postFrameCallback(this);
        }
      };
      Choreographer.getInstance().postFrameCallback(this.frameCallback);
    }
    catch (Exception e){
      Log.e(TAG, "setupLayout: Exception :"+e.toString());
    }

  }

  public void manuallyLayoutChildren(View view) {
    try{
      DisplayMetrics displayMetrics = new DisplayMetrics();
      WindowManager windowManager = this.reactContext.getCurrentActivity().getWindowManager();
      windowManager.getDefaultDisplay().getMetrics(displayMetrics);

      int width = displayMetrics.widthPixels;
      int height = displayMetrics.heightPixels;
      view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
      view.layout(0, 0, width, height);
    }
    catch (NullPointerException e){
      Log.e(TAG, "manuallyLayoutChildren Error: "+e.toString());
    }
  }


}
