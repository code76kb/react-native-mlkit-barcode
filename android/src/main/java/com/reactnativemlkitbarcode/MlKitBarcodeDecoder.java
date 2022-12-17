package com.reactnativemlkitbarcode;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraFilter;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class MlKitBarcodeDecoder implements ImageAnalysis.Analyzer{
  private static String TAG = "MlKitBarcodeDecoder :";

  private final double RATIO_4_3_VALUE = 4.0 / 3.0;
  private final double RATIO_16_9_VALUE = 16.0 / 9.0;
  private Fragment frag;
  private PreviewView previewView;
  private BarcodeScanner scanner;
  private ProcessCameraProvider cameraProvider;
  private ImageAnalysis imageAnalysis;

  MainThreadExecutor mainThreadExecutor;

  private ReactContext reactContext;

  public MlKitBarcodeDecoder(Fragment frag, ReactContext reactContext){
    this.frag = frag;
    this.reactContext = reactContext;
  }

  public PreviewView createScannerView(int width, int height){
    previewView = new PreviewView(frag.getActivity());
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    layoutParams.weight = width;
    layoutParams.height = height;
    layoutParams.gravity = Gravity.CENTER;
    previewView.setLayoutParams(layoutParams);
    previewView.setBackgroundColor(Color.RED);
    return previewView;
  }

  public void updateSize(int width, int height){
    Log.e(TAG, "updateSize: W:"+width+" H:"+height);
    previewView.getLayoutParams().width = width;
    previewView.getLayoutParams().height = height;
  }


  /////////////////
  protected void startCamera() {
    createBarCodeScanner();
    mainThreadExecutor = new MainThreadExecutor();

    ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(frag.getActivity());
    processCameraProvider.addListener(() -> {
      try {

        Preview preview = getPreview();

        CameraSelector cameraSelector = new CameraSelector.Builder()
          .requireLensFacing(CameraSelector.LENS_FACING_BACK)
          .build();

        imageAnalysis = new ImageAnalysis.Builder()
          .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
          .build();
        imageAnalysis.setAnalyzer(new ThreadExecutor(),this);

        cameraProvider = processCameraProvider.get();

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(frag.getActivity(), cameraSelector, preview,imageAnalysis);
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

      } catch (ExecutionException e) {
        Log.e(TAG, "startCamera: ExecutionException :"+e.toString());
        e.printStackTrace();
      } catch (InterruptedException e) {
        Log.e(TAG, "startCamera: InterruptedException :"+e.toString());
        e.printStackTrace();
      }

    },mainThreadExecutor);
  }

  @SuppressLint("RestrictedApi")
  protected void stopAll(){
    if(imageAnalysis != null){
      imageAnalysis.clearAnalyzer();
    }
    if(scanner!=null)
      scanner.close();
    if(cameraProvider!=null){
      cameraProvider.unbindAll();
      cameraProvider.shutdown();
    }

    Log.e(TAG, "stopAll:....");
  }

  private Preview getPreview() {
    return new Preview.Builder()
      .setTargetAspectRatio(aspectRatio())
      .setTargetRotation(previewView.getDisplay().getRotation())
      .build();
  }

  private int aspectRatio() {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    previewView.getDisplay().getRealMetrics(displayMetrics);
    double previewRatio = (double) Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels) / Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
      return AspectRatio.RATIO_4_3;
    }
    return AspectRatio.RATIO_16_9;
  }

  private void createBarCodeScanner(){
    BarcodeScannerOptions options =
      new BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
          Barcode.FORMAT_QR_CODE)
        .build();
    scanner = BarcodeScanning.getClient(options);
  }


  @SuppressLint("UnsafeOptInUsageError")
  @Override
  public void analyze(@NonNull ImageProxy imageProxy) {
//    Log.e(TAG, "analyze: ImageProxy:"+imageProxy.getImageInfo().getTimestamp());
    Image mediaImage = imageProxy.getImage();
    if (mediaImage != null) {
      InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
      scanner.process(image).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
        @Override
        public void onSuccess(List<Barcode> barcodes) {
          if(barcodes.size() > 0){
//                Log.e(TAG, "analyze: ImageProxy: H:  "+imageProxy.getImage().getHeight()+" W :"+imageProxy.getImage().getWidth());
            Log.e(TAG, "onSuccess: BarCodes :"+barcodes.size());
            String rawValue = barcodes.get(barcodes.size()-1).getRawValue();
            Log.e(TAG, "onSuccess: RawValue :"+rawValue);
            sendEvent(rawValue);
          }
        }
      })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onFailure: "+e.getMessage()+"\n"+e.getCause());

          }
        })
        .addOnCanceledListener(new OnCanceledListener() {
          @Override
          public void onCanceled() {
            Log.e(TAG, "onCanceled: ....");

          }
        })
        .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
          @Override
          public void onComplete(@NonNull Task<List<Barcode>> task) {
            if(mediaImage!=null)
                mediaImage.close();
            imageProxy.close();
          }
        });
    }

  }

  private void sendEvent(String data){
    WritableMap writableMap = new WritableNativeMap();
    writableMap.putString("code",data);
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("BARCODE_SCANNED",writableMap);

  }


  //////////////////////////

  public class MainThreadExecutor implements Executor {
    private final Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public void execute(Runnable r) {
      handler.post(r);
    }
  }

  public class ThreadExecutor implements Executor {
    @Override
    public void execute(Runnable r) {
      new Thread(r).start();
    }
  }


}
