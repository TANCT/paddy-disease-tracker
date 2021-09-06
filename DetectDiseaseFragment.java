package com.example.paddy_disease_tracker;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.paddy_disease_tracker.classifier.ImageClassifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetectDiseaseFragment extends Fragment {
    private static final int CAMERA_REQUEST_CODE = 10001;
    private ImageView imageView;
    private ListView listview;
    private static final int CAMERA_PERMISSION_REQUEST_CODE=1000;
    private ImageClassifier imageClassifier;
    Button takepicture, report, details;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_detect_disease,container,false);
        imageView=(ImageView)v.findViewById(R.id.imageCaptured);
        listview=(ListView)v.findViewById(R.id.probabilities);
        takepicture=(Button)v.findViewById(R.id.take_photo_btn);
        report=(Button)v.findViewById(R.id.report_btn);
        details=(Button)v.findViewById(R.id.detail_btn);
        try {
            imageClassifier=new ImageClassifier(getActivity());
        } catch (IOException e) {
            Log.e("Image Classifier Error","ERROR: "+e);
        }
        takepicture.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(hasPermission()){
                    openCamera();
                }else{
                    requestPermission();
                }

            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ReportActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    private void requestPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                Toast.makeText(getActivity(),"Camera Permission Required",Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CAMERA_PERMISSION_REQUEST_CODE){
            if(hasAllPermissions(grantResults)){
                openCamera();
            }else{
                requestPermission();
            }
        }
    }

    private boolean hasAllPermissions(int[] grantResults) {
        for(int result:grantResults){
            if(result== PackageManager.PERMISSION_DENIED)
                return false;
        }
        return true;
    }


    private void openCamera(){
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);

    }

    private boolean hasPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            return ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo;

        if (requestCode == CAMERA_REQUEST_CODE&&resultCode==RESULT_OK)  {
            photo=(Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
            imageView.setImageBitmap(photo);
            if(data==null||data.getStringExtra("path") == null || data.getStringExtra("path").trim().equals(null)){
                photo = Bitmap.createBitmap(photo.getWidth() + 96, photo.getHeight() + 96, Bitmap.Config.ARGB_8888);
            }

            report.setVisibility(View.VISIBLE);
            details.setVisibility(View.VISIBLE);
            List<ImageClassifier.Recognition> predictions = imageClassifier.recognizeImage(photo, 0);
            final List<String> predictionList = new ArrayList<>();
            for (ImageClassifier.Recognition r : predictions) {
                predictionList.add(String.format("Label: %s %nConfidence: %.2f%%",r.getName(),r.getConfidence()*100));
            }

            ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, predictionList);
            listview.setAdapter(predictionsAdapter);


        }
    }

}




