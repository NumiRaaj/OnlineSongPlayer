package com.codeempire.jetplayer.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bullhead.equalizer.DialogEqualizerFragment;
import com.bullhead.equalizer.EqualizerModel;
import com.bullhead.equalizer.Settings;
import com.codeempire.jetplayer.R;
import com.codeempire.jetplayer.adapter.FolderDetailAdapter;
import com.codeempire.jetplayer.data.SongModel;
import com.codeempire.jetplayer.util.ParseFolder;
import com.codeempire.jetplayer.util.folderItemListClick;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tcking.github.com.giraffeplayer.GiraffePlayer;


/**
 * Created by Administrator on 4/20/2017.
 */

public class CustomPlayer extends AppCompatActivity {

    public static final String PREF_KEY = "equalizer";
    GiraffePlayer player;
    List<SongModel> songModelList;
    int currentPos;
    RelativeLayout containerPlayList;
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


    @Override
    protected void onStop() {
        super.onStop();
        saveEqualizerSettings();

    }

    //Handel screen rotation
    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    public void setupPlayer() {
        Log.e("video player", "setting up player");

        currentPos = getIntent().getIntExtra("pos", 0);
        songModelList = (List<SongModel>) getIntent().getSerializableExtra("LIST");
        player = new GiraffePlayer(this);
        //player.play(url);
        list = new ArrayList<>();
        for (int i = 0; i < songModelList.size(); i++) {
            tcking.github.com.giraffeplayer.SongModel model = new tcking.github.com.giraffeplayer.SongModel();
            model.setDATA(songModelList.get(i).getDATA());
            model.setDISPLAY_NAME(songModelList.get(i).getDISPLAY_NAME());
            model.setDURATION(songModelList.get(i).getDURATION());


            //Check song format
            String url = songModelList.get(i).getDATA();
            String filenameArray[] = url.split("\\.");
            String extension = filenameArray[filenameArray.length - 1];
            if (extension.equalsIgnoreCase("mp3") ||
                    extension.equalsIgnoreCase("wav") ||
                    extension.equalsIgnoreCase("m4a") ||
                    extension.equalsIgnoreCase("aac")) {
                model.setIsMp3(true);


            } else {
                model.setIsMp3(false);

            }

            ///*********************////
            list.add(model);
        }

        //  player.setFullScreenOnly(false);
        // player.tryFullScreen(true);


        //Set full screen activity
        player.playListPlayer(list, currentPos);

        ImageView img = (ImageView) findViewById(R.id.app_video_equilizer);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadEqualizerSettings();
                DialogEqualizerFragment fragment = DialogEqualizerFragment.newBuilder()
                        .setAudioSessionId(new MediaPlayer().getAudioSessionId())
                        .themeColor(ContextCompat.getColor(CustomPlayer.this, R.color.primaryColor))
                        .textColor(ContextCompat.getColor(CustomPlayer.this, R.color.textColor))
                        .accentAlpha(ContextCompat.getColor(CustomPlayer.this, R.color.playingCardColor))
                        .darkColor(ContextCompat.getColor(CustomPlayer.this, R.color.primaryDarkColor))
                        .setAccentColor(ContextCompat.getColor(CustomPlayer.this, R.color.secondaryColor))

                        .build();
                fragment.show(getSupportFragmentManager(), "eq");
            }
        });


        containerPlayList = (RelativeLayout) findViewById(R.id.container_video_list);
        ImageView imgPlaylistBtn = (ImageView) findViewById(R.id.video_playlist);
        imgPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (containerPlayList.getVisibility() == View.VISIBLE) {
                    containerPlayList.setVisibility(View.GONE);
                } else {
                    containerPlayList.setVisibility(View.VISIBLE);

                }
            }
        });


        //Playlist
        ImageView imgCrossButton = (ImageView) findViewById(R.id.video_list_close);
        imgCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                containerPlayList.setVisibility(View.GONE);
            }
        });


        FolderDetailAdapter folderListAdapter = new FolderDetailAdapter(CustomPlayer.this, songModelList, songModelList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.videoplaylist_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CustomPlayer.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(folderListAdapter);

        recyclerView.addOnItemTouchListener(new folderItemListClick(CustomPlayer.this, recyclerView, new folderItemListClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == -1) {
                    return;
                }
                player.playListOperate(position);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

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
            ParseFolder.currentSongIndex = player.currentListIndex;
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

    private void saveEqualizerSettings() {
        if (Settings.equalizerModel != null) {
            EqualizerSetting settings = new EqualizerSetting();
            settings.bassStrength = Settings.equalizerModel.getBassStrength();
            settings.presetPos = Settings.equalizerModel.getPresetPos();
            settings.reverbPreset = Settings.equalizerModel.getReverbPreset();
            settings.seekbarpos = Settings.equalizerModel.getSeekbarpos();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

            Gson gson = new Gson();
            preferences.edit()
                    .putString(PREF_KEY, gson.toJson(settings))
                    .apply();
        }
    }

    private void loadEqualizerSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        EqualizerSetting settings = gson.fromJson(preferences.getString(PREF_KEY, "{}"), EqualizerSetting.class);
        EqualizerModel model = new EqualizerModel();
        model.setBassStrength(settings.bassStrength);
        model.setPresetPos(settings.presetPos);
        model.setReverbPreset(settings.reverbPreset);
        model.setSeekbarpos(settings.seekbarpos);

        Settings.isEqualizerEnabled = true;
        Settings.isEqualizerReloaded = true;
        Settings.bassStrength = settings.bassStrength;
        Settings.presetPos = settings.presetPos;
        Settings.reverbPreset = settings.reverbPreset;
        Settings.seekbarpos = settings.seekbarpos;
        Settings.equalizerModel = model;
    }
}
