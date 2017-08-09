package com.example.myapplication.util;

import android.content.Context;
import android.util.Log;

import com.example.myapplication.model.SongsDataModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 8/8/2017.
 */

public class JsonReader {
    public static List<SongsDataModel> getSongList(Context context) {
        List<SongsDataModel> songList = new ArrayList<>();
        //get songs list from json file in assets
        try {
            InputStream is = context.getAssets().open("json/songs.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String bufferString = new String(buffer);
            String data = bufferString.trim();
            data = data.replace("\n", "");
            data = data.replace("\r", "");

            JSONObject object = new JSONObject(data);

            JSONArray array = object.getJSONArray("Audio");
            Log.d("array COUNT", String.valueOf(array.length()));
            for (int i = 0; i < array.length(); i++) {
                object = array.getJSONObject(i);

                int id = object.getInt("Id");
                String title = object.getString("Title");
                String artist = object.getString("Artist");
                String album = object.getString("Album");
                String path = object.getString("SLinkLow");
                String duration = object.getString("Dura");
                if (duration.equalsIgnoreCase(""))
                    duration = "0";

                songList.add(new SongsDataModel(id, title, artist, album, path, Long.parseLong(duration)));
            }
            /*Collections.sort(songList, new Comparator<Song>() {
                public int compare(Song a, Song b) {
                    return a.getTitle().toLowerCase().compareTo(b.getTitle().toLowerCase());
                }
            });*/
        } catch (Exception e) {
            Log.d("error", e.getMessage());
            return null;
        }
        return songList;
    }

}
