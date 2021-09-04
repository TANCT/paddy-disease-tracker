package com.example.paddy_disease_tracker;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class DetectDiseaseFragment extends Fragment {


    public static final int UPLOAD_FROM_GALLERY_CODE=1;
    ImageView photo;
    Button upload_photo;
    Button detect_disease;
    Button report;
    Button details;
    TextView disease;
    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;
    Uri imageuri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detect_disease, container, false);

        upload_photo = v.findViewById(R.id.upload_img_btn);
        detect_disease = v.findViewById(R.id.detect_disease_btn);
        photo = v.findViewById(R.id.imageCaptured);
        disease = v.findViewById(R.id.probabilities);
        report=v.findViewById(R.id.report_btn);
        details=v.findViewById(R.id.detail_btn);

        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), UPLOAD_FROM_GALLERY_CODE);

            }
        });
        try {
            tflite = new Interpreter(loadmodelfile(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        detect_disease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int imageTensorIndex = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
                imageSizeY = imageShape[1];
                imageSizeX = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape =
                        tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
                DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                inputImageBuffer = loadImage(bitmap);

                tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());
                showresult(report,details);
            }
        });
        return v;
    }
    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }
    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }
    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }
    private void showresult(Button report, Button details){
        String ans="";
        DecimalFormat df = new DecimalFormat("0.00");

        try{
            labels = FileUtil.loadLabels(getActivity(),"labels.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        ArrayList<Float> prob=new ArrayList<Float>(labeledProbability.values());
        Collections.sort(prob,Collections.reverseOrder());
        //float maxValueInMap =(Collections.max(labeledProbability.values()));
        for(int i=0; i<4; i++){
            for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
                if (entry.getValue()==prob.get(i)) {
                    ans+=entry.getKey()+" : \t"+df.format(entry.getValue()*100)+"%\n";

                }
            }
        }

        disease.setText(ans);
        report.setVisibility(View.VISIBLE);
        details.setVisibility(View.VISIBLE);
    }
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==UPLOAD_FROM_GALLERY_CODE&&resultCode==RESULT_OK && data!=null) {
            imageuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageuri);
                photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

        /*
        View v= inflater.inflate(R.layout.fragment_detect_disease,container,false);
        imageView=(ImageView)v.findViewById(R.id.imageCaptured);
        listview=(ListView)v.findViewById(R.id.probabilities);
        Button takepicture=(Button)v.findViewById(R.id.take_photo_btn);
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
        if (requestCode == CAMERA_REQUEST_CODE) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(Objects.requireNonNull(data).getExtras()).get("data");
            imageView.setImageBitmap(photo);
            List<ImageClassifier.Recognition> predictions = imageClassifier.recognizeImage(photo, 0);
            final List<String> predictionList = new ArrayList<>();
            for (ImageClassifier.Recognition r : predictions) {
                predictionList.add("Label: " + r.getName() + " Confidence: " + r.getConfidence());
            }
            ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, predictionList);
            listview.setAdapter(predictionsAdapter);
        }*/


}

