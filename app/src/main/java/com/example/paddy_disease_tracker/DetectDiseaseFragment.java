package com.example.paddy_disease_tracker;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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
    private static final int UPLOAD_FROM_GALLERY_CODE = 1;

    private ImageView imageView;
    private ListView listview;
    private ImageClassifier imageClassifier;
    Button report, details, upload_photo;
    Uri imageuri;
    Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detect_disease, container, false);
        imageView = (ImageView) v.findViewById(R.id.imageCaptured);
        listview = (ListView) v.findViewById(R.id.probabilities);
        report = (Button) v.findViewById(R.id.report_btn);
        details = (Button) v.findViewById(R.id.detail_btn);
        upload_photo = (Button) v.findViewById(R.id.upload_img_btn);
        try {
            imageClassifier = new ImageClassifier(getActivity());
        } catch (IOException e) {
            Log.e("Image Classifier Error", "ERROR: " + e);
        }

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),SelectDiseaseDetails.class);
                startActivity(intent);
            }
        });
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), UPLOAD_FROM_GALLERY_CODE);

            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==UPLOAD_FROM_GALLERY_CODE&&resultCode == RESULT_OK && data != null){
            imageuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageuri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if(bitmap!=null){
            List<ImageClassifier.Recognition> predictions = imageClassifier.recognizeImage(bitmap, 0);
            final List<String> predictionList = new ArrayList<>();
            for (ImageClassifier.Recognition r : predictions) {
                predictionList.add(String.format("Label: %s %nConfidence: %.2f%%", r.getName(), r.getConfidence() * 100));
            }

            ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, predictionList);
            listview.setAdapter(predictionsAdapter);
            details.setVisibility(View.VISIBLE);

        }

    }

}
