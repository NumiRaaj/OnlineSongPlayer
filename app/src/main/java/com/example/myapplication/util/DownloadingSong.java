package com.example.myapplication.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.myapplication.adapter.SongsListAdapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 8/8/2017.
 */

public class DownloadingSong extends AsyncTask<String, Integer, String> {
    int pos;
    SongsListAdapter songAdt;
    Context context;
    int[] totalDonwloadSize;

    public void setData( SongsListAdapter songAdt,int pos, Context context, int[] totalDonwloadSize) {
        this.pos = pos;
        this.context = context;
        this.songAdt=songAdt;
        this.totalDonwloadSize = totalDonwloadSize;
    }

    protected String doInBackground(String... urls) {

        String filename = urls[1];


        File file = new File(Utilities.Path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(file.getAbsolutePath() + "/" + filename);

        try {
            URL url = new URL(urls[0]);
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileSize = connection.getContentLength();

            InputStream is = new BufferedInputStream(url.openStream());
            OutputStream os = new FileOutputStream(file1);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = is.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / fileSize));
                os.write(data, 0, count);
            }

            os.flush();
            os.close();
            is.close();

        } catch (Exception e) {
            Log.e("Error", e.toString());//
        }

        return filename;

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
       // songAdt.currentlyPlayingPosition = pos;
        totalDonwloadSize[pos] = progress[0];
        songAdt.notifyDataSetChanged();


    }

    @Override
    protected void onCancelled() {
        Toast toast = Toast.makeText(context, "Error connecting to Server", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();

    }

    @Override
    protected void onPostExecute(String filename) {
      //  songAdt.currentlyPlayingPosition = pos;
        totalDonwloadSize[pos] = 100;
        songAdt.notifyDataSetChanged();

    }

    protected void onPreExecute() {
      //  songAdt.currentlyPlayingPosition = pos;
        songAdt.notifyDataSetChanged();


    }

}
