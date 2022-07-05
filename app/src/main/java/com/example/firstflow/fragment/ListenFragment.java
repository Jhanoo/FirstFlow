package com.example.firstflow.fragment;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.firstflow.R;
import com.example.firstflow.adapter.ListenRecyclerAdapter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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
    String changeName = "바꿀 이름";


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

            @Override
            public void onMenuClick(View v, int position) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.option_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.changeNameMenu:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
                                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom, v.findViewById(R.id.layoutDialog));

                                builder.setView(view);

                                ((TextView)view.findViewById(R.id.dialog_textTitle)).setText("파일명 변경");
                                ((TextView)view.findViewById(R.id.dialog_textMessage)).setText("변경할 파일명을 입력해주세요.");
                                ((Button)view.findViewById(R.id.dialog_OkBtn)).setText("저장");
                                ((Button)view.findViewById(R.id.dialog_cancelBtn)).setText("취소");

                                AlertDialog alertDialog = builder.create();

                                view.findViewById(R.id.dialog_OkBtn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changeName = ((EditText)view.findViewById(R.id.dialog_editText)).getText().toString();
                                        if (position != RecyclerView.NO_POSITION) {
                                            String pcmName = files.get(position);
                                            File audioCapturesDirectory = new File(getContext().getExternalFilesDir(null), "/AudioCaptures");
                                            String path = audioCapturesDirectory.getAbsolutePath() + "/";
                                            File filePrev = new File(path + pcmName);
                                            File fileChange = new File(path + changeName + ".pcm");
                                            int i = 2;
                                            while(fileChange.exists()) {
                                                String newName = changeName + i;
                                                fileChange = new File(path + newName + ".pcm");
                                                i++;
                                            }
                                            filePrev.renameTo(fileChange);

                                            getPcmList(files);
                                            getData(files);
                                        }
                                        alertDialog.dismiss();
                                    }
                                });
                                view.findViewById(R.id.dialog_cancelBtn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                if(alertDialog.getWindow() != null){
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                                }
                                alertDialog.show();


                                break;

                            case R.id.deleteMenu:
                                if (position != RecyclerView.NO_POSITION) {
                                    String pcmName = files.get(position);
                                    File audioCapturesDirectory = new File(getContext().getExternalFilesDir(null), "/AudioCaptures");
                                    String path = audioCapturesDirectory.getAbsolutePath() + "/";
                                    File file = new File(path + pcmName);
                                    file.delete();

                                    getPcmList(files);
                                    getData(files);
                                }
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
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
        adapter.clearItem();
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

        if(pcmList != null){
            Arrays.sort(pcmList);

            files.clear();
            files.addAll(Arrays.asList(pcmList));
        }

    }

}