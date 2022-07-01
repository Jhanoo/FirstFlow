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

    private ArrayList<Uri> mData = null;
    private Context mContext = null;

    public GalleryRecyclerAdapter(ArrayList<Uri> list, Context context) {
        mData = list;
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
        Uri image_uri = mData.get(position);
        Glide.with(mContext).load(image_uri).into(holder.imgView);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Uri getData(int pos){
        return mData.get(pos);
    }

    // stores and recycles views as they are scrolled off screen
    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgView;

        GalleryViewHolder(View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.galleryImgView);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });


        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

}