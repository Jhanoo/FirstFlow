package com.example.firstflow.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstflow.R;

import java.io.File;
import java.util.ArrayList;

public class ListenRecyclerAdapter extends RecyclerView.Adapter<ListenRecyclerAdapter.ListenViewHolder> {

    private ArrayList<String> listData = new ArrayList<>();
    private static ListenRecyclerAdapter adapter;

    public ListenRecyclerAdapter() {
        adapter = this;
    }

    @NonNull
    @Override
    public ListenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        Context context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_listen_recycler_adapter, parent, false);
        return new ListenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListenViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        // TODO : 저장된 음원 파일 바인딩하기
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        // TODO : RecyclerView 갯수 반환
        return listData.size();
    }

    public void addItem(String data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    public void clearItem(){
        listData.clear();
    }

    class ListenViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv;
        private ImageButton moreBtn;

        ListenViewHolder(View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.listen_adapter_name);
            moreBtn = itemView.findViewById(R.id.moreBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null)
                            mListener.onItemClick(view, pos);
                    }

                }
            });
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null)
                            mListener.onMenuClick(v, pos);
                    }
                }
            });
        }

        void onBind(String data) {
            nameTv.setText(data.substring(0,data.length()-4));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
        void onMenuClick(View v, int position);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

}
