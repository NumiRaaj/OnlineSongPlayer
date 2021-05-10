package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.myapplication.R;
import com.example.myapplication.data.MediaFolder;
import com.example.myapplication.fragment.FolderDetailFragment;
import com.example.myapplication.fragment.FolderFragment;
import com.example.myapplication.util.AlertDialogHelper;
import com.example.myapplication.util.ParseFolder;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by Administrator on 8/16/2017.
 */

public class MainActivity extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener {

    public ActionMode mActionMode;
    public Menu context_menu;
    public Menu mActivityMenu;
    public FragmentManager mFragmentManager;
    public FragmentTransaction mFragmentTransaction;
    public AlertDialogHelper alertDialogHelper;


    public List<MediaFolder> folderList = new ArrayList<>();
    public List<String> selectedFolderList = new ArrayList<>();
    public FolderFragment mFolderFragment;
    public FolderDetailFragment mFolderDetailFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertDialogHelper = new AlertDialogHelper(this);

        //Initialize Home fragment here
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.add(R.id.container, new FolderFragment().getInstance(this)).commit();

    }

    public void fragmentReplace(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (!(fragment instanceof FolderFragment)) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common_activity, menu);

        mActivityMenu = menu;


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:

                return true;

            case R.id.action_refresh:

                if (mFolderFragment != null) {
                    mFolderFragment.findVideoDataList();
                }

                //for folder detail item reomve
                if (mFolderDetailFragment != null) {
                    mFolderDetailFragment.findVideoDataList(mFolderDetailFragment.path);
                }


                return true;
            case R.id.action_exit:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("", "Delete Contact", "DELETE", "CANCEL", 1, false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if (mFolderFragment != null) {
                mFolderFragment.findVideoDataList();
                mFolderFragment.isMultiSelect=false;
            }
            //for folder detail item reomve
            if (mFolderDetailFragment != null) {
                mFolderDetailFragment.findVideoDataList(mFolderDetailFragment.path);
                mFolderDetailFragment.isMultiSelect=false;
            }
        }
    };



    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if (from == 1) {
            //Now Delete the folder whole
            if (selectedFolderList.size() > 0) {
                ParseFolder folder = new ParseFolder();

                if (mFolderFragment != null) {
                    for (int i = 0; i < selectedFolderList.size(); i++) {
                        folderList.remove(selectedFolderList.get(i));
                        folder.deleteFolder(this, selectedFolderList.get(i));
                    }
                    mFolderFragment.findVideoDataList();
                }

                //for folder detail item reomve
                if (mFolderDetailFragment != null) {
                    ParseFolder parseFolder=new ParseFolder();
                    for (int i = 0; i < selectedFolderList.size(); i++) {
                        parseFolder.deleteViaContentProvider(this,selectedFolderList.get(i));
                    }
                    mFolderDetailFragment.findVideoDataList(mFolderDetailFragment.path);
                }

                if (mActionMode != null) {
                    mActionMode.finish();
                }
                selectedFolderList.clear();
            }

        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }


}
