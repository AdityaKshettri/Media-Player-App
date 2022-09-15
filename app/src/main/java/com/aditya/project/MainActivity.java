package com.aditya.project;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(MainActivity.this, "Runtime Permission given!", Toast.LENGTH_SHORT).show();
                        ArrayList<File> songs = fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[songs.size()];
                        for (int i = 0; i < songs.size(); i++) {
                            items[i] = songs.get(i).getName().replace(".mp3", "");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                String currSong = listView.getItemAtPosition(i).toString();
                                intent.putExtra("songs", songs);
                                intent.putExtra("currSong", currSong);
                                intent.putExtra("position", i);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(MainActivity.this, "Runtime Permission denied!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    private ArrayList<File> fetchSongs(File file) {
        ArrayList<File> list = new ArrayList<>();
        File[] songs = file.listFiles();
        if (songs != null) {
            for (File song : songs) {
                if (!song.isHidden() && song.isDirectory()) {
                    list.addAll(fetchSongs(song));
                } else {
                    if (song.getName().endsWith(".mp3") && !song.getName().startsWith(".")) {
                        list.add(song);
                    }
                }
            }
        }
        return list;
    }
}