package com.example.soundrecorder;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

public class ListFragment extends Fragment implements Adapter.onItemListCLick {

    private ConstraintLayout constraintLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView recyclerView;
    private File[] files;

    private Adapter adapter;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay;
    private ImageButton play_btn;
    private TextView player_filename;
    private TextView player_header;
    private SeekBar seekBar;
    private Handler seekbarHandler;
    private Runnable runnabler;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        constraintLayout = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        recyclerView = view.findViewById(R.id.listView);
        play_btn = view.findViewById(R.id.play_btn);
        player_filename = view.findViewById(R.id.player_filename);
        player_header = view.findViewById(R.id.player_header_title);
        seekBar = view.findViewById(R.id.seekbar);

        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        files = directory.listFiles();
        adapter = new Adapter(files, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        play_btn.setOnClickListener(v -> {
            if (isPlaying){
                pauseAudio();
            }
            else{
                if (fileToPlay != null) {
                    resumeAudio();
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });
    }

    @Override
    public void onCLickListener(File file, int position) {
        fileToPlay = file;
        if (isPlaying) {
            stopAudio();
            playAudio(fileToPlay);
        } else {
            playAudio(fileToPlay);
        }
    }

    private void pauseAudio(){
        mediaPlayer.pause();
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_baseline_play_24));
        isPlaying = false;
        seekbarHandler.removeCallbacks(runnabler);
    }

    private void resumeAudio(){
        mediaPlayer.start();
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_baseline_pause_24));
        isPlaying = true;
        updateRunnable();
        seekbarHandler.postDelayed(runnabler,0);
    }
    @SuppressLint("NewApi")
    private void stopAudio() {
        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_baseline_play_24,null));
        player_header.setText("Stopped");
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(runnabler);
    }

    @SuppressLint("NewApi")
    private void playAudio(File fileToPlay) {
        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        play_btn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_baseline_pause_24,null));
        player_filename.setText(fileToPlay.getName());
        player_header.setText("Playing");
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(mp -> {
            stopAudio();
            player_header.setText("Finished");
        });
        seekBar.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(runnabler,0);
    }

    private void updateRunnable() {
        runnabler = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this,500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying) {
            stopAudio();
        }
    }
}