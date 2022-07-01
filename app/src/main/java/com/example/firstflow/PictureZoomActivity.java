package com.example.firstflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

public class PictureZoomActivity extends AppCompatActivity {

    Uri getUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_zoom);

        Intent intent = getIntent();
        getUri = getIntent().getParcelableExtra("imageUri");

        Log.d(null, "=============== " + getUri);

        PhotoView photoView = findViewById(R.id.photoView);
        photoView.setImageURI(getUri);

    }
}