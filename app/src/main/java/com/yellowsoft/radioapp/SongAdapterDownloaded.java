package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class SongAdapterDownloaded extends BaseAdapter{
    String [] result;
    Context context;
    int [] imageId;
    ArrayList<SongModel> artists;
    PageListFragment songListActivity;
    MediaMetadataRetriever metaRetriver;
    byte[] art;

    private static LayoutInflater inflater=null;

    public SongAdapterDownloaded(Activity mainActivity, ArrayList<SongModel> artists, PageListFragment fragment) {
        // TODO Auto-generated constructor stub
      //  result=prgmNameList;
        context=mainActivity;
        this.artists=artists;
        this.songListActivity = fragment;
      //  imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        metaRetriver = new MediaMetadataRetriever();
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
        rowView = inflater.inflate(R.layout.preview_download_song_list_item, null);
        holder.tv=(TextView) rowView.findViewById(R.id.song_name);
        holder.tvartist = (TextView) rowView.findViewById(R.id.album_name);
        holder.img=(CircleImageView) rowView.findViewById(R.id.song_image);
        holder.imgpop = (ImageView) rowView.findViewById(R.id.pop_up_menu);
        holder.tv.setText(artists.get(position).getSongName());
        holder.tvartist.setText(artists.get(position).getArtist_Name());
        Picasso.with(context).load(artists.get(position).getImage()).fit().into(holder.img);

        holder.imgpop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(context, holder.imgpop);
                popup.getMenuInflater().inflate(R.menu.menu_pop_up_downloaded, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = item.getItemId();
                        if (i == R.id.action_playlist) {
                            //do something
                            show_playlist_dailog(position);
                            return true;
                        } else if(i == R.id.action_delete){
                            DatabaseHandler db =new DatabaseHandler(context);
                            db.deleteContact(artists.get(position));
                            artists.remove(position);
                            db.close();
                            notifyDataSetChanged();
                            return true;
                        } else{
                            return onMenuItemClick(item);
                        }
                    }
                });

                popup.show();
            }
        });
        Log.e("url", artists.get(position).getSong());

        try {
            metaRetriver.setDataSource(artists.get(position).getSong());
            art = metaRetriver.getEmbeddedPicture();
            Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
            holder.img.setImageBitmap(songImage);

        } catch (Exception e) {
            Log.e("url",artists.get(position).getSong());
             }

                return rowView;
    }


    private void show_playlist_dailog(final int position) {

        final ArrayList<String> items = new ArrayList<>();
        final DatabaseHandler db = new DatabaseHandler(context);
        final ArrayList<ArtistModel> playlists = db.getAllPlaylist();
        for(int i=0;i<playlists.size()-1;i++){
            items.add(playlists.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Select Playlist");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                db.addContact(artists.get(position),playlists.get(item),playlists.get(item).getArtist_id());
            }
        });

        final AlertDialog dialog = builder.create();
        // mImageView = (ImageView) findViewById(R.id.imageView);
        dialog.show();
    }

}