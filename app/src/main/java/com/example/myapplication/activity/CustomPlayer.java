package com.example.myapplication.activity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.data.SongModel;
import com.example.myapplication.util.ParseFolder;

import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer.GiraffePlayer;


/**
 * Created by Administrator on 4/20/2017.
 */

public class CustomPlayer extends AppCompatActivity {

    GiraffePlayer player;
    List<SongModel> songModelList;
    int currentPos;

    boolean isHomePressed = false;
    List<tcking.github.com.giraffeplayer.SongModel> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_custom_player);
        //For screen orientation changes checked

        setupPlayer();
        if (savedInstanceState != null) {
            player.playListPlayer(list, ParseFolder.currentSongIndex);
            player.seekTo(ParseFolder.seekBeforeRotation, true);

        }
    }

    //Handel screen rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    public void setupPlayer() {
        currentPos = getIntent().getIntExtra("pos", 0);
        songModelList = (List<SongModel>) getIntent().getSerializableExtra("LIST");
        player = new GiraffePlayer(this);
        //player.play(url);
       list = new ArrayList<>();
        for (int i = 0; i < songModelList.size(); i++) {
            tcking.github.com.giraffeplayer.SongModel model = new tcking.github.com.giraffeplayer.SongModel();
            model.setDATA(songModelList.get(i).getDATA());
            model.setDISPLAY_NAME(songModelList.get(i).getDISPLAY_NAME());

            list.add(model);
        }

        player.setFullScreenOnly(true);
       // player.tryFullScreen(true);
        player.playListPlayer(list, currentPos);


    }

    @Override
    protected void onPause() {
        super.onPause();
        //Check wheater playing mp3 or not
        if (player.isMp3Song) {
            detectScreenLocking();
        } else {
            isHomePressed = false;
        }

        if (!isHomePressed) {
            if (player != null) {
                player.onPause();
            }
        }
    }

    private void detectScreenLocking() {
        // If the screen is off then the device has been locked
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            isScreenOn = powerManager.isInteractive();
        } else {
            isScreenOn = powerManager.isScreenOn();
        }
        if (!isScreenOn) {
            isHomePressed = true;
        }

    }

    //For detection home key pressed
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        isHomePressed = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        isHomePressed = false;

    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            //Getting current postion of seek bar while moving orientation
            ParseFolder.seekBeforeRotation = player.getCurrentPosition();
            ParseFolder.currentSongIndex=player.currentListIndex;
            player.onDestroy();

        }
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
