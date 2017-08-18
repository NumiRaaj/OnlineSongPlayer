package com.example.myapplication.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.data.MediaFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaison on 08/10/16.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {

    public List<MediaFolder> folderList = new ArrayList<>();
    public List<MediaFolder> selectedFolderList = new ArrayList<>();
    Context mContext;


    public FolderAdapter(Context context, List<MediaFolder> userList, List<MediaFolder> selectedList) {
        this.mContext = context;
        this.folderList = userList;
        this.selectedFolderList = selectedList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_folder, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MediaFolder movie = folderList.get(position);
        String message=movie.getDisplayName();
        message=Character.toUpperCase(message.charAt(0)) + message.substring(1);
        holder.folderName.setText(message);

        //FolderSelector action
        if (selectedFolderList.contains(folderList.get(position)))
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
        else
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));

        holder.folderCount.setText(""+movie.getNumberOfMediaFiles() +" media files");
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView folderName,folderCount;
        public ImageView folderIcon;
        public LinearLayout ll_listitem;

        public MyViewHolder(View view) {
            super(view);
            folderName = (TextView) view.findViewById(R.id.tv_folder_name);
            ll_listitem = (LinearLayout) view.findViewById(R.id.ll_listitem);
            folderCount=(TextView)view.findViewById(R.id.tv_folder_count);

        }
    }

}

