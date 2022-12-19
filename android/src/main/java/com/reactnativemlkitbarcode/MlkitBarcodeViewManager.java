package com.reactnativemlkitbarcode;

import android.os.Handler;
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

  private int WIDTH = 0;
  private int HEIGHT = 0;

  private MlKitBarcodeDecoder mlKitBarcodeDecoder;

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  public MlkitBarcodeViewManager() {
    super();
    // Log.e(TAG, "MlkitBarcodeViewManager:  Construct....");
  }

  @Override
  @NonNull
  public FrameLayout createViewInstance(ThemedReactContext reactContext) {
    this.reactContext = reactContext;
//    previewView = new PreviewView(reactContext.getCurrentActivity());
//    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//    layoutParams.weight = 300;
//    layoutParams.height = 200;
//    previewView.setLayoutParams(layoutParams);
//    Log.e(TAG, "createViewInstance: View instance Created...");
//    startCamera();

//    scannerView = new ScannerView(reactContext);
//    this.startScanner();
//    return scannerView.createScannerView(100,200);

    FrameLayout frameLayout = new FrameLayout(reactContext);
    return frameLayout;
  }

  @ReactProp(name = "width")
  public void setWidth(View view, int width) {
    // Log.e(TAG, "setWidth: " + width);
//    view.getLayoutParams().width = width;
    WIDTH = width;
    if(scannerFrag != null && scannerFrag.getScannerView() != null)
      scannerFrag.getScannerView().updateSize(WIDTH,HEIGHT);
  }

  @ReactProp(name = "height")
  public void setHeight(View view, int height) {
    // Log.e(TAG, "setHeight: " + height);
//    view.getLayoutParams().height = height;
    HEIGHT = height;
    if(scannerFrag != null && scannerFrag.getScannerView() != null)
      scannerFrag.getScannerView().updateSize(WIDTH,HEIGHT);
  }

  private void startScanner(){
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        mlKitBarcodeDecoder.startCamera();
      }
    },1200);
  }

  @Override
  public void receiveCommand(@NonNull FrameLayout root, String commandId, @Nullable ReadableArray args) {
    super.receiveCommand(root, commandId, args);
    int reactNativeViewId = args.getInt(0);

    // Log.e(TAG, "receiveCommand: "+commandId);
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



  public void createFragment(FrameLayout root, int reactNativeViewId) {
    // Log.e(TAG, "createFragment: ..."+reactNativeViewId+" rootID:"+root.getId());
    ViewGroup parentView = (ViewGroup) root.findViewById(reactNativeViewId);
    this.setupLayout(parentView);
    this.scannerFrag = new MlKitBarcodeFragment(WIDTH,HEIGHT,reactContext);
    // Log.e(TAG, "Fragment Transaction... ");
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
      // Log.e(TAG, "Fragment removed...");
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
//        Log.e(TAG, "doFrame: "+frameTimeNanos);
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
//      Log.e(TAG, "manuallyLayoutChildren:... ");
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
