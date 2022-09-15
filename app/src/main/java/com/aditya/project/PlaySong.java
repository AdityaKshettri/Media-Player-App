package com.aditya.project;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    private TextView textView;
    private ImageView prev, play, next;
    private SeekBar seekBar;

    private ArrayList<File> songs;
    private MediaPlayer mediaPlayer;
    private String textContent;
    private int position;
    private Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        initializeViews();

        fetchDataFromIntent();

        initializeTextView();

        initializeMediaPlayer(this);

        initializeSeekBar();

        initializePlayButton();

        initializePrevButton();

        initializeNextButton();
    }

    private void initializeViews() {
        textView = findViewById(R.id.textView);
        prev = findViewById(R.id.prev);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
    }

    private void fetchDataFromIntent() {
        Intent intent = getIntent();
        songs = (ArrayList) intent.getExtras().getParcelableArrayList("songs");
        textContent = intent.getStringExtra("currSong");
        position = intent.getIntExtra("position", 0);
    }

    private void initializeTextView() {
        textView.setText(textContent);
        textView.setSelected(true);
    }

    private void initializeMediaPlayer(Context context) {
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.start();
    }

    private void initializeSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread() {
            @Override
            public void run() {
                int currPos = 0;
                try {
                    while (currPos < mediaPlayer.getDuration()) {
                        currPos = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currPos);
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
    }

    private void initializePlayButton() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
    }

    private void initializePrevButton() {
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0) {
                    position--;
                } else {
                    position = songs.size() - 1;
                }
                initializeMediaPlayer(getApplicationContext());
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
            }
        });
    }

    private void initializeNextButton() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != songs.size() - 1) {
                    position++;
                } else {
                    position = 0;
                }
                initializeMediaPlayer(getApplicationContext());
                play.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }
}