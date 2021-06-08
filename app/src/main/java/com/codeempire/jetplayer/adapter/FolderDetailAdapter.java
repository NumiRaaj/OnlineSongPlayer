package com.codeempire.jetplayer.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codeempire.jetplayer.R;
import com.codeempire.jetplayer.data.SongModel;
import com.codeempire.jetplayer.util.ParseFolder;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Jaison on 08/10/16.
 */

public class FolderDetailAdapter extends RecyclerView.Adapter<FolderDetailAdapter.MyViewHolder> {

    public List<SongModel> folderList = new ArrayList<>();
    public List<SongModel> selectedFolderList = new ArrayList<>();
    Context mContext;
    ParseFolder parseFolder;

    public FolderDetailAdapter(Context context, List<SongModel> userList, List<SongModel> selectedList) {
        this.mContext = context;
        this.folderList = userList;
        this.selectedFolderList = selectedList;
        parseFolder = new ParseFolder();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_folder_detail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SongModel movie = folderList.get(position);


        String message = parseFolder.removeExtension(movie.getDISPLAY_NAME());
        message = Character.toUpperCase(message.charAt(0)) + message.substring(1);
        holder.folderName.setText(message);


        //FolderSelector action
        if (selectedFolderList.contains(folderList.get(position)))
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
        else
            holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));

        if (movie.getDURATION() != null) {
            int dur = Integer.parseInt(movie.getDURATION());
            int hrs = (dur / 3600000);
            int mns = (dur / 60000) % 60000;
            int scs = dur % 60000 / 1000;
            String songTime = String.format("%2d:%2d", mns, scs);
            dur = Integer.parseInt(movie.getDURATION());


            //For Video File Duration
            mns = (dur % 3600000) / 60000;
            scs = ((dur % 3600000) % 60000) / 1000;
            if (dur / 3600000 > 0) {
                songTime = String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(dur / 3600000), Integer.valueOf(mns), Integer.valueOf(scs)});
            } else {
                songTime = String.format("%02d:%02d", new Object[]{Integer.valueOf(mns), Integer.valueOf(scs)});
            }
            //*************** End *****************//
            holder.duration.setText(songTime);
        }
        //For thumbnail
        long ids = Long.parseLong(movie.get_ID());
        ContentResolver crThumb = mContext.getContentResolver();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap thmb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, ids, 1, options);

        if (thmb != null) {
            holder.thumbnail.setImageBitmap(thmb);
        } else {
            holder.thumbnail.setImageResource(R.color.thumb_color);
        }
        crThumb = null;//Now make it null for every row
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView folderName, duration;
        public ImageView thumbnail;
        public LinearLayout ll_listitem;

        public MyViewHolder(View view) {
            super(view);
            folderName = (TextView) view.findViewById(R.id.tv_folder_name);
            ll_listitem = (LinearLayout) view.findViewById(R.id.ll_listitem);
            duration = (TextView) view.findViewById(R.id.tv_duration);
            thumbnail = (ImageView) view.findViewById(R.id.item_thumbnail);
        }
    }

}

