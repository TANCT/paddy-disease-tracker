package com.example.paddy_disease_tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;

public class ObjectDetectorActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG="DetectDiseaseFragment";
    private Mat mRgba;
    private Mat mGrey;
    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface
                        .SUCCESS:{
                    Log.i(TAG,"OpenCv is loaded");
                }
                default:
                {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    public ObjectDetectorActivity()
    {
        Log.i(TAG,"Instantiated new "+this.getClass());
    }

    private objectDetectorClass objectDetectorClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        if(ContextCompat.checkSelfPermission(ObjectDetectorActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(ObjectDetectorActivity.this,new String[] {Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);

        }

        setContentView(R.layout.activity_object_detector);

        mOpenCvCameraView=(CameraBridgeViewBase) findViewById(R.id.frame_Surface);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        try
        {
            objectDetectorClass=new objectDetectorClass(getAssets(), "modelLive.tflite","labelsLive.txt",300);
            Log.d("DetectDiseaseFragment","Model is successfully loadedr");
        }
        catch (IOException e)
        {
            Log.d("DetectDiseaseFragment","Getting some error");
            e.printStackTrace();
        }





    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(!OpenCVLoader.initDebug())
        {
            Log.d(TAG,"Opencv initialization is done");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else
        {
            Log.d(TAG,"Opencv is not loaded, try again");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);

        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(mOpenCvCameraView!=null)
        {
            mOpenCvCameraView.disableView();
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        if(mOpenCvCameraView!=null)
        {
            mOpenCvCameraView.disableView();
        }

    }

    public void onCameraViewStarted(int width,int height)
    {
        mRgba=new Mat(height,width, CvType.CV_8UC4);
        mGrey=new Mat(height,width,CvType.CV_8UC1);
    }
    public void onCameraViewStopped()
    {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba=inputFrame.rgba();
        mGrey=inputFrame.gray();

        Mat out=new Mat();
        out=objectDetectorClass.recognizeImage(mRgba);
        out=objectDetectorClass.recognizeImage(mRgba);

        return mRgba;
    }
}
