package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sriven on 1/26/2016.
 */
public class NavigationActivity extends AppCompatActivity implements ArtistListActivity.FragmentTouchListner,
        PlayerFragment.FragmentTouchListner,SongListActivity.FragmentTouchListner,
        PageListFragment.FragmentTouchListner,HomeFragment.FragmentTouchListner,VideosCategoryActivity.FragmentTouchListner,
        VideoListActivity.FragmentTouchListner,NewsListActivity.FragmentTouchListner,
        PlayListFragment.FragmentTouchListner,
        PlaylistSongListActivity.FragmentTouchListner, GalleryListActivity.FragmentTouchListner, SearchFragment.FragmentTouchListner, CurrentPlaylist.FragmentTouchListner, RadioChannelsList.FragmentTouchListner, LoginFragment.FragmentTouchListner {
    JSONObject words;
    int repeattype = 0;
    Boolean isFirstSong = true;
    Boolean paused = true;
    Boolean apipaused = false;
    int previousselected = 0;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ArrayList<String>  MenuTitles = new ArrayList<>();
    ArrayList<String>  MenuImages = new ArrayList<>();
    ArrayList<Integer> prgmImages = new ArrayList<>();
    ArrayList<String> prgmIds = new ArrayList<>();
    SlidingUpPanelLayout slidingUpPanelLayout;
    PlayerFragment player_fragmnet;
    ImageView back_btn,menu_btn,search_btn,playlist_btn;
    TextView follow_unfollow,artist_name;
    private boolean boolMusicPlaying = false;
    private boolean isOnline;
    Intent serviceIntent;
    String strAudioLink = "";
    String album_art_url,songname,artistname,viewcount,id,itunes,youtue,songdescription;
    boolean mBroadcastIsRegistered=false;
    private ArrayList<SongModel> songs;
    private int position;
    Intent intent;
    CustomAdapter customAdapter;
    public static final String BROADCAST_SEEKBAR = "com.yellowsoft.radioapp.sendseekbar";
    boolean isappstartednow = true;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            String counter = serviceIntent.getStringExtra("counter");
            String mediamax = serviceIntent.getStringExtra("mediamax");
            String strSongEnded = serviceIntent.getStringExtra("song_ended");
            Boolean ispaused = serviceIntent.getBooleanExtra("ispaused",false);
            paused = ispaused;

            player_fragmnet.updateUI(Integer.parseInt(mediamax),Integer.parseInt(counter),Integer.parseInt(strSongEnded));
            player_fragmnet.updateplaybutton(!ispaused);
            boolMusicPlaying = true;
            if(strSongEnded.equals("1")){
                playNext();
            }
            if(songs.size()>position && position>=0)
            if(songs.get(position).isradio.equals("1")){
                player_fragmnet.hide_player_controls();
                playlist_btn.setVisibility(View.INVISIBLE);
            }
            else {
                player_fragmnet.show_player_controls();
                if(follow_unfollow.getVisibility() == View.VISIBLE)
                    playlist_btn.setVisibility(View.GONE);
                else
                    playlist_btn.setVisibility(View.VISIBLE);
            }
        }
    };

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
        {
            if(player_fragmnet!=null) {
                if (player_fragmnet.lyrics_flipper.getDisplayedChild() == 1) {
                    player_fragmnet.lyrics_flipper.showPrevious();
                } else
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }
        else if(fragmentManager.getBackStackEntryCount()>0) {

            fragmentManager.popBackStackImmediate();
            refresh_header();
        }
        else
            super.onBackPressed();
    }


    @Override
    public void playlistselected(String artist_id, String name, String artistimage, String desc) {
        Bundle args = new Bundle();
        args.putString(Settings.ARTIST_ID, artist_id);
        args.putString("title", name);
        args.putString("desc", desc);
        args.putString("artist_image", artistimage);

        Fragment song_fragmnet = new PlaylistSongListActivity();
        song_fragmnet.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, song_fragmnet).addToBackStack(null)
                .commit();
        Log.e("artist", "selected");
        refresh_header();
    }


    @Override
    public void artistselected(String artist_id,String artistname,String artisitmage,String desc) {
        Bundle args = new Bundle();
        selected_artistid = artist_id;
        args.putString(Settings.ARTIST_ID, "artist_id=" +artist_id);
        args.putString("id", artistname);
        args.putString("title", artistname);
        args.putString("desc", desc);
        args.putString("artist_image", artisitmage);

        Fragment song_fragmnet = new SongListActivity();
        song_fragmnet.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, song_fragmnet).addToBackStack(null)
                .commit();
        Log.e("artist", "selected");
        refresh_header();
    }
    @Override
    public void showartist() {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            artistselected(songs.get(position).artist_id, songs.get(position).artist_name, songs.get(position).image, songs.get(position).getDescription());
    }


    @Override
    public void gallery_selected(String artist_id, String artist_desc, String artist_image) {
        Bundle args = new Bundle();
        args.putString(Settings.ARTIST_ID, artist_id);
        args.putString("title", artistname);
        args.putString("desc", artist_desc);
        args.putString("artist_image", artist_image);

        Fragment song_fragmnet = new GalleryListActivity();
        song_fragmnet.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, song_fragmnet).addToBackStack(null)
                .commit();
        Log.e("artist", "selected");
        refresh_header();

    }

    @Override
    public void songselected(ArrayList<String> image, ArrayList<String> desc, int position) {

        Bundle args = new Bundle();
        args.putStringArrayList("images", image);
        args.putStringArrayList("desc", desc);
        args.putInt("position", position);

        Fragment song_fragmnet = new GalleryViewFragment();
        song_fragmnet.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, song_fragmnet).addToBackStack(null)
                .commit();
        Log.e("artist", "selected");
        refresh_header();
            }


    public void fragmentselected(String artist_id,int position,String tittle,Boolean adminplaylist) {
        Bundle args = new Bundle();
        args.putString(Settings.ARTIST_ID, artist_id);
        args.putString("title", tittle);
        if(!adminplaylist)
        args.putString("type", "down");

        args.putBoolean("adminplaylist", adminplaylist);
        Fragment song_fragmnet = new PageListFragment();
        song_fragmnet.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(previousselected == position)
            fragmentManager.popBackStack(String.valueOf(position), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        else
            fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, song_fragmnet).addToBackStack(String.valueOf(position))
                .commit();
        previousselected = position;
        refresh_header();
    }


    @Override
    public void songselected(ArrayList<SongModel> songs, int position) {
        isFirstSong = false;
        this.songs = songs;
        this.position = position;
        strAudioLink = songs.get(position).getSong();
        album_art_url = songs.get(position).getImage();
        id = songs.get(position).getSongId();
        songname = songs.get(position).getSongName();
        songdescription = songs.get(position).getDescription();
        artistname = songs.get(position).getArtist_Name();
        viewcount = songs.get(position).getViewcount();
        youtue = songs.get(position).youtube;
        itunes = songs.get(position).itunes;
        try {
            serviceIntent = new Intent(this, myPlayService.class);
            if(isappstartednow) {
                 slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    isappstartednow = false;
            }
            else
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            // --- set up seekbar intent for broadcasting new position to service ---
            intent = new Intent(BROADCAST_SEEKBAR);

            playAudio();
            boolMusicPlaying=false;

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void addtoqueue(SongModel song) {
        songs.add(song);
        Toast.makeText(this,"song added to queue",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void followingclicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int i = MenuTitles.size()-1;
            Fragment player_fragmnet = new LoginFragment();
            if(previousselected == i)
                fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            else
                fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(i))
                    .commit();
            previousselected = i;
        refresh_header();
    }

    @Override
    public void update_follow_unfollow(String msg,String artistname,Boolean isfollowing)
    {       follow_unfollow.setVisibility(View.VISIBLE);
            follow_unfollow.setText(msg);
            artist_name.setText(artistname);
            search_btn.setVisibility(View.GONE);
            playlist_btn.setVisibility(View.GONE);
            this.isfollowing = isfollowing;
    }

    public void refresh_header(){
        follow_unfollow.setVisibility(View.GONE);
        artist_name.setText("DanDen");
        search_btn.setVisibility(View.VISIBLE);
        playlist_btn.setVisibility(View.VISIBLE);
    }
    @Override
    public void buttonPlayStopClick() {
        if(isFirstSong)
        {
            songselected(songs,position);
        }
        else {
            if (!boolMusicPlaying) {
                playAudio();

            } else {
                if (paused) {
                    seekbarchange(-1, false);
                    player_fragmnet.updateplaybutton(false);
                } else {
                    seekbarchange(0, true);
                    player_fragmnet.updateplaybutton(true);
                }

            }
        }
    }

    @Override
    public void videocategoryselected(String artist_id, String name, String artistimage, String desc) {
        Bundle args = new Bundle();
        args.putString(Settings.ARTIST_ID, "parent_id=" +artist_id);
        args.putString("title", artistname);
        args.putString("desc", desc);
        args.putString("artist_image", artistimage);

        Fragment song_fragmnet = new VideoListActivity();
        song_fragmnet.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, song_fragmnet).addToBackStack(null)
                .commit();
        Log.e("video", "selected");
refresh_header();
    }
    @Override
    public void newsselected(ArrayList<SongModel> songs, int position) {
        Bundle args = new Bundle();
        args.putString("title", songs.get(position).getSongName());
        args.putString("desc", songs.get(position).getDescription());
        args.putString("image", songs.get(position).getImage());

        Fragment song_fragmnet = new NewsDetailFragment();
        song_fragmnet.setArguments(args);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, song_fragmnet).addToBackStack(null)
                .commit();
        Log.e("video", "selected");
        refresh_header();
    }


    private void playAudio() {

      //  checkConnectivity();
        isOnline = true;
        if (isOnline) {
                stopMyPlayService();
                serviceIntent.putExtra("sentAudioLink", strAudioLink);
                player_fragmnet.updateThumb(this,id,album_art_url,artistname,songname,get_word("total_views")+" : "+viewcount,youtue,itunes,songdescription);
                try {
                    startService(serviceIntent);
                    boolMusicPlaying = true;
                    player_fragmnet.updateplaybutton(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getApplicationContext(),
                            e.getClass().getName() + " " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                // -- Register receiver for seekbar--
                registerReceiver(broadcastReceiver, new IntentFilter(
                        myPlayService.BROADCAST_ACTION));
                mBroadcastIsRegistered = true;


        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Network Not Connected...");
            alertDialog.setMessage("Please connect to a network and try again");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // here you can add functions
                }
            });
            alertDialog.setIcon(R.drawable.notification_template_icon_bg);

            alertDialog.show();
        }
    }
    private void checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting()
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting())
            isOnline = true;
        else
            isOnline = false;
    }
    private void stopMyPlayService() {
        // --Unregister broadcastReceiver for seekbar
        if (mBroadcastIsRegistered) {
            try {
                unregisterReceiver(broadcastReceiver);
                mBroadcastIsRegistered = false;
            } catch (Exception e) {
                // Log.e(TAG, "Error in Activity", e);
                // TODO Auto-generated catch block

                e.printStackTrace();
                Toast.makeText(

                        getApplicationContext(),

                        e.getClass().getName() + " " + e.getMessage(),

                        Toast.LENGTH_LONG).show();
            }
        }

        try {
            stopService(serviceIntent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),
                    e.getClass().getName() + " " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public  void playNext() {
        if(isFirstSong){
            buttonPlayStopClick();
        }
        else {
            int temp = 0;
            if (player_fragmnet.play_mode == 0)
                temp = 1;
            else if (player_fragmnet.play_mode == 1)
                temp = 0;
            else if (player_fragmnet.play_mode == 3) {
                temp = 0;
                position = r.nextInt(songs.size()) - 1;
                if (position == 0)
                    position = 1;
                if (position == songs.size())
                    position = position - 1;
            }

            if ((position + 1) < songs.size()) {
                if (position < 0)
                    position = 0;
                try {
                    strAudioLink = songs.get(position + temp).getSong();
                    album_art_url = songs.get(position + temp).getImage();
                    songname = songs.get(position + temp).getSongName();  //http://clients.yellowsoft.in/dendan/api/adds-json.php
                    songdescription = songs.get(position + temp).getDescription();
                    artistname = songs.get(position + temp).getArtist_Name();
                    viewcount = songs.get(position + temp).getViewcount();
                    id = songs.get(position + temp).getSongId();
                    position = position + temp;
                }catch (Exception ex){
                    strAudioLink = songs.get(0).getSong();
                    album_art_url = songs.get(0).getImage();
                    songname = songs.get(0).getSongName();  //http://clients.yellowsoft.in/dendan/api/adds-json.php
                    songdescription = songs.get(0).getDescription();
                    artistname = songs.get(0).getArtist_Name();
                    viewcount = songs.get(0).getViewcount();
                    id = songs.get(0).getSongId();
                    position=0;
                }


                stopMyPlayService();
                playAudio();


                if (previousselected == -2) {
                    playlist_btn.performClick();
                }

            } else {
                position = -1;
                playNext();
            }
        }

    }
    Random r = new Random();
    @Override
    public void playPrevious() {
        if(isFirstSong){
            buttonPlayStopClick();
        }
        else {
            int temp = 0;
            if (player_fragmnet.play_mode == 0)
                temp = 1;
            else if (player_fragmnet.play_mode == 1)
                temp = 0;
            else if (player_fragmnet.play_mode == 3) {
                temp = 0;
                position = r.nextInt(songs.size()) - 1;
                if (position == 0)
                    position = 1;
                if (position == songs.size())
                    position = position - 1;
            }
            if (position > 0) {
                strAudioLink = songs.get(position - temp).getSong();
                album_art_url = songs.get(position - temp).getImage();
                songname = songs.get(position - temp).getSongName();
                songdescription = songs.get(position - temp).getDescription();
                artistname = songs.get(position - temp).getArtist_Name();
                viewcount = songs.get(position - temp).getViewcount();

                id = songs.get(position - temp).getSongId();
                stopMyPlayService();
                playAudio();
                position = position - temp;
                if (previousselected == -2) {
                    playlist_btn.performClick();
                }

            }
        }
    }

    @Override
    public void seekbarchange(int seekPos,boolean pause) {
        if(intent == null){
            intent = new Intent(BROADCAST_SEEKBAR);
        }
         intent.putExtra("seekpos", seekPos);
        intent.putExtra("pause", pause);
         sendBroadcast(intent);

    }
    private  String get_word(String word){
        try {
            return  words.getString(word);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  word;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Settings.set_user_language(this, "ar");
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.navigation_activity);
        words = Settings.get_user_language_words(this);
        getSupportActionBar().hide();
        songs = new ArrayList<>();
        MenuTitles.add(get_word("home"));
        MenuTitles.add(get_word("artists"));
        MenuTitles.add(get_word("news"));
        MenuTitles.add(get_word("videos"));
        MenuTitles.add(get_word("downloads"));
        MenuTitles.add(get_word("playlist"));
        MenuTitles.add(get_word("radio"));

        prgmImages.add(R.drawable.home);
        prgmImages.add(R.drawable.artist);
        prgmImages.add(R.drawable.news);
        prgmImages.add(R.drawable.videos);
        prgmImages.add(R.drawable.download);
        prgmImages.add(R.drawable.playlist);
        prgmImages.add(R.drawable.radio);
        MenuImages.add("-1");
        MenuImages.add("-1");
        MenuImages.add("-1");
        MenuImages.add("-1");
        MenuImages.add("-1");
        MenuImages.add("-1");
        MenuImages.add("-1");
        prgmIds.add("-1");
        prgmIds.add("-1");
        prgmIds.add("-1");
        prgmIds.add("-1");
        prgmIds.add("-1");
        prgmIds.add("-1");
        prgmIds.add("-1");
        search_btn = (ImageView) findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment player_fragmnet = new SearchFragment();
                if (previousselected == -1)
                    fragmentManager.popBackStack(String.valueOf(-1), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                else
                    fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(-1))
                        .commit();
                previousselected = -1;
                refresh_header();
            }
        });
        playlist_btn = (ImageView) findViewById(R.id.playlist_btn);
        playlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                FragmentManager fragmentManager = getSupportFragmentManager();
                Bundle args = new Bundle();
                args.putSerializable("songs",songs);
                args.putInt("pos",position);
                Fragment player_fragmnet = new CurrentPlaylist();
                player_fragmnet.setArguments(args);
                if (previousselected == -2)
                    fragmentManager.popBackStack(String.valueOf(-2), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                else
                    fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(-2))
                        .commit();
                previousselected = -2;
                refresh_header();

            }
        });
        follow_unfollow = (TextView) findViewById(R.id.follow_unfollow_btn);
        follow_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
               int i = MenuTitles.size()-1;
                if(Settings.get_user_id(NavigationActivity.this).equals("-1")){
                    Fragment player_fragmnet = new LoginFragment();
                    if(previousselected == i)
                        fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    else
                        fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(i))
                            .commit();
                    previousselected = i;
                    refresh_header();
                }
                else{
                        update_follow();
                }
            }
        });
        artist_name = (TextView) findViewById(R.id.tittle);
        back_btn = (ImageView) findViewById(R.id.back_btn);
        menu_btn = (ImageView) findViewById(R.id.menu_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
                Log.e("menu click", "yes");
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        customAdapter = new CustomAdapter(this, MenuTitles, prgmImages, MenuImages);
        mDrawerList.setAdapter(customAdapter);
     //   mDrawerLayout.openDrawer(GravityCompat.START);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (i == 0) {
                    Fragment player_fragmnet = new HomeFragment();
                    if (previousselected == i)
                        fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    else
                        fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(i))
                            .commit();
                    previousselected = i;
                    refresh_header();
                } else if (i == 1) {
                    Fragment player_fragmnet = new ArtistListActivity();

                    if (previousselected == i)
                        fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    else
                        fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(i))
                            .commit();
                    previousselected = i;
                    refresh_header();
                } else if (i == 2) {
                    Bundle args = new Bundle();

                    args.putString("title", artistname);
                    args.putString("page_title", get_word("news"));
                    Fragment song_fragmnet = new NewsListActivity();
                    song_fragmnet.setArguments(args);
                    fragmentManager = getSupportFragmentManager();

                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, song_fragmnet).addToBackStack(null)
                            .commit();
                    Log.e("news", "selected");
                    refresh_header();

                } else if (i == 3) {
                    // fragmentselected("type=playlist",i,"Play List");
                    Fragment vidcat_fragmnet = new VideosCategoryActivity();

                    if (previousselected == i)
                        fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    else
                        fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, vidcat_fragmnet).addToBackStack(String.valueOf(i))
                            .commit();
                    previousselected = i;
                    refresh_header();

                } else if (i == 4) {
                    fragmentselected(prgmIds.get(i), i, get_word("downloads"), false);
                } else if (i == 5) {
                    Fragment player_fragmnet = new PlayListFragment();

                    if (previousselected == i)
                        fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    else
                        fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(i))
                            .commit();
                    previousselected = i;
                    refresh_header();
                } else if (i == 6) {

                    /*Fragment player_fragmnet = new RadioChannelsList();

                    if(previousselected == i)
                        fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    else
                        fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(i))
                            .commit();
                    previousselected = i;*/
                    get_chanel_songs("1");
                    refresh_header();
                } else if (i >= Settings.static_menu && i < MenuTitles.size() - Settings.static_menu_bottom)
                    fragmentselected(prgmIds.get(i), i, MenuTitles.get(i), true);
                else {
                    if (i == MenuTitles.size() - 3) {
                        Fragment player_fragmnet = new ContactUsFragment();

                        if (previousselected == i)
                            fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        else
                            fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(i))
                                .commit();
                        previousselected = i;
                        refresh_header();

                    } else if (i == MenuTitles.size() - 2) {
                        if (Settings.get_user_language(NavigationActivity.this).equals("ar")) {
                            Settings.set_user_language(NavigationActivity.this, "en");

                        } else {
                            Settings.set_user_language(NavigationActivity.this, "ar");
                        }
                        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        refresh_header();
                    } else if (i == MenuTitles.size() - 1) {
                        if (Settings.get_user_id(NavigationActivity.this).equals("-1")) {

                            Fragment player_fragmnet = new LoginFragment();

                            if (previousselected == i)
                                fragmentManager.popBackStack(String.valueOf(i), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            else
                                fragmentManager.popBackStack(String.valueOf(previousselected), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_frame, player_fragmnet).addToBackStack(String.valueOf(i))
                                    .commit();
                            previousselected = i;
                            refresh_header();
                        } else {
                            showDialog(NavigationActivity.this, "", "Do you want to logout?");
                        }
                    }
                }
            }
        });
        slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                player_fragmnet.toggleviews(true);
            }

            @Override
            public void onPanelExpanded(View view) {

                player_fragmnet.toggleviews(false);

            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });
        player_fragmnet = new PlayerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.player_fragment, player_fragmnet)
                .commit();

        Fragment main_fragmnet = new HomeFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, main_fragmnet)
                .commit();

refresh_header();

        get_admin_playlist();
    }
    public void showDialog(Activity activity, String title, CharSequence message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Settings.set_user_id(NavigationActivity.this,"-1");
                refresh_sliding_menu();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void get_admin_playlist(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(Settings.getword(this,"please_wait"));
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url;
        url = Settings.SERVER_URL + "admin-playlist-json.php";
        Log.e("url", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                                try {
                                    if(progressDialog!=null)
                                        progressDialog.dismiss();
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        MenuTitles.add(jsonObject1.getString("title"+Settings.get_append(NavigationActivity.this)));
                        MenuImages.add(jsonObject1.getString("image"));
                        prgmImages.add(-1);
                        prgmIds.add(jsonObject1.getString("id"));
                                            }
                                    loadnextmenuitems();
                } catch (JSONException e) {

                                    if(progressDialog!=null)
                                        progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

                if(progressDialog!=null)
                    progressDialog.dismiss();
                loadnextmenuitems();
                Log.e("response is:", error.toString());
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }


    private void loadnextmenuitems(){
        MenuTitles.add(get_word("contact_us"));
        MenuImages.add("-1");
        prgmImages.add(R.drawable.contact);
        prgmIds.add("-1");
        if(Settings.get_user_language(NavigationActivity.this).equals("ar"))
            MenuTitles.add("ENGLISH");
        else
            MenuTitles.add("العربية");
        MenuImages.add("-1");
        prgmImages.add(R.drawable.launguage);
        prgmIds.add("-1");

        if(Settings.get_user_id(NavigationActivity.this).equals("-1"))
            MenuTitles.add(get_word("log_in"));
        else
            MenuTitles.add(get_word("log_out"));
        MenuImages.add("-1");
        prgmImages.add(R.drawable.login_icon);
        prgmIds.add("-1");

        customAdapter.notifyDataSetChanged();
    }

    private void refresh_sliding_menu(){
        MenuTitles.remove(MenuTitles.size()-1);
        MenuImages.remove(MenuImages.size() - 1);
        prgmImages.remove(prgmImages.size() - 1);
        prgmIds.remove(prgmIds.size()-1);

        if(Settings.get_user_id(NavigationActivity.this).equals("-1"))
            MenuTitles.add(get_word("log_in"));
        else
            MenuTitles.add(get_word("log_out"));
        MenuImages.add("-1");
        prgmImages.add(R.drawable.login_icon);
        prgmIds.add("-1");

        customAdapter.notifyDataSetChanged();
    }

    @Override
    public void downloadFile(){
        Uri downloadUri = Uri.parse(songs.get(position).getSong());
        String folder_main = "Radio App";
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
            f.setReadable(false,true);
        }
        final Uri destinationUri = Uri.parse(f.getPath() + "/" + songs.get(position).getSongName() + ".mp3");
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .addCustomHeader("Auth-Token", "YourTokenApiKey")
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int idi) {
                        Log.e("down_load", "completed");
                       player_fragmnet.progressBar.setVisibility(View.GONE);
                        Toast.makeText(NavigationActivity.this,"download completed",Toast.LENGTH_SHORT).show();
                        final DatabaseHandler db = new DatabaseHandler(NavigationActivity.this);
                        ArtistModel download_artist = new ArtistModel(songs.get(position).getArtist_Name(),
                                songs.get(position).getArtist_id(),
                                songs.get(position).getImage(),
                                songs.get(position).getDescription());

                        db.addContact(songs.get(position), download_artist,"-1");
                        db.close();
                        player_fragmnet.updateThumb(NavigationActivity.this,id, album_art_url, artistname, songname, get_word("total_views")+" : "+viewcount,youtue,itunes, songdescription);

                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                        Log.e("down_load", "failed");
                        player_fragmnet.progressBar.setVisibility(View.GONE);
                        Toast.makeText(NavigationActivity.this,"download failed",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
                        Log.e(String.valueOf(totalBytes), String.valueOf(downlaodedBytes));
                    }
                });
        DownloadManager downloadManager = new ThinDownloadManager();
        downloadManager.add(downloadRequest);
        player_fragmnet.progressBar.setVisibility(View.VISIBLE);
    }



    @Override
    public void youselected(ArrayList<SongModel> songs,int pos) {
        if(intent!=null) {
            seekbarchange(0, true);
            player_fragmnet.updateplaybutton(false);
        }
            Intent intent = new Intent(this, YoutubePlayer.class);
            intent.putExtra("video", "https://www.youtube.com/watch?v=" + songs.get(pos).getYoutube());
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+songs.get(pos).getYoutube()));
        startActivity(browserIntent);


    }

    @Override
    public void itunesselected(ArrayList<SongModel> songs,int pos) {
            if(intent!=null) {
                seekbarchange(0, true);
                player_fragmnet.updateplaybutton(false);
            }
            String url = songs.get(pos).getItunes();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
    }
    @Override
    public void youtubeselected() {
        if(intent!=null) {
            seekbarchange(0, true);
            player_fragmnet.updateplaybutton(false);
        }

        Intent intent = new Intent(this, YoutubePlayer.class);
        intent.putExtra("video", songs.get(position).getYoutube());
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+songs.get(position).getYoutube()));

        startActivity(browserIntent);


    }

    @Override
    public void itunesselected() {
        if(intent!=null) {
            seekbarchange(0, true);
            player_fragmnet.updateplaybutton(false);
        }

        String url = songs.get(position).getItunes();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    @Override
    public void playerfragmentloaded() {
        registerReceiver(broadcastReceiver, new IntentFilter(
                myPlayService.BROADCAST_ACTION));
        mBroadcastIsRegistered = true;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String is_list_saved = sharedPreferences.getString("saved", "-1");
        int pos_saved = Integer.parseInt(sharedPreferences.getString("position", "0"));
        DatabaseHandler db = new DatabaseHandler(this);
        if(is_list_saved.equals("1")){
            songs = db.getsongs_by_playlist("-2");
            if(songs.size()>0) {
                Log.e("activi", String.valueOf(songs.size()));
                position = pos_saved;
                if (position>=songs.size())
                    position = 0;
                strAudioLink = songs.get(position).getSong();
                album_art_url = songs.get(position).getImage();
                id = songs.get(position).getSongId();
                songname = songs.get(position).getSongName();
                songdescription = songs.get(position).getDescription();
                artistname = songs.get(position).getArtist_Name();
                viewcount = songs.get(position).getViewcount();
                youtue = songs.get(position).youtube;
                itunes = songs.get(position).itunes;
                player_fragmnet.updateThumb(this,id,album_art_url,artistname,songname,get_word("total_views")+" : "+viewcount,youtue,itunes,songdescription);
                //songselected(songs, position);


            }
            else{
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        }else{
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        db.close();

    }

    @Override
    public void showplaylist(ArrayList<SongModel> songs,int position) {

        Fragment song_fragmnet = new VideoListActivity();
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, song_fragmnet).addToBackStack(null)
                .commit();
        Log.e("video", "selected");
        refresh_header();

    }
    @Override
    public void onResume() {
        super.onResume();
        //playAudio();
    }
    @Override
    public void onPause() {
        super.onPause();
        //playAudio();


        DatabaseHandler db = new DatabaseHandler(this);
        db.deletePlaylistsongs("-2");

        for(int j=0;j<songs.size();j++) {
            ArtistModel artist = new ArtistModel(songs.get(j).getArtist_Name(), songs.get(j).getArtist_id(), songs.get(j).getImage(), songs.get(j).getDescription());
            db.addContact(songs.get(j), artist, "-2");
        }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("saved", "1");
            editor.putString("position", String.valueOf(position));
        Log.e("activi", String.valueOf(position));
            editor.commit();
        }
    @Override
    public void radiochannelselected(String artist_id, String name, String artistimage, String desc) {
        get_chanel_songs(artist_id);
    }
    public void get_chanel_songs(final String artist_id){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait getting the radio channel information...");
        progressDialog.setCancelable(false);
        progressDialog.show();
            songs.clear();
            String url;
            url = Settings.SERVER_URL + "radio-chanels-songs-json.php?playlist_id="+artist_id;
            Log.e("url", url);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {
                   // progressBar.setVisibility(View.GONE);
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("songs");
                        for(int i=0;i<jsonArray.length();i++){
                            songs.add(new SongModel(jsonArray.getJSONObject(i).getString("title"),jsonArray.getJSONObject(i).getString("title_ar"),
                                    jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("image"),
                                    jsonArray.getJSONObject(i).getString("description"),jsonArray.getJSONObject(i).getString("description_ar"),
                                    jsonArray.getJSONObject(i).getString("song"),
                                    jsonArray.getJSONObject(i).getString("view_count"),
                                    jsonArray.getJSONObject(i).getString("artist_name"),jsonArray.getJSONObject(i).getString("artist_name_ar"),
                                    jsonArray.getJSONObject(i).getString("artist_id"),
                                    jsonArray.getJSONObject(i).getString("youtube"),
                                    jsonArray.getJSONObject(i).getString("itunes"),
                                    "1",NavigationActivity.this
                            ));
                        }
                       // songAdapter.notifyDataSetChanged();
                     //   get_banners();
                        position=0;
                        songselected(songs,position);
                        player_fragmnet.hide_player_controls();
                        getStatus(artist_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                      //  get_banners();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                    Log.e("response is:", error.toString());
                    //progressBar.setVisibility(View.GONE);
                    //get_banners();
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                }
            });

// Access the RequestQueue through your singleton class.
            AppController.getInstance().addToRequestQueue(jsObjRequest);
            //progressBar.setVisibility(View.VISIBLE);
        }
    private void getStatus(final String chanel_id){
        String url;
        url = Settings.SERVER_URL + "chanel-status-json.php?playlist_id="+chanel_id;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("chanels");
                    Log.e("api_status",jsonArray.getJSONObject(0).getString("status"));
                    Log.e("user_status",player_fragmnet.userpaused?"pause":"play");
                    if(!player_fragmnet.userpaused)
                    if(jsonArray.getJSONObject(0).getString("status").equals("Play"))
                    {

                        if(paused){

                                seekbarchange(-1, false);
                                player_fragmnet.updateplaybutton(false);

                        }

                    }
                    else {
                        apipaused=true;
                        seekbarchange(0, true);
                        player_fragmnet.updateplaybutton(true);
                    }

                    if(songs.size()>position)
                    if(songs.get(position).isradio.equals("1")){

                     getStatus(chanel_id);
                        player_fragmnet.hide_player_controls();
                    }
                    else
                        player_fragmnet.show_player_controls();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());

            }
        });

// Access the RequestQueue through your singleton
        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }
    final ArrayList<String> lans = new ArrayList<>();
    public void select_language() {
        lans.clear();
        lans.add("ENGLISH/الإنجليزية");
        lans.add("ARABIC/العربية");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, lans);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Language");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Settings.set_user_language(NavigationActivity.this, "en");


                } else {
                    Settings.set_user_language(NavigationActivity.this, "ar");


                }
                Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void log_in_success() {
        refresh_sliding_menu();
        back_btn.performClick();
    }
    String selected_artistid = "";
    Boolean isfollowing = false;
    private void update_follow(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url;
        url = Settings.SERVER_URL +"follow_artist.php?artist_id="+selected_artistid+"&cust_id="+Settings.get_user_id(this);
        if(isfollowing)
            url=url+"type=remove";
        Log.e("url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(progressDialog!=null)
                    progressDialog.hide();

                Log.e("follow", jsonObject.toString());
                try {
                    String status = jsonObject.getString("status");
                    if(status.equals("Success")){
                        isfollowing=!isfollowing;
                        if(isfollowing)
                            follow_unfollow.setText("unfollow");
                        else
                            follow_unfollow.setText("follow");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                if(progressDialog!=null)
                    progressDialog.hide();
            }
        });

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

}

