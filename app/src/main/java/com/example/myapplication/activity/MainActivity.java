package com.example.myapplication.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.SongsListAdapter;
import com.example.myapplication.model.SongsDataModel;
import com.example.myapplication.util.DownloadingSong;
import com.example.myapplication.util.JsonReader;
import com.example.myapplication.util.Utilities;
import com.example.myapplication.views.PlayPauseButton;
import com.example.myapplication.views.SongListListner;
import com.jaeger.library.StatusBarUtil;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, View.OnClickListener, SongListListner {
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private int mAlpha = StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA;


    private PlayPauseButton iconPlay;
    private MaterialIconView btnNext;
    private MaterialIconView btnPrevious;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songArtistLabel;
    private View btnPlay;


    private MediaPlayer mp;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private List<SongsDataModel> songsList;
    private SongsListAdapter adapter;
    private RecyclerView recyclerView;
    int currentSongIndex = 0;
    int[] totalDonwloadSize;
    NavigationView mainNavigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Save Path defined to app directory
        Utilities.Path = Environment.getExternalStorageDirectory() + "/Android/data/com.example.myapplication/songs";

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        //Set Transparent Toolbar here
        mAlpha = 0;
        StatusBarUtil.setTranslucentForDrawerLayout(MainActivity.this, mDrawerLayout, mAlpha);
        mToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        initializeViews();

        mainNavigationMenu = (NavigationView) findViewById(R.id.navigation);
        mainNavigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_all:
                        getSongsList();
                        break;
                    case R.id.nav_downloaded:
                        getDownloadList();
                        break;
                }

                mDrawerLayout.closeDrawers();

                return true;
            }
        });
    }

    @Override
    protected void setStatusBar() {
        //mStatusBarColor = getResources().getColor(R.color.colorPrimary);
        //StatusBarUtil.setColorForDrawerLayout(this, (DrawerLayout) findViewById(R.id.drawer_layout), mStatusBarColor, mAlpha);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

        }
        return false;
    }

    public void getSongsList() {
        songsList = JsonReader.getSongList(this);

        totalDonwloadSize = new int[songsList.size()];
        for (int i = 0; i < totalDonwloadSize.length; i++) {
            totalDonwloadSize[i] = 0;
        }

        adapter = new SongsListAdapter(this, songsList, this, totalDonwloadSize);
        recyclerView = (RecyclerView) findViewById(R.id.song_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void getDownloadList() {

        List<SongsDataModel> downloadList = new ArrayList<>();

        for (int i = 0; i < songsList.size(); i++) {
            if (Utilities.checkIsFileExit(songsList.get(i).getTitle().replaceAll("\\s+$", "") + ".mp3")) {
                downloadList.add(songsList.get(i));
            }
        }
        if (downloadList.size() > 0) {

            songsList = downloadList;
            adapter = new SongsListAdapter(this, songsList, this, totalDonwloadSize);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No downloaded songs found", Toast.LENGTH_SHORT).show();
        }
    }

    public void initializeViews() {
        // All player buttons
        iconPlay = (PlayPauseButton) findViewById(R.id.playpause);
        btnNext = (MaterialIconView) findViewById(R.id.next);
        btnPrevious = (MaterialIconView) findViewById(R.id.previous);
        songProgressBar = (SeekBar) findViewById(R.id.song_progress);
        songTitleLabel = (TextView) findViewById(R.id.song_title);
        songArtistLabel = (TextView) findViewById(R.id.song_artist);
        btnPlay = (View) findViewById(R.id.playpausewrapper);

        // Mediaplayer
        mp = new MediaPlayer();
        utils = new Utilities();
        songsList = new ArrayList<>();
        iconPlay.setColor(ContextCompat.getColor(this, android.R.color.white));


        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);

        getSongsList();


        Typeface face = Typeface.createFromAsset(getAssets(),
                "Roboto_Light.ttf");
        songTitleLabel.setTypeface(face);
        songArtistLabel.setTypeface(face);
    }

    public void updatePlayPauseButton(boolean isPlaying) {
        iconPlay.setPlayed(isPlaying);
        iconPlay.startAnimation();

        if (isPlaying) {
            adapter.currentlyPlayingPosition = currentSongIndex;
            adapter.notifyDataSetChanged();
        } else {
            adapter.currentlyPlayingPosition = -1;
            adapter.notifyDataSetChanged();
        }

    }

    public void playListSong(int songIndex) {
        // Play song
        try {


            final ProgressDialog progressDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
            progressDialog.setMessage("Loading audio...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            mp.stop();
            mp.reset();
            mp.release();


            //set player properties
            mp = new MediaPlayer();
            mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if (Utilities.checkIsFileExit(songsList.get(songIndex).getTitle().replaceAll("\\s+$", "") + ".mp3")) {
                File file1 = new File(Utilities.Path + "/" + songsList.get(songIndex).getTitle().replaceAll("\\s+$", "") + ".mp3");
                mp.setDataSource(file1.getPath());

            } else {
                mp.setDataSource(songsList.get(songIndex).getPath());

            }
            mp.prepareAsync();


            //mp3 will be started after completion of preparing...
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer player) {
                    player.start();
                    progressDialog.dismiss();
                }

            });


            // Displaying Song title
            String songTitle = songsList.get(songIndex).getTitle();
            songTitleLabel.setText(songTitle);

            updatePlayPauseButton(true);
            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                long totalDuration = mp.getDuration();
                long currentDuration = mp.getCurrentPosition();
                int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                songProgressBar.setProgress(progress);
                mHandler.postDelayed(this, 100);
            } catch (Exception e) {
            }
        }
    };


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
        mp.seekTo(currentPosition);
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        if (songProgressBar.getProgress() == 100) {
            if (currentSongIndex < songsList.size()) {
                currentSongIndex++;
                playListSong(currentSongIndex);
            } else {
                adapter.currentlyPlayingPosition = -1;
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "No more Songs", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playpausewrapper:
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        updatePlayPauseButton(false);
                    }
                } else {
                    if (mp != null) {
                        mp.start();
                        updatePlayPauseButton(true);
                    }
                }

                break;
            case R.id.next:
                if (songsList.size() > 1) {
                    if (currentSongIndex < songsList.size()) {
                        currentSongIndex++;
                        playListSong(currentSongIndex);
                    } else {
                        Toast.makeText(this, "No more Songs", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No more Songs", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.previous:
                if (currentSongIndex > 0) {
                    currentSongIndex--;
                    playListSong(currentSongIndex);
                } else {
                    currentSongIndex = 0;
                    Toast.makeText(this, "No more Songs", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSongClick(int pos) {
        currentSongIndex = pos;
        playListSong(currentSongIndex);

    }

    @Override
    public void onDownloadClick(int pos) {

        currentSongIndex = pos;

        String fileName = songsList.get(currentSongIndex).getTitle().replaceAll("\\s+$", "");
        String url = songsList.get(currentSongIndex).getPath();
        if (!Utilities.checkIsFileExit(fileName)) {
            DownloadingSong grabURL = new DownloadingSong();
            grabURL.setData(adapter, pos, this, totalDonwloadSize);
            StartAsyncTaskInParallel(grabURL, url, fileName + ".mp3");
            Toast.makeText(this, "Added to Download basket", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Already Downloaded", Toast.LENGTH_SHORT).show();

        }
    }

    //Parrelel task in above api level 11
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void StartAsyncTaskInParallel(DownloadingSong task, String url, String fileName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            String[] params = new String[2];
            params[0] = url;
            params[1] = fileName;
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else
            task.execute(url, fileName);
    }

}
