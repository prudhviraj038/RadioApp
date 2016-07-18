package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaylistAdapter extends BaseAdapter{
    String [] result;
    Context context;
    int [] imageId;
    ArrayList<ArtistModel> artists;
    PlayListFragment playListFragment;
    private static LayoutInflater inflater=null;
    public PlaylistAdapter(Activity mainActivity, ArrayList<ArtistModel> artists,PlayListFragment playListFragment) {
        // TODO Auto-generated constructor stub
      //  result=prgmNameList;
        context=mainActivity;
        this.artists=artists;
        this.playListFragment=playListFragment;
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
        ImageView pop;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.preview_play_list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.artist_name);
        holder.img=(CircleImageView) rowView.findViewById(R.id.artist_image);
        holder.pop = (ImageView) rowView.findViewById(R.id.pop_up_menu);
        holder.tv.setText(artists.get(position).getName());
        Uri uri = Uri.fromFile(new File(artists.get(position).getImage()));
        Picasso.with(context).load(uri).placeholder(R.drawable.app_icon).into(holder.img);

        Log.e("from_adap",artists.get(position).getImage());
        if(position+1== getCount())
            holder.pop.setVisibility(View.GONE);
        else
            holder.pop.setVisibility(View.VISIBLE);

        holder.pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(context, holder.pop);
                popup.getMenuInflater().inflate(R.menu.menu_pop_up_playlist, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.action_edit) {
                            //do something
                            playListFragment.selectphotos(position);
                            return true;
                        }
                        else if(i == R.id.action_rename){
                            playListFragment.rename_playlist(position);
                            return true;
                        }
                        else if(i == R.id.action_delete){
                            playListFragment.delete_playlsit(position);
                            return true;
                        }
                        else {
                            return onMenuItemClick(item);
                        }
                    }
                });

                popup.show();
            }
        });
        return rowView;
    }


}