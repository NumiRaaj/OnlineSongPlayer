package com.example.myapplication.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.activity.CustomPlayer;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.adapter.FolderDetailAdapter;
import com.example.myapplication.data.AudioModel;
import com.example.myapplication.data.SongModel;
import com.example.myapplication.util.ParseFolder;
import com.example.myapplication.util.folderItemListClick;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 8/16/2017.
 */

public class FolderDetailFragment extends Fragment {

    MainActivity mainActivity;
    RecyclerView recyclerView;
    FolderDetailAdapter folderListAdapter;
    public boolean isMultiSelect = false;
    public List<SongModel> folderList = new ArrayList<>();
    List<SongModel> selectedFolderList = new ArrayList<>();
    public String path, folderName;

    public FolderDetailFragment getInstance(MainActivity mainActivity, String path, String folderName) {
        FolderDetailFragment fragment = new FolderDetailFragment();
        fragment.mainActivity = mainActivity;
        fragment.path = path;
        fragment.folderName = folderName;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_contextual_menu, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        findVideoDataList(path);

        return v;
    }

    public void findVideoDataList(String path) {
        //Get folder data
        ParseFolder parseFolder = new ParseFolder();
        Cursor cursor = null;
        cursor = parseFolder.getMediaDetailList(mainActivity, path);


        List<SongModel> listFolders = setMediaFolderData(cursor);//for video folders
        folderList = listFolders;
        if (folderList == null) {
            return;
        }

        if (folderList.size() > 0) {
            folderListAdapter = new FolderDetailAdapter(mainActivity, folderList, selectedFolderList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainActivity);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(folderListAdapter);
            recyclerView.addOnItemTouchListener(new folderItemListClick(mainActivity, recyclerView, new folderItemListClick.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position == -1) {

                        return;
                    }
                    if (isMultiSelect)
                        multi_select(position);
                    else {
                        Intent i = new Intent(mainActivity, CustomPlayer.class);
                        i.putExtra("pos", position);
                        i.putExtra("LIST", (Serializable) folderList);
                        mainActivity.startActivity(i);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    if (!isMultiSelect) {
                        selectedFolderList = new ArrayList<SongModel>();
                        isMultiSelect = true;
                    }
                    if (position == -1) {
                        return;

                    } else {

                        mainActivity.mActionMode = mainActivity.startActionMode(mainActivity.mActionModeCallback);
                        multi_select(position);

                    }
                }
            }));
        }
    }


    public void multi_select(int position) {
        if (mainActivity.mActionMode != null) {
            if (selectedFolderList.contains(folderList.get(position)))
                selectedFolderList.remove(folderList.get(position));
            else
                selectedFolderList.add(folderList.get(position));

            if (selectedFolderList.size() > 0)
                mainActivity.mActionMode.setTitle("" + selectedFolderList.size());
            else
                mainActivity.mActionMode.setTitle("");

            refreshAdapter();

        }
    }


    public void refreshAdapter() {
        folderListAdapter.selectedFolderList = selectedFolderList;
        folderListAdapter.folderList = folderList;
        folderListAdapter.notifyDataSetChanged();

        List<String> selectedPath = new ArrayList<>();
        for (int i = 0; i < selectedFolderList.size(); i++) {
            selectedPath.add(selectedFolderList.get(i).getDATA());
        }
        mainActivity.selectedFolderList = selectedPath;

    }


    private List<SongModel> setMediaFolderData(Cursor folderList) {

        List<SongModel> mediaFolders = new ArrayList();
        if (folderList != null && folderList.moveToFirst()) {

            do {
                File file = new File(folderList.getString(folderList.getColumnIndex(AudioModel.DATA)));
                if (file.exists()) {
                    SongModel mediaFolder = new SongModel();
                    mediaFolder.setDISPLAY_NAME(folderList.getString(folderList.getColumnIndex(AudioModel.DISPLAY_NAME)));
                    mediaFolder.set_ID(folderList.getString(folderList.getColumnIndex(AudioModel._ID)));
                    mediaFolder.setDATA(folderList.getString(folderList.getColumnIndex(AudioModel.DATA)));
                    mediaFolder.setDURATION(folderList.getString(folderList.getColumnIndex(AudioModel.DURATION)));
                    mediaFolder.setSIZE(folderList.getString(folderList.getColumnIndex(AudioModel.SIZE)));

                    mediaFolders.add(mediaFolder);
                }

            } while (folderList.moveToNext());


        } else {

            return null;
        }

        return mediaFolders;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.getSupportActionBar().setTitle(folderName);
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainActivity.mFolderDetailFragment = this;
        mainActivity.mFolderFragment = null;
    }
}
