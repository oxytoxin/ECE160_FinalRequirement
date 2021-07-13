package me.toxinsgrace.whatsthat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Photograph extends AppCompatActivity {
    ImageButton btnWhatsthat;
    ImageView imgPhoto;
    Bitmap photo;
    InputImage image;
    Button btnAnalyze;
    ProgressBar progressBar;

    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int CAMERA_INTENT_CODE = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph);
        btnWhatsthat = findViewById(R.id.btnWhatsthat);
        imgPhoto = findViewById(R.id.imgPhoto);
        progressBar = findViewById(R.id.progressBar);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        btnWhatsthat.setOnClickListener(v -> {
            checkCameraPermissions();
        });
        Context context = this;
        btnAnalyze.setOnClickListener(v -> {
            if (image == null){
                Toast.makeText(this, "You need to capture an image first.", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(ProgressBar.VISIBLE);
            ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
            labeler.process(image).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                @Override
                public void onSuccess(@NonNull @NotNull List<ImageLabel> imageLabels) {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Log.i("LABELS", imageLabels.toString());
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setTitle("PHOTO ANALYSIS");
                    String classes = "";
                    for (ImageLabel label : imageLabels) {
                        String text = label.getText();
                        float confidence = label.getConfidence();
                        if (confidence > 0.75){
                            classes += (text + ", ");
                        }
                    }
                    if (classes == "") classes = "Unidentifiable :(";
                    dialogBuilder.setMessage(classes);
                    dialogBuilder.setPositiveButton("One More!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Photograph.this, "So you want more eh?", Toast.LENGTH_SHORT).show();
                            checkCameraPermissions();
                        }
                    }).setNegativeButton("Return", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Photograph.this, "Great photography!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(Photograph.this, "Failed to analyze image. Please try capturing again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    private void checkCameraPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }else{
            photograph();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @org.jetbrains.annotations.NotNull String[] permissions, @NonNull @org.jetbrains.annotations.NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                photograph();
            }else{
                Toast.makeText(this, "Permission to use your camera is required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void photograph() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_INTENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_INTENT_CODE && resultCode != 0){
            Log.i("RESULT", String.valueOf(resultCode));
            photo = (Bitmap) data.getExtras().get("data");
            imgPhoto.setImageBitmap(photo);
            image = InputImage.fromBitmap(photo, 0);
        }
    }
}