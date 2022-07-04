package com.example.firstflow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstflow.R;

import java.util.ArrayList;

        public class ListenRecyclerAdapter extends RecyclerView.Adapter<com.example.firstflow.adapter.ListenRecyclerAdapter.ItemViewHolder> {

            private ArrayList<String> listData = new ArrayList<>();

            @NonNull
            @Override
            public com.example.firstflow.adapter.ListenRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
                // return 인자는 ViewHolder 입니다.
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_listen_recycler_adapter, parent, false);
        return new ListenRecyclerAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
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
    
    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView name;

        ItemViewHolder(View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.listen_adapter_name);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO : 누르면 음원 재생되도록 하기
                }
            });
        }

        void onBind(String data){
            name.setText(data);
        }
    }
}
