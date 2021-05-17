package com.example.lads_image_edit_app;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;


public class MainActivity extends FlutterActivity {

    static {
        System.loadLibrary("SuperResolution");
    }

    private static final String TAG = "SuperResolution";
    private static final String MODEL_NAME = "ESRGAN.tflite";
    private static final int LR_IMAGE_HEIGHT = 50;
    private static final int LR_IMAGE_WIDTH = 50;
    private static final int UPSCALE_FACTOR = 4;
    private static final int SR_IMAGE_HEIGHT = LR_IMAGE_HEIGHT * UPSCALE_FACTOR;
    private static final int SR_IMAGE_WIDTH = LR_IMAGE_WIDTH * UPSCALE_FACTOR;
    private static final String LR_IMG_1 = "lr-1.jpg";
    private static final String LR_IMG_2 = "lr-2.jpg";
    private static final String LR_IMG_3 = "lr-3.jpg";

    private MappedByteBuffer model;
    private long superResolutionNativeHandle = 0;
    private Bitmap selectedLRBitmap = null;
    private boolean useGPU = false;

    private ImageView lowResImageView1;
    private ImageView lowResImageView2;
    private ImageView lowResImageView3;
    private TextView selectedImageTextView;
    private Switch gpuSwitch;
    File imageFile;
    private static final String CHANNEL = "sample.resolution.dev/image";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(),CHANNEL)
                .setMethodCallHandler((call, result) -> {
                    if (call.method.equals("getModifiedImage")){

                        String image = call.argument("image");
                        if (image != null){
                            imageFile= new File(image);
                            System.out.print(image);
                            result.success(image);
                        }


                    }
                    else{
                        result.notImplemented();
                    }
                });



    }

    String getImage(){
        return "HI";
    }



    void showDialog(){


    }


    @WorkerThread
    public synchronized int[] doSuperResolution(int[] lowResRGB) {
        return superResolutionFromJNI(superResolutionNativeHandle, lowResRGB);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        try (AssetFileDescriptor fileDescriptor =
                     AssetsUtils.getAssetFileDescriptorOrCached(getApplicationContext(), MODEL_NAME);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }

    private void showToast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    private long initTFLiteInterpreter(boolean useGPU) {
        try {
            model = loadModelFile();
        } catch (IOException e) {
            Log.e(TAG, "Fail to load model", e);
        }
        return initWithByteBufferFromJNI(model, useGPU);
    }

    private void deinit() {
        deinitFromJNI(superResolutionNativeHandle);
    }

    private native int[] superResolutionFromJNI(long superResolutionNativeHandle, int[] lowResRGB);

    private native long initWithByteBufferFromJNI(MappedByteBuffer modelBuffer, boolean useGPU);

    private native void deinitFromJNI(long superResolutionNativeHandle);
}

