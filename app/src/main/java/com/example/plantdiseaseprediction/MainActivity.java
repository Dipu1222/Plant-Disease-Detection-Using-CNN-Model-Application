package com.example.plantdiseaseprediction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1001;
    private static final int CAMERA_REQUEST_CODE = 2001;

    private ImageView imageView;
    private Button selectImageBtn, predictBtn,takePhotoBtn;
    private TextView resultText;

    private Interpreter tflite;
    private Bitmap selectedBitmap;
    private Map<String, String> diseaseInfo;

    private final String[] classLabels = {
            "Apple___Apple_scab", "Apple___Black_rot", "Apple___Cedar_apple_rust", "Apple___healthy",
            "Cherry_(including_sour)___Powdery_mildew", "Cherry_(including_sour)___healthy",
            "Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot", "Corn_(maize)___Common_rust_",
            "Corn_(maize)___Northern_Leaf_Blight", "Corn_(maize)___healthy",
            "Grape___Black_rot", "Grape___Esca_(Black_Measles)", "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)", "Grape___healthy", "Potato___Early_blight", "Potato___Late_blight", "Potato___healthy"
    };
    private void initDiseaseInfo() {
        diseaseInfo = new HashMap<>();

        // Apple Diseases
        diseaseInfo.put("Apple___Apple_scab",
                "• Cause: Fungus (Venturia inaequalis)\n" +
                        "• Symptoms: Dark spots on leaves and fruits\n" +
                        "• Action: Remove fallen leaves and apply fungicides regularly\n\n" +
                        "• কারণ: ফাংগাস (Venturia inaequalis)\n" +
                        "• লক্ষণ: পাতায় ও ফলের কালো দাগ\n" +
                        "• করণীয়: পতিত পাতা সরিয়ে দিন এবং নিয়মিত ফাঙ্গিসাইড ব্যবহার করুন");

        diseaseInfo.put("Apple___Black_rot",
                "• Cause: Fungus (Botryosphaeria obtusa)\n" +
                        "• Symptoms: Black spots and decay on leaves and fruits\n" +
                        "• Action: Prune infected branches and apply fungicides\n\n" +
                        "• কারণ: ফাংগাস (Botryosphaeria obtusa)\n" +
                        "• লক্ষণ: পাতায় ও ফলের কালো দাগ ও পচন\n" +
                        "• করণীয়: সংক্রমিত শাখা ছেঁটে ফেলা এবং ফাঙ্গিসাইড ব্যবহার করা");

        diseaseInfo.put("Apple___Cedar_apple_rust",
                "• Cause: Fungus (Gymnosporangium juniperi-virginianae)\n" +
                        "• Symptoms: Yellow-orange spots on leaves\n" +
                        "• Action: Remove nearby cedar trees if possible and spray fungicides\n\n" +
                        "• কারণ: ফাংগাস (Gymnosporangium juniperi-virginianae)\n" +
                        "• লক্ষণ: পাতায় হলুদ-কমলা দাগ\n" +
                        "• করণীয়: সম্ভব হলে কাছাকাছি সিডার গাছ সরান এবং ফাঙ্গিসাইড স্প্রে করুন");

        diseaseInfo.put("Apple___healthy",
                "• This leaf is healthy ✅\n• No action needed\n\n" +
                        "• এই পাতা সুস্থ ✅\n• কোনো করণীয় নেই");

        // Cherry Diseases
        diseaseInfo.put("Cherry_(including_sour)___Powdery_mildew",
                "• Cause: Fungus (Podosphaera clandestina)\n" +
                        "• Symptoms: White powdery patches on leaves\n" +
                        "• Action: Use sulfur-based sprays and improve air circulation\n\n" +
                        "• কারণ: ফাংগাস (Podosphaera clandestina)\n" +
                        "• লক্ষণ: পাতায় সাদা গুঁড়ো দাগ\n" +
                        "• করণীয়: সালফার-ভিত্তিক স্প্রে ব্যবহার করুন এবং বাতাস চলাচল বৃদ্ধি করুন");

        diseaseInfo.put("Cherry_(including_sour)___healthy",
                "• This leaf is healthy ✅\n• No action needed\n\n" +
                        "• এই পাতা সুস্থ ✅\n• কোনো করণীয় নেই");

        // Corn Diseases
        diseaseInfo.put("Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot",
                "• Cause: Fungus (Cercospora zeae-maydis)\n" +
                        "• Symptoms: Small gray spots on leaves\n" +
                        "• Action: Use resistant corn varieties and apply fungicides\n\n" +
                        "• কারণ: ফাংগাস (Cercospora zeae-maydis)\n" +
                        "• লক্ষণ: পাতায় ছোট ধূসর দাগ\n" +
                        "• করণীয়: প্রতিরোধী ভুট্টার জাত ব্যবহার করুন এবং ফাঙ্গিসাইড স্প্রে করুন");

        diseaseInfo.put("Corn_(maize)___Common_rust_",
                "• Cause: Fungus (Puccinia sorghi)\n" +
                        "• Symptoms: Reddish-brown pustules on leaves\n" +
                        "• Action: Apply fungicides if infection is severe\n\n" +
                        "• কারণ: ফাংগাস (Puccinia sorghi)\n" +
                        "• লক্ষণ: পাতায় লালচে-বাদামী দাগ\n" +
                        "• করণীয়: সংক্রমণ গুরুতর হলে ফাঙ্গিসাইড ব্যবহার করুন");

        diseaseInfo.put("Corn_(maize)___Northern_Leaf_Blight",
                "• Cause: Fungus (Exserohilum turcicum)\n" +
                        "• Symptoms: Gray-green long lesions on leaves\n" +
                        "• Action: Use resistant varieties and apply fungicides\n\n" +
                        "• কারণ: ফাংগাস (Exserohilum turcicum)\n" +
                        "• লক্ষণ: পাতায় ধূসর-সবুজ লম্বা দাগ\n" +
                        "• করণীয়: প্রতিরোধী জাত ব্যবহার করুন এবং ফাঙ্গিসাইড স্প্রে করুন");

        diseaseInfo.put("Corn_(maize)___healthy",
                "• This leaf is healthy ✅\n• No action needed\n\n" +
                        "• এই পাতা সুস্থ ✅\n• কোনো করণীয় নেই");

        // Grape Diseases
        diseaseInfo.put("Grape___Black_rot",
                "• Cause: Fungus (Guignardia bidwellii)\n" +
                        "• Symptoms: Black spots on leaves and fruits\n" +
                        "• Action: Remove infected leaves and apply fungicides\n\n" +
                        "• কারণ: ফাংগাস (Guignardia bidwellii)\n" +
                        "• লক্ষণ: পাতায় ও ফলের কালো দাগ\n" +
                        "• করণীয়: সংক্রমিত পাতা সরান এবং ফাঙ্গিসাইড ব্যবহার করুন");

        diseaseInfo.put("Grape___Esca_(Black_Measles)",
                "• Cause: Fungal complex\n" +
                        "• Symptoms: Dark spots on wood and leaves\n" +
                        "• Action: Remove infected wood and apply fungicides\n\n" +
                        "• কারণ: ফাঙ্গাল কমপ্লেক্স\n" +
                        "• লক্ষণ: কাঠ ও পাতায় কালো দাগ\n" +
                        "• করণীয়: সংক্রমিত কাঠ সরান এবং ফাঙ্গিসাইড ব্যবহার করুন");

        diseaseInfo.put("Grape___Leaf_blight_(Isariopsis_Leaf_Spot)",
                "• Cause: Fungus (Isariopsis)\n" +
                        "• Symptoms: Brown spots on leaves\n" +
                        "• Action: Remove infected leaves and spray fungicides\n\n" +
                        "• কারণ: ফাংগাস (Isariopsis)\n" +
                        "• লক্ষণ: পাতায় বাদামী দাগ\n" +
                        "• করণীয়: সংক্রমিত পাতা সরান এবং ফাঙ্গিসাইড স্প্রে করুন");

        diseaseInfo.put("Grape___healthy",
                "• This leaf is healthy ✅\n• No action needed\n\n" +
                        "• এই পাতা সুস্থ ✅\n• কোনো করণীয় নেই");

        // Potato Diseases
        diseaseInfo.put("Potato___Early_blight",
                "• Cause: Fungus (Alternaria solani)\n" +
                        "• Symptoms: Dark rings on leaves and stems\n" +
                        "• Action: Apply fungicides and rotate crops\n\n" +
                        "• কারণ: ফাংগাস (Alternaria solani)\n" +
                        "• লক্ষণ: পাতায় ও গাছের ডালে কালো বৃত্তাকার দাগ\n" +
                        "• করণীয়: ফাঙ্গিসাইড ব্যবহার করুন এবং ফসল পরিবর্তন করুন");

        diseaseInfo.put("Potato___Late_blight",
                "• Cause: Fungus (Phytophthora infestans)\n" +
                        "• Symptoms: Wet-looking spots on leaves and tubers\n" +
                        "• Action: Use resistant varieties, apply fungicides, and ensure proper drainage\n\n" +
                        "• কারণ: ফাংগাস (Phytophthora infestans)\n" +
                        "• লক্ষণ: পাতায় ও আলুর দাগ ভেজা-ভেজা দেখায়\n" +
                        "• করণীয়: প্রতিরোধী জাত ব্যবহার করুন, ফাঙ্গিসাইড স্প্রে করুন এবং সঠিক নিষ্কাশন নিশ্চিত করুন");

        diseaseInfo.put("Potato___healthy",
                "• This leaf is healthy ✅\n• No action needed\n\n" +
                        "• এই পাতা সুস্থ ✅\n• কোনো করণীয় নেই");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDiseaseInfo();

        // Load TFLite model
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Model failed to load", Toast.LENGTH_LONG).show();
        }

        imageView = findViewById(R.id.imageView);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        predictBtn = findViewById(R.id.predictBtn);
        resultText = findViewById(R.id.resultText);
        takePhotoBtn = findViewById(R.id.takePhotoBtn);


        selectImageBtn.setOnClickListener(v -> openGallery());
        takePhotoBtn.setOnClickListener(v -> openCamera());
        predictBtn.setOnClickListener(v -> {
            if (selectedBitmap == null) {
                Toast.makeText(MainActivity.this, "⚠ Please select an image first", Toast.LENGTH_SHORT).show();
                return;
            }
            String prediction = classifyImage(selectedBitmap);


            resultText.setText("Prediction: " + prediction);
            resultText.setVisibility(View.VISIBLE);
            resultText.setOnClickListener(view -> {
                if (diseaseInfo.containsKey(prediction)) {
                    showDiseaseDialog(prediction, diseaseInfo.get(prediction));
                } else {
                    Toast.makeText(MainActivity.this, "No info available for this disease", Toast.LENGTH_SHORT).show();
                }
            });
        });

        selectImageBtn.setOnClickListener(v -> {
            openGallery();
            resultText.setVisibility(View.GONE); // Hide old result when picking new image
        });

    }

    private void showDiseaseDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_disease_info, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button dialogOk = dialogView.findViewById(R.id.dialogOkButton);

        dialogTitle.setText(title);
        dialogMessage.setText(message);

        AlertDialog dialog = builder.setView(dialogView).create();

        dialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }





    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedBitmap);
                predictBtn.setEnabled(true);
                resultText.setText("Prediction will appear here");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            selectedBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(selectedBitmap);
            resultText.setVisibility(View.GONE);
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        InputStream is = getAssets().openFd("plant_disease_2nd_final_model.tflite").createInputStream();
        FileInputStream fis = (FileInputStream) is;
        FileChannel fileChannel = fis.getChannel();
        long startOffset = getAssets().openFd("plant_disease_2nd_final_model.tflite").getStartOffset();
        long declaredLength = getAssets().openFd("plant_disease_2nd_final_model.tflite").getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private String classifyImage(Bitmap bitmap) {
        int imagesize = 128;
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, imagesize, imagesize, true);

        float[][][][] input = new float[1][imagesize][imagesize][3];
        for (int x = 0; x < imagesize; x++) {
            for (int y = 0; y < imagesize; y++) {
                int pixel = resized.getPixel(x, y);

                // Extract RGB components, normalize to [0,1]
                input[0][y][x][0] = ((pixel >> 16) & 0xFF) / 255.0f;
                input[0][y][x][1] = ((pixel >> 8) & 0xFF) / 255.0f;
                input[0][y][x][2] = (pixel & 0xFF) / 255.0f;
            }
        }

        float[][] output = new float[1][classLabels.length];
        tflite.run(input, output);

        int maxIndex = 0;
        float maxProb = 0;
        for (int i = 0; i < classLabels.length; i++) {
            if (output[0][i] > maxProb) {
                maxProb = output[0][i];
                maxIndex = i;
            }
        }
        // Threshold check
        if (maxProb < 0.8f) {  // Adjust 0.5 depending on your model
            return "⚠ Image not recognized as a valid leaf!";
        }

        return classLabels[maxIndex];
    }
}
