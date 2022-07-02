package com.example.firstflow.fragment;

import android.media.SoundPool;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.example.firstflow.R;
import com.example.firstflow.function.Recorder;

public class XylophoneFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    Recorder recorder = new Recorder(this.getContext());
    SoundPool soundPool = new SoundPool.Builder().setMaxStreams(8).build();
    boolean soundPoolLoaded = false;


    public XylophoneFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static XylophoneFragment newInstance(String param1, String param2) {
        XylophoneFragment fragment = new XylophoneFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_xylophone, container, false);

        ToggleButton recordBtn = v.findViewById(R.id.xylophone_toggleButton);

        Button[] keyboards = {
                (Button) v.findViewById(R.id.xylophone_do),
                (Button) v.findViewById(R.id.xylophone_re),
                (Button) v.findViewById(R.id.xylophone_mi),
                (Button) v.findViewById(R.id.xylophone_fa),
                (Button) v.findViewById(R.id.xylophone_so),
                (Button) v.findViewById(R.id.xylophone_la),
                (Button) v.findViewById(R.id.xylophone_ti),
                (Button) v.findViewById(R.id.xylophone_doHigh),
        };

        int[] files = {
                R.raw.do_,
                R.raw.re,
                R.raw.mi,
                R.raw.fa,
                R.raw.so,
                R.raw.la,
                R.raw.ti,
                R.raw.dohigh
        };



        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPoolLoaded = true;
            }
        });

        for (int i = 0; i < keyboards.length; i++) {
            int soundId = soundPool.load(v.getContext(), files[i], 1);

            keyboards[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (soundPoolLoaded) {
                        soundPool.play(soundId, 5, 5, 1, 0, 1);

                    }
                }
            });
        }

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordBtn.isChecked()){
                    recorder.startRecord();
                }else{
                    recorder.stopRecord();
                }
            }
        });

        return v;
    }


}