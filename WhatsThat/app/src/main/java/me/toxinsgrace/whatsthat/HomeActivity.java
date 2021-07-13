package me.toxinsgrace.whatsthat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    Button btnGetstarted;
    SharedPreferences prefs;
    Button btnSignout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        prefs = this.getSharedPreferences(getString(R.string.project_id),Context.MODE_PRIVATE);

        TextView lblName = findViewById(R.id.lblName);
        btnSignout = findViewById(R.id.btnSignout);
        btnGetstarted = findViewById(R.id.btnGetstarted);
        btnGetstarted.setOnClickListener(v -> {
            Intent intent = new Intent(this, Photograph.class);
            startActivity(intent);
        });
        btnSignout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            prefs.edit().clear().apply();
            startActivity(intent);
            this.finish();
        });

        lblName.setText(prefs.getString("user_name", "Anonymous"));
    }

}