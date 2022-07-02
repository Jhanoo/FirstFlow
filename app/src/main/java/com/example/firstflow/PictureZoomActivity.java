package com.example.firstflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

public class PictureZoomActivity extends AppCompatActivity {

    Uri getUri;
    Button delBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_zoom);

        getUri = getIntent().getParcelableExtra("imageUri");

        delBtn = findViewById(R.id.pictureDelBtn);
        int pos = getIntent().getIntExtra("position", -1);
        PhotoView photoView = findViewById(R.id.photoView);
        photoView.setImageURI(getUri);

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("position", pos);
                setResult(1, intent);
                finish();
            }
        });

    }
}