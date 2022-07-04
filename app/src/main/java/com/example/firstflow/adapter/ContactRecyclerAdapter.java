package com.example.firstflow.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firstflow.ContactDetailActivity;
import com.example.firstflow.R;
import com.example.firstflow.dto.Contact;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<Contact> listData = new ArrayList<>();
    public Bitmap profile;
    public long photoIdForIntent;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_contact_recycler_adapter, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Contact contact = listData.get(position);

        holder.textView1.setText(contact.getName());
        holder.textView2.setText(contact.getPhoneNum());

        holder.imgView.setImageDrawable(holder.view.getContext().getResources().getDrawable(R.drawable.personicon));
        holder.imgView.setBackground(new ShapeDrawable(new OvalShape()));
        holder.imgView.setClipToOutline(true);

        profile = loadContactPhoto(holder.view.getContext().getContentResolver(), contact.getPhotoId());

        if(profile != null){
            holder._photoIdForIntent = contact.getPhotoId();
            holder.imgView.setImageBitmap(profile);
        }
    }


    public static Bitmap loadContactPhoto(ContentResolver cr, long photoId) {
        byte[] photoBytes = null;
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, photoId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor c = cr.query(
                photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO},
                null,
                null,
                null
        );

        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);
        } catch (Exception e) {
            Log.e("loadContact", "" + e);
            e.printStackTrace();
        } finally {
            c.close();
        }

        if (photoBytes != null) {
            return resizingBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        }

        return null;
    }

    public static Bitmap resizingBitmap(Bitmap oBitmap) {
        if (oBitmap == null) {
            return null;
        }
        float width = oBitmap.getWidth();
        float height = oBitmap.getHeight();
        float resizing_size = 120;
        final int RATIO = 100;

        Bitmap rBitmap = null;
        if (width > resizing_size) {
            float mWidth = (float)(width / RATIO);
            float fScale = (float)(resizing_size / mWidth);
            width *= (fScale / RATIO);
            height *= (fScale / RATIO);
        } else if (height > resizing_size) {
            float mHeight = (float)(height / RATIO);
            float fScale = (float)(resizing_size / mHeight);
            width *= (fScale / RATIO);
            height *= (fScale / RATIO);
        }
        //Log.d("rBitmap : " + width + ", " + height);
        rBitmap = Bitmap.createScaledBitmap(oBitmap, (int)width, (int)height, true);

        return rBitmap;
    }



    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    public void addItem(Contact data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public void deleteAllItem(){
        listData.clear();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private TextView textView2;
        private ImageView imgView;
        private long _photoIdForIntent;
        public View view;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            imgView = itemView.findViewById(R.id.profileImg);
            view = itemView;
            _photoIdForIntent = photoIdForIntent;

            // 각 아이템마다 클릭이벤트 생성
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent detailIntent = new Intent(v.getContext(), ContactDetailActivity.class);

                    detailIntent.putExtra("name", textView1.getText());
                    detailIntent.putExtra("phone", textView2.getText());
                    detailIntent.putExtra("photoId", _photoIdForIntent);

                    v.getContext().startActivity(detailIntent);
                }
            });
        }

    }
}