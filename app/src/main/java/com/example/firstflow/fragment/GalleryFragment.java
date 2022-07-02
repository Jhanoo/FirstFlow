package com.example.firstflow.fragment;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.firstflow.PictureZoomActivity;
import com.example.firstflow.R;
import com.example.firstflow.adapter.GalleryRecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GalleryFragment() {
        // Required empty public constructor
    }

    ArrayList<Uri> a;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private GalleryRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private final String jsonName = "galleryUri.json";
    ArrayList<Uri> uriList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);

        // 리사이클러뷰 띄우기
        recyclerView = v.findViewById(R.id.fragmentGalleryRecyclerView);

        Button upBtn = v.findViewById(R.id.uploadBtn);
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });

        readJson();
        refresh();

        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2222) {
            if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(getContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            } else {   // 이미지를 하나라도 선택한 경우
                // 이미지를 여러장 선택한 경우
                ClipData clipData = data.getClipData();

                if (clipData.getItemCount() > 30) {   // 선택한 이미지가 30장 이상인 경우
                    Toast.makeText(getContext(), "사진은 30장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                } else {   // 선택한 이미지가 1장 이상 30장 이하인 경우
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        uriList.add(imageUri);  //uri를 list에 담는다.

                    }

                }
            }
        } else if (requestCode == 3333 && resultCode == 1) {
            int pos = data.getIntExtra("position", -1);
            uriList.remove(pos);

        }
        refresh();
    }

    private void readJson() {
        try {
            FileInputStream fis = getContext().openFileInput(jsonName);
            BufferedReader iReader = new BufferedReader((new InputStreamReader(fis)));

            StringBuffer buffer = new StringBuffer();
            String str = iReader.readLine();
            while (str != null) {
                buffer.append(str);
                str = iReader.readLine();
            }
            buffer.append("\n");
            iReader.close();

            JSONArray jarr = new JSONArray(buffer.toString());
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                uriList.add(Uri.parse(jobj.getString("uri")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        adapter = new GalleryRecyclerAdapter(uriList, getContext());
        recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter.setOnItemClickListener(new GalleryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(getContext(), PictureZoomActivity.class);
                intent.putExtra("imageUri", adapter.getData(pos));
                intent.putExtra("position", pos);
                startActivityForResult(intent, 3333);
            }
        });
        // JSON 으로 변환
        try {
            JSONArray jArray = new JSONArray();//배열이 필요할때
            for (int i = 0; i < uriList.size(); i++) {
                JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                sObject.put("uri", uriList.get(i));
                jArray.put(sObject);
            }

            FileOutputStream fos = getContext().openFileOutput(jsonName, Context.MODE_PRIVATE);
            fos.write(jArray.toString().getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}