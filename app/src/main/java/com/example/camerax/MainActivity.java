package com.example.camerax;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static Camera.Parameters p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA
    };

    private static final int REQUEST_PERMISSIONS = 34;

    private static final int PERMISSIONS_COUNT = 1;

    @SuppressLint("NewApi")
    private boolean arePermissionsDenied(){
        for(int i = 0; i< PERMISSIONS_COUNT ; i++){
            if(checkSelfPermission(PERMISSIONS[i])!= PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_PERMISSIONS && grantResults.length > 0){
            if(arePermissionsDenied()){
                ((ActivityManager) (this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }else{
                onResume();

            }
        }

    }

    private boolean isCameraInitialized;

    private Camera mCamera = null;

    private static SurfaceHolder myHolder;

    private static CameraPreview mPreview;

    private FrameLayout preview;

    @Override
    protected void onResume(){
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()){
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }

        if(!isCameraInitialized){
            mPreview = new CameraPreview(this, mCamera);
            preview = findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            rotateCamera();

        }
    }

    private static List<String> camEffects;

    private static boolean hasFlash(){
        camEffects = p.getSupportedColorEffects();
        final List<String> flashModes = p.getSupportedFlashModes();
        if(flashModes == null) {
            return false;
        }
        for(String flashMode:flashModes){
            if(Camera.Parameters.FLASH_MODE_ON.equals(flashMode)){
                return true;
    }

        }

    }


    private static int rotation;
    private static boolean whichCamera = true;
    private Camera.Parameters p;
    private void rotateCamera(){
        if(mCamera != null){
            rotation = this.getWindowManager().getDefaultDisplay().getRotation();
            if(rotation ==0){
                rotation = 90;
            }else if(rotation == 1){
                rotation = 0;
            }else if(rotation == 2){
                rotation = 270;
            }else{
                rotation = 180;
            }
            mCamera.setDisplayOrientation(rotation);
            if(! whichCamera ){
                if(rotation == 90){
                    rotation = 270;
                }else if (rotation == 270){
                    rotation = 90;
                }
            }
            p = mCamera.getParameters();
            p.setRotation(rotation);
            mCamera.setParameters(p);
        }
    }

    private static class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
        private static SurfaceHolder mHolder;
        private static Camera mCamera;

        private CameraPreview(Context context, Camera camera){
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        public void surfaceCreated(SurfaceHolder holder){
            myHolder = holder;
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void surfaceDestroyed(SurfaceHolder holder){

        }
        public void surfaceChanged(SurfaceHolder, int format, int w, int h){

        }
    }
}