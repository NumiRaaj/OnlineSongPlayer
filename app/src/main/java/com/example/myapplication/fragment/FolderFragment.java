package com.example.myapplication.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.adapter.FolderAdapter;
import com.example.myapplication.data.MediaFolder;
import com.example.myapplication.util.ParseFolder;
import com.example.myapplication.util.folderItemListClick;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.path;

/**
 * Created by Administrator on 8/16/2017.
 */

public class FolderFragment extends Fragment {
    MainActivity mainActivity;
    public boolean isMultiSelect = false;
    List<MediaFolder> folderList = new ArrayList<>();
    List<MediaFolder> selectedFolderList = new ArrayList<>();
    RecyclerView recyclerView;
    FolderAdapter folderListAdapter;


    public FolderFragment getInstance(MainActivity mainActivity) {
        FolderFragment fragment = new FolderFragment();
        fragment.mainActivity = mainActivity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_contextual_menu, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

       // findVideoDataList();

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                findVideoDataList();
            }
        }
    }
    public void findVideoDataList() {
        //Get folder data
        ParseFolder parseFolder = new ParseFolder();
        List<MediaFolder> listFolders = setMediaFolderData(parseFolder.getMediaFolderList(mainActivity), false);//for video folders

        folderList = listFolders;

        if (folderList == null) {
            return;
        }
        if (folderList.size() > 0) {
            folderListAdapter = new FolderAdapter(mainActivity, folderList, selectedFolderList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mainActivity);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(folderListAdapter);
            recyclerView.addOnItemTouchListener(new folderItemListClick(mainActivity, recyclerView, new folderItemListClick.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (isMultiSelect) {
                        multi_select(position);
                    } else {
                        mainActivity.fragmentReplace(new FolderDetailFragment().getInstance(mainActivity, folderList.get(position).getPath(), folderList.get(position).getDisplayName()));
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    if (!isMultiSelect) {
                        selectedFolderList = new ArrayList<MediaFolder>();
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
            selectedPath.add(selectedFolderList.get(i).getPath());
        }
        mainActivity.selectedFolderList = selectedPath;
    }

    private ArrayList<MediaFolder> setMediaFolderData(Cursor folderList, boolean isAudioList) {

        ArrayList<MediaFolder> mediaFolders = new ArrayList();
        if (folderList != null && folderList.getCount() > 0) {
            int i = 0;
            while (folderList.moveToNext()) {
                String data;
                String fileName;
                MediaFolder mediaFolder = new MediaFolder();
                data = folderList.getString(folderList.getColumnIndex("_data"));
                fileName = folderList.getString(folderList.getColumnIndex("_display_name"));

                mediaFolder.setPath(data.replace("/" + fileName, ""));
                mediaFolder.setDisplayName(mediaFolder.getPath().substring(mediaFolder.getPath().lastIndexOf("/") + 1));
                //check total number of files in follder
                File dir = new File(mediaFolder.getPath());
                File[] files = dir.listFiles();
                int numberOfFiles = files.length;
                File[] list = files;
                int count = 0;
                for (File f : list) {
                    String name = f.getName();
                    Log.e("file name", "" + name);
                    if (!fileName.equals("facebook_ringtone_pop.m4a"))
                    {
                        if (name.endsWith(".mp3") || name.endsWith(".m4a") || name.endsWith(".wav")
                                || name.endsWith(".avi") || name.endsWith(".mp4")
                                || name.endsWith(".mkv") || name.endsWith(".3gp")
                                || name.endsWith(".aac") || name.endsWith(".TS")
                                || name.endsWith(".webm") || name.endsWith(".mov")
                                )
                            count++;
                    }
                }
                mediaFolder.setNumberOfMediaFiles(count);

                if (count > 0) {
                    mediaFolders.add(mediaFolder);
                }

            }
        } else {

            Log.e("No", "Folder exites here");
            return null;
        }

        return mediaFolders;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);//To show back Arrow

        mainActivity.mFolderDetailFragment = null;
        mainActivity.mFolderFragment = this;
    }
}
