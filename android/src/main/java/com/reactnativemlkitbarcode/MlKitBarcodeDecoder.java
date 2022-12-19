package com.reactnativemlkitbarcode;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
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
  private PreviewView previewView = null;
  private BarcodeScanner scanner;
  private ProcessCameraProvider cameraProvider;
  private ImageAnalysis imageAnalysis;

  MainThreadExecutor mainThreadExecutor;

  private ReactContext reactContext;

  private int barcodeFormat = 0; // All

  public MlKitBarcodeDecoder(Fragment frag, ReactContext reactContext){
    this.frag = frag;
    this.reactContext = reactContext;
  }

  public PreviewView createScannerView(){
    this.previewView = new PreviewView(frag.getActivity());
    return this.previewView;
  }

  public void setBarCodeFormat(int barcodeFormat){
    Log.e(TAG, "setBarCodeFormat: "+barcodeFormat);
    this.barcodeFormat = barcodeFormat;
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
        cameraProvider.bindToLifecycle(frag.getActivity(), cameraSelector, preview, imageAnalysis);
        preview.setSurfaceProvider(this.previewView.getSurfaceProvider());

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
  }

  private Preview getPreview() {
    return new Preview.Builder()
      .setTargetAspectRatio(aspectRatio())
      .setTargetRotation(this.previewView.getDisplay().getRotation())
      .build();
  }

  private int aspectRatio() {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    this.previewView.getDisplay().getRealMetrics(displayMetrics);
    double previewRatio = (double) Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels) / Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE)) {
      return AspectRatio.RATIO_4_3;
    }
    return AspectRatio.RATIO_16_9;
  }

  private void createBarCodeScanner(){
    Log.e(TAG, "createBarCodeScanner: BarCode Format :"+barcodeFormat);
    BarcodeScannerOptions options =
      new BarcodeScannerOptions.Builder()
        .setBarcodeFormats(barcodeFormat)
        .build();
    scanner = BarcodeScanning.getClient(options);
  }


  @SuppressLint("UnsafeOptInUsageError")
  @Override
  public void analyze(@NonNull ImageProxy imageProxy) {
    Image mediaImage = imageProxy.getImage();
    if (mediaImage != null) {
      InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
      scanner.process(image).addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
        @Override
        public void onSuccess(List<Barcode> barcodes) {
          if(barcodes.size() > 0){
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
            if(mediaImage!=null)
              mediaImage.close();
            imageProxy.close();
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
