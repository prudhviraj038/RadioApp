package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SongAdapter extends BaseAdapter{
    String [] result;
    Context context;
    int [] imageId;
    ArrayList<SongModel> artists;
    SongListActivity songListActivity;
    private static LayoutInflater inflater=null;

    public SongAdapter(Activity mainActivity, ArrayList<SongModel> artists,SongListActivity fragment) {
        // TODO Auto-generated constructor stub
      //  result=prgmNameList;
        context=mainActivity;
        this.artists=artists;
        this.songListActivity = fragment;
      //  imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return artists.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv,tvartist;
        CircleImageView img;
        ImageView imgpop,download,you;
        ImageView itunes;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.preview_song_list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.song_name);
        holder.tvartist = (TextView) rowView.findViewById(R.id.album_name);
        holder.img=(CircleImageView) rowView.findViewById(R.id.song_image);
        holder.imgpop = (ImageView) rowView.findViewById(R.id.pop_up_menu);
        holder.tv.setText(artists.get(position).getSongName());
        holder.tvartist.setText(artists.get(position).getArtist_Name());

        holder.download = (ImageView) rowView.findViewById(R.id.download_btn);
        holder.you = (ImageView) rowView.findViewById(R.id.youtube_btn);
        holder.itunes = (ImageView) rowView.findViewById(R.id.itunes_btn);
        if(artists.get(position).itunes.equals(""))
            holder.itunes.setVisibility(View.INVISIBLE);
        else
            holder.itunes.setVisibility(View.VISIBLE);
        if(artists.get(position).youtube.equals(""))
            holder.you.setVisibility(View.INVISIBLE);
        else
            holder.you.setVisibility(View.VISIBLE);

        holder.you.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                songListActivity.youtubeselected(position);
            }
        });

        holder.itunes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                songListActivity.ituneselected(position);
            }
        });

        holder.download.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //downloadFilefromlist(artists.get(position));
                Settings.downloadFilefromlist(artists.get(position),context);
                holder.download.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "download started", Toast.LENGTH_SHORT).show();
            }
        });

        final DatabaseHandler db = new DatabaseHandler(context);
        if(db.getContactcount(artists.get(position).getSongId()) == 0){
            holder.download.setVisibility(View.VISIBLE);
        }
        else{
            holder.download.setVisibility(View.INVISIBLE);
        }
        db.close();

        Picasso.with(context).load(artists.get(position).getImage()).fit().into(holder.img);

        holder.imgpop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(context, holder.imgpop);
                popup.getMenuInflater().inflate(R.menu.menu_pop_up, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.action_settings) {
                            //do something
                            songListActivity.addtoqueue(position);
                            return true;
                        } else {
                            return onMenuItemClick(item);
                        }
                    }
                });

                popup.show();
            }
        });
      //  holder.imgpop.setVisibility(View.GONE);
        return rowView;
    }


}