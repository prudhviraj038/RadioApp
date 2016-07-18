package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AritstAdapterVideos extends BaseAdapter{
    String [] result;
    Context context;
    int [] imageId;
    ArrayList<ArtistModel> artists;
    ArtistListActivity artistListActivity;
    private static LayoutInflater inflater=null;
    public AritstAdapterVideos(Activity mainActivity, ArrayList<ArtistModel> artists) {
        // TODO Auto-generated constructor stub
      //  result=prgmNameList;
        context=mainActivity;
        this.artists=artists;
        this.artistListActivity = artistListActivity;
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
        TextView tv;
        CircleImageView img;
        LinearLayout artist_select_view;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.preview_artist_list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.artist_name);
        holder.img=(CircleImageView) rowView.findViewById(R.id.artist_image);
        holder.tv.setText(artists.get(position).getName());
        Picasso.with(context).load(artists.get(position).getImage()).placeholder(R.drawable.aa).fit().into(holder.img);

        return rowView;
    }

}