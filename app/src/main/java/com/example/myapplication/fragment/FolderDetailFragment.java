package com.example.myapplication.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;

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

        //findVideoDataList(path);

        return v;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                findVideoDataList(path);
            }
        }
    }
    public void findVideoDataList(String path) {
        //Get folder data
        ParseFolder parseFolder = new ParseFolder();
        List<SongModel> listFolders = setMediaFolderData(parseFolder.getMediaDetailList(mainActivity, path));//for video folders
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
                    if (isMultiSelect)
                        multi_select(position);
                    else {
                        Intent i = new Intent(mainActivity, CustomPlayer.class);
                        i.putExtra("uri", folderList.get(position).getDATA());
                        i.putExtra("title", folderList.get(position).getDISPLAY_NAME());
                        i.putExtra("pos",position);
                        i.putExtra("LIST", (Serializable) folderList);
                        mainActivity.startActivity(i);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    if (!isMultiSelect) {
                        selectedFolderList = new ArrayList<SongModel>();
                        isMultiSelect = true;
                        if (mainActivity.mActionMode == null) {
                            mainActivity.mActionMode = mainActivity.startActionMode(mainActivity.mActionModeCallback);
                        }
                    }
                    multi_select(position);
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
                SongModel mediaFolder = new SongModel();
                mediaFolder.setDISPLAY_NAME(folderList.getString(folderList.getColumnIndex(AudioModel.DISPLAY_NAME)));
                mediaFolder.set_ID(folderList.getString(folderList.getColumnIndex(AudioModel._ID)));
                mediaFolder.setDATA(folderList.getString(folderList.getColumnIndex(AudioModel.DATA)));
                mediaFolder.setDURATION(folderList.getString(folderList.getColumnIndex(AudioModel.DURATION)));
                mediaFolder.setSIZE(folderList.getString(folderList.getColumnIndex(AudioModel.SIZE)));
                mediaFolders.add(mediaFolder);
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
