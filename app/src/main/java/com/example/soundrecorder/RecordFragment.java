package com.example.soundrecorder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment {

    private NavController navController;
    private ImageView listButton;
    private ImageView recordButton;
    private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;
    private MediaRecorder mediaRecorder;
    private String recordFile;
    private Chronometer chronometer;
    private TextView recordText;


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listButton = view.findViewById(R.id.record_list_btn);
        recordButton = view.findViewById(R.id.record_btn);
        chronometer = view.findViewById(R.id.chronometer);
        recordText = view.findViewById(R.id.recordText);
        listButton.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.record_list_btn:
                    if (isRecording) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                navController.navigate(R.id.action_recordFragment_to_listFragment);
                                isRecording = false;
                            }
                        });
                        alertDialog.setNegativeButton("CANCEL", null);
                        alertDialog.setTitle("Audio Still Recording");
                        alertDialog.setMessage("Are you sure to stop recording");
                        alertDialog.create().show();
                    } else {
                        navController.navigate(R.id.action_recordFragment_to_listFragment);
                    }
                    break;
            }
        });

        recordButton.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.record_btn:
                    if (isRecording) {
                        stopRecording();
                        recordButton.setImageDrawable(getResources().getDrawable(R.drawable.stopped));
                        isRecording = false;
                    } else {
                        if (checkPermission()) {
                            startRecording();
                            recordButton.setImageDrawable(getResources().getDrawable(R.drawable.recording));
                            isRecording = true;
                        }
                    }
            }
        });
    }

    private void startRecording() {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd__hh__mm__ss", Locale.CANADA);
        Date date = new Date();
        recordFile = "Recording_" + formatter.format(date) + ".3gp";
        recordText.setText("Recording, File Name : " + recordFile);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    private void stopRecording() {
        chronometer.stop();
        recordText.setText("Recording Stopped, File Saved : " + recordFile);
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) {
            stopRecording();
        }
    }
}