package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapter extends BaseAdapter{
    String [] result;
    Context context;
    int [] imageId;
    ArrayList<SongModel> artists;
    NewsListActivity songListActivity;
    private static LayoutInflater inflater=null;

    public NewsAdapter(Activity mainActivity, ArrayList<SongModel> artists, NewsListActivity fragment) {
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
        ImageView imgpop;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.preview_video_list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.song_name);
        holder.tvartist = (TextView) rowView.findViewById(R.id.album_name);
        holder.img=(CircleImageView) rowView.findViewById(R.id.song_image);
        holder.imgpop = (ImageView) rowView.findViewById(R.id.pop_up_menu);
        holder.tv.setText(artists.get(position).getSongName());
        holder.tvartist.setText(artists.get(position).getArtist_Name());
        Picasso.with(context).load(artists.get(position).getImage()).fit().into(holder.img);

                return rowView;
    }

}