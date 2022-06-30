package com.example.firstflow.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.firstflow.R;

import java.util.ArrayList;

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.GalleryViewHolder> {

    private ArrayList<Uri> listData = null;
    private Context mContext = null;

    public GalleryRecyclerAdapter(ArrayList<Uri> list, Context context) {
        listData = list;
        mContext = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_gallery_recycler_adapter, parent, false);
        return new GalleryViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        Uri image_uri = listData.get(position);
        Glide.with(mContext).load(image_uri).into(holder.imgView);
    }

    public void addItem(Uri uri) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(uri);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return listData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class GalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgView;

        GalleryViewHolder(View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.galleryImgView);
            itemView.setOnClickListener(this);
        }

        // ToDo
        @Override
        public void onClick(View v) {

        }
    }

}