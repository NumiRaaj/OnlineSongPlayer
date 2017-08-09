package com.example.myapplication.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.SongsDataModel;
import com.example.myapplication.util.Utilities;
import com.example.myapplication.views.MusicVisualizer;
import com.example.myapplication.views.SongListListner;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;

/**
 * Created by Administrator on 8/8/2017.
 */

public class SongsListAdapter extends RecyclerView.Adapter<SongsListAdapter.ItemHolder> {

    public int currentlyPlayingPosition;
    private List<SongsDataModel> arraylist;
    private AppCompatActivity mContext;
    SongListListner listner;
    int[] downloadingPos;

    public SongsListAdapter(AppCompatActivity context, List<SongsDataModel> arraylist, SongListListner listner, int[] downloadingPos) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.listner = listner;
        this.downloadingPos = downloadingPos;

        currentlyPlayingPosition = -1;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song, null);
        ItemHolder ml = new ItemHolder(v);
        return ml;

    }

    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, final int i) {
      final  SongsDataModel localItem = arraylist.get(i);
        itemHolder.serial.setText("" + (i+1));
        itemHolder.title.setText(localItem.getTitle().replaceAll("\\s+$", ""));

        Typeface face = Typeface.createFromAsset(mContext.getAssets(),
                "Roboto_Light.ttf");
        itemHolder.title.setTypeface(face);



        itemHolder.artist.setText(localItem.getArtist());
        itemHolder.visualizer.setVisibility(View.GONE);
        itemHolder.title.setTextColor(Color.WHITE);
        itemHolder.artist.setTextColor(Color.WHITE);
        itemHolder.serial.setTextColor(Color.WHITE);
        if (currentlyPlayingPosition == i) {
            itemHolder.visualizer.setVisibility(View.VISIBLE);
            itemHolder.visualizer.setColor(mContext.getResources().getColor(R.color.golden));
            itemHolder.title.setTextColor(mContext.getResources().getColor(R.color.golden));
            itemHolder.artist.setTextColor(mContext.getResources().getColor(R.color.golden));
            itemHolder.serial.setTextColor(mContext.getResources().getColor(R.color.golden));
        }



        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onSongClick(i);
            }
        });

        itemHolder.btnDownload.setVisibility(View.VISIBLE);

        if (downloadingPos[i] > 0 && downloadingPos[i] <= 100) {
            itemHolder.btnDownload.setVisibility(View.GONE);
            itemHolder.pBar.setMax(100);
            itemHolder.pBar.setProgress(downloadingPos[i]);
            itemHolder.pBar.setVisibility(View.VISIBLE);
        } else {
            itemHolder.pBar.setProgress(0);
            itemHolder.pBar.setVisibility(View.GONE);
        }

        if (Utilities.checkIsFileExit(localItem.getTitle().replaceAll("\\s+$", "") + ".mp3")) {
            itemHolder.btnDownload.setVisibility(View.GONE);
        } else {

            itemHolder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listner.onDownloadClick(i);
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        protected TextView title, artist, serial;
        public MusicVisualizer visualizer;
        public ProgressBar pBar;
        public MaterialIconView btnDownload;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.song_title);
            this.artist = (TextView) view.findViewById(R.id.song_artist);
            visualizer = (MusicVisualizer) view.findViewById(R.id.visualizer);
            this.serial = (TextView) view.findViewById(R.id.song_serial);
            this.pBar = (ProgressBar) view.findViewById(R.id.progressBar);
            this.btnDownload = (MaterialIconView) view.findViewById(R.id.btn_download);

        }


    }


}

