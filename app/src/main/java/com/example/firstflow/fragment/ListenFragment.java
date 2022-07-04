package com.example.firstflow.fragment;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firstflow.R;
import com.example.firstflow.adapter.ListenRecyclerAdapter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListenFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListenFragment newInstance(String param1, String param2) {
        ListenFragment fragment = new ListenFragment();
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

    private ListenRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_listen, container, false);
        adapter = new ListenRecyclerAdapter();

        RecyclerView recyclerView = v.findViewById(R.id.fragmentListenRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        // TODO : 파일 리스트 불러온 것을 recyclerView에 넣기
        ArrayList<String> files = new ArrayList<>();
        getPcmList(files);
        getData(files);


        adapter.setOnItemClickListener(new ListenRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (playThread != null) {
                    playThread.interrupt();
                }
                pcmName = files.get(position);
                playPcm();

            }
        });

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (playThread != null)
            playThread.interrupt();
    }

    private void getData(ArrayList<String> a) {
        for (int i = 0; i < a.size(); i++) {
            adapter.addItem(a.get(i));
        }

        adapter.notifyDataSetChanged();
    }

    public Thread playThread;
    private String pcmName;

    private void playPcm() {
        AudioTrack audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(8000)
                        .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
                        .build())
                .setBufferSizeInBytes(2048)
                .build();

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] writeData = new byte[2048];
                FileInputStream fis = null;
                File audioCapturesDirectory = new File(getContext().getExternalFilesDir(null), "/AudioCaptures");
                String path = audioCapturesDirectory.getAbsolutePath() + "/" + pcmName;
                try {
                    fis = new FileInputStream(path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                DataInputStream dis = new DataInputStream(fis);
                audioTrack.play();
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int ret = dis.read(writeData, 0, 2048);
                        if (ret <= 0) {
                            break;
                        }
                        audioTrack.write(writeData, 0, ret);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                audioTrack.stop();
                audioTrack.release();
                try {
                    dis.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        playThread.start();
    }


    private void getPcmList(ArrayList<String> files) {
        File audioCapturesDirectory = new File(getContext().getExternalFilesDir(null), "/AudioCaptures");
        String pcmList[] = audioCapturesDirectory.list();
        files.addAll(Arrays.asList(pcmList));

    }

}