package com.example.firstflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firstflow.adapter.ContactRecyclerAdapter;

public class ContactDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        init();
    }

    protected void init(){
        // indent로 연락처 정보 불러와서 매핑시키기
        Intent detailIntent = getIntent();

        TextView nameTextView = (TextView)findViewById(R.id.contact_name);
        TextView phoneTextView = (TextView)findViewById(R.id.contact_num);
        ImageButton callImageButton = (ImageButton)findViewById(R.id.contact_callBtn);
        ImageButton smsImageButton = (ImageButton)findViewById(R.id.contact_smsBtn);
        ImageView profileImageView = (ImageView)findViewById(R.id.contactDetail_profile);

        Long photoId = detailIntent.getLongExtra("photoId", 0);

        nameTextView.setText(detailIntent.getStringExtra("name"));
        phoneTextView.setText(detailIntent.getStringExtra("phone"));

        if(photoId != 0){
            Bitmap profile = ContactRecyclerAdapter.loadContactPhoto(getContentResolver(), photoId);
            profileImageView.setImageBitmap(profile);
        }

        profileImageView.setBackground(new ShapeDrawable(new OvalShape()));
        profileImageView.setClipToOutline(true);

        callImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = "tel:"+detailIntent.getStringExtra("phone");
                startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
            }
        });

        smsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String smsString = "sms:"+detailIntent.getStringExtra("phone");
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(smsString)));
            }
        });
    }
}