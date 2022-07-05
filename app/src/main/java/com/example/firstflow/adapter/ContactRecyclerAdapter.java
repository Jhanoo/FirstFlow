package com.example.firstflow.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firstflow.ContactDetailActivity;
import com.example.firstflow.R;
import com.example.firstflow.dto.Contact;

import java.io.IOException;
import java.util.ArrayList;

public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<Contact> listData = new ArrayList<>();

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

        Uri photoUri = getContactPhotoUri(holder.view.getContext().getContentResolver(), contact.getPhotoId());

        if (photoUri != null) {
            holder.imgView.setImageURI(photoUri);
            holder.photoUri = photoUri;
        }
    }

    private Uri getContactPhotoUri(ContentResolver cr, long photoId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, photoId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    cr.openAssetFileDescriptor(displayPhotoUri, "r");
            return displayPhotoUri;
        } catch (IOException e) {
            return null;
        }

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

    public void deleteAllItem() {
        listData.clear();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private TextView textView2;
        private ImageView imgView;
        private Uri photoUri;
        public View view;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            imgView = itemView.findViewById(R.id.profileImg);
            view = itemView;

            // 각 아이템마다 클릭이벤트 생성
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(v.getContext(), ContactDetailActivity.class);

                    detailIntent.putExtra("name", textView1.getText());
                    detailIntent.putExtra("phone", textView2.getText());
                    detailIntent.putExtra("photoUri", photoUri);

                    v.getContext().startActivity(detailIntent);
                }
            });
        }

    }
}