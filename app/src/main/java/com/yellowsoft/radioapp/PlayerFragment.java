package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;
import com.squareup.picasso.Picasso;
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

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sriven on 1/28/2016.
 */
public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,HoloCircleSeekBar.OnCircleSeekBarChangeListener {
    int play_mode=0;
    public LinearLayout player_mini,player_full;
    private ImageView buttonPlayStop;
    private ImageView buttonPlayStopmini;
    private ImageView buttonPlayNext;
    private ImageView buttonPlayPrevious;
    private ImageView buttonPlayNextMini;
    private ImageView buttonPlayPreviousMini;
    private ImageView buttonRepeat;
    private ImageView buttonShuffle;
    private ImageView buttonLyrics;
    private CircleImageView albumArt,albumArtSmall,albumArtclick;
    private ImageView download_btn;
    private ImageView youtube_btn;
    private ImageView itunes_btn;
    private TextView artist_btn;
    private TextView playlist_btn;
    public ProgressBar progressBar;
    TextView total_duration,seek_position,artist_name,song_name,view_count,itunes_link,song_name_mini,lyrics;
    private SeekBarCompat seekBar;
    private HoloCircleSeekBar circleseekbar;
    FragmentTouchListner mCallBack;
    private Utilities utils;
    private SliderLayout mDemoSlider;
    LinearLayout timer_display;
    ViewFlipper lyrics_flipper;
    Boolean userpaused = false;
    public void hide_player_controls(){
        //buttonPlayStop.setVisibility(View.INVISIBLE);
        //buttonPlayStopmini.setVisibility(View.INVISIBLE);
        buttonPlayNext.setVisibility(View.INVISIBLE);
        buttonPlayNextMini.setVisibility(View.INVISIBLE);
        buttonPlayPrevious.setVisibility(View.INVISIBLE);
        buttonPlayPreviousMini.setVisibility(View.INVISIBLE);
        download_btn.setVisibility(View.GONE);
        youtube_btn.setVisibility(View.GONE);
        itunes_btn.setVisibility(View.GONE);
        buttonRepeat.setVisibility(View.INVISIBLE);
        buttonShuffle.setVisibility(View.INVISIBLE);
        timer_display.setVisibility(View.GONE);
        circleseekbar.setVisibility(View.INVISIBLE);
    }
    public void show_player_controls(){
        //buttonPlayStop.setVisibility(View.VISIBLE);
        //buttonPlayStopmini.setVisibility(View.VISIBLE);
        buttonPlayNext.setVisibility(View.VISIBLE);
        buttonPlayNextMini.setVisibility(View.VISIBLE);
        buttonPlayPrevious.setVisibility(View.VISIBLE);
        buttonPlayPreviousMini.setVisibility(View.VISIBLE);

       // download_btn.setVisibility(View.VISIBLE);
        //youtube_btn.setVisibility(View.VISIBLE);
        //itunes_btn.setVisibility(View.VISIBLE);


        buttonRepeat.setVisibility(View.VISIBLE);
        buttonShuffle.setVisibility(View.VISIBLE);
        timer_display.setVisibility(View.VISIBLE);
        circleseekbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChanged(HoloCircleSeekBar holoCircleSeekBar, int i, boolean b) {
        if (b) {
            int seekPos = holoCircleSeekBar.getValue();
            mCallBack.seekbarchange(seekPos,false);
        }
    }

    @Override
    public void onStartTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {

    }

    @Override
    public void onStopTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {

    }

    public void updateplaybutton(boolean boolMusicPlaying) {
        if(boolMusicPlaying) {
            buttonPlayStop.setImageResource(R.drawable.img_btn_pause);
            buttonPlayStopmini.setImageResource(R.drawable.img_btn_pause);
        }
        else {
            buttonPlayStop.setImageResource(R.drawable.img_btn_play);
            buttonPlayStopmini.setImageResource(R.drawable.img_btn_play);
        }
    }

    public interface FragmentTouchListner{
        public void buttonPlayStopClick();
        public void playNext();
        public void playPrevious();
        public void seekbarchange(int seekPos,boolean pause);
        public void downloadFile();
        public void youtubeselected();
        public void itunesselected();
        public void playerfragmentloaded();
        public  void showplaylist(ArrayList<SongModel> songs,int pos);
        public void showartist();
    }
    @Override
    public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
        // TODO Auto-generated method stub
        if (fromUser) {
            int seekPos = sb.getProgress();
            mCallBack.seekbarchange(seekPos,false);
                   }
    }


    // --- The following two methods are alternatives to track seekbar if moved.
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallBack = (NavigationActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LogoutUser");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.player_fragment_mini, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = getView();
        utils = new Utilities();
        lyrics_flipper = (ViewFlipper) v.findViewById(R.id.lyrics_flipper);
        mDemoSlider = (SliderLayout)v.findViewById(R.id.slider);
        player_mini = (LinearLayout) v.findViewById(R.id.player_mini);
        player_full = (LinearLayout) v.findViewById(R.id.player_full);
        buttonPlayStop = (ImageView) v.findViewById(R.id.btnPlay);
        buttonPlayStopmini = (ImageView) v.findViewById(R.id.play_btn_mini);
        buttonPlayNext = (ImageView) v.findViewById(R.id.btnNext);
        buttonPlayNextMini = (ImageView) v.findViewById(R.id.btnNextNini);
        buttonPlayPrevious = (ImageView) v.findViewById(R.id.btnPrevious);
        buttonPlayPreviousMini = (ImageView) v.findViewById(R.id.btnPreviousMini);
        buttonLyrics = (ImageView) v.findViewById(R.id.lyrics_btn);
        albumArt = (CircleImageView) v.findViewById(R.id.album_art);
        albumArtclick = (CircleImageView) v.findViewById(R.id.album_art_click);
        albumArtclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.showartist();
            }
        });
        albumArtSmall = (CircleImageView) v.findViewById(R.id.songThumbnailSmall);
        total_duration = (TextView) v.findViewById(R.id.songTotalDurationLabel);
        seek_position = (TextView) v.findViewById(R.id.songCurrentDurationLabel);
        song_name_mini = (TextView) v.findViewById(R.id.song_name_mini);
        song_name = (TextView) v.findViewById(R.id.song_name);
        artist_name = (TextView) v.findViewById(R.id.artist_name);
        view_count = (TextView) v.findViewById(R.id.view_count);
        download_btn = (ImageView) v.findViewById(R.id.download_btn);
        youtube_btn = (ImageView) v.findViewById(R.id.youtube_link);
        itunes_btn = (ImageView) v.findViewById(R.id.itunes_link);
        lyrics = (TextView) v.findViewById(R.id.lyrics);
        timer_display = (LinearLayout) v.findViewById(R.id.timerDisplay);
        buttonRepeat = (ImageView) v.findViewById(R.id.btnRepeat);
        buttonShuffle = (ImageView) v.findViewById(R.id.btnShuffle);
        buttonRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonShuffle.setImageResource(R.drawable.img_btn_shuffle);
                if(play_mode==0) {
                    play_mode=1;
                    buttonRepeat.setImageResource(R.drawable.img_btn_repeat_pressed);
                    Toast.makeText(getActivity(),"repeating this song",Toast.LENGTH_SHORT).show();
                } else if(play_mode == 1 ){
                    play_mode=2;
                    buttonRepeat.setImageResource(R.drawable.img_btn_repeat_pressed);
                    Toast.makeText(getActivity(), "repeating all songs", Toast.LENGTH_SHORT).show();
                }else if(play_mode == 2){
                  play_mode=0;
                    buttonRepeat.setImageResource(R.drawable.img_btn_repeat);
                    Toast.makeText(getActivity(), "repeating stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonRepeat.setImageResource(R.drawable.img_btn_repeat);
                if(play_mode==3){
                    play_mode=0;
                    buttonShuffle.setImageResource(R.drawable.img_btn_shuffle);
                }
                else{
                    play_mode=3;
                    buttonShuffle.setImageResource(R.drawable.img_btn_shuffle_pressed);

                }

            }
        });
        // --Reference seekbar in main.xml
        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.downloadFile();
            }
        });
        buttonLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lyrics_flipper.setDisplayedChild(1);
            }
        });
        seekBar = (SeekBarCompat) v.findViewById(R.id.materialSeekBar);
        circleseekbar = (HoloCircleSeekBar) v.findViewById(R.id.picker);
        seekBar.setOnSeekBarChangeListener(this);
        circleseekbar.setOnSeekBarChangeListener(this);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        buttonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userpaused=!userpaused;
                mCallBack.buttonPlayStopClick();
            }
        });
        buttonPlayStopmini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPlayStop.performClick();
            }
        });
        buttonPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.playNext();
            }
        });
        buttonPlayPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.playPrevious();
            }
        });

        youtube_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.youtubeselected();
            }
        });
        itunes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.itunesselected();
            }
        });
        buttonPlayNextMini.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            buttonPlayNext.performClick();
        }
    });

        buttonPlayPreviousMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPlayPrevious.performClick();
            }
        });

        mCallBack.playerfragmentloaded();
        get_banners();
    }
    public void toggleviews(boolean minimize) {
        if (minimize) {
            player_full.setVisibility(View.GONE);
            player_mini.setVisibility(View.VISIBLE);
        } else {
            player_full.setVisibility(View.VISIBLE);
            player_mini.setVisibility(View.GONE);
        }
    }
    public void updateUI(int seekMax, int seekProgress, int songEnded){
        seekBar.setMax(seekMax);
        seekBar.setProgress(seekProgress);
        circleseekbar .setMax(seekMax);
        circleseekbar.setValue(seekProgress);
        total_duration.setText("" + utils.milliSecondsToTimer(seekMax));
        seek_position.setText("" + utils.milliSecondsToTimer(seekProgress));
        if (songEnded == 1) {
//            buttonPlayNext.performClick();

        }

    }
    public void updateThumb(Context context, String id, String url, String artistname, String songname, String viewcount, String youtube, String itunes, String songdescription){
        Picasso.with(context).load(url).into(albumArt);
        Picasso.with(context).load(url).into(albumArtSmall);
        artist_name.setText(artistname);
        song_name.setText(songname);
        lyrics.setText(Html.fromHtml(songdescription));
        view_count.setText(viewcount);
        song_name_mini.setText(artistname + " - " + songname);
        final DatabaseHandler db = new DatabaseHandler(getActivity());
        Log.e("song_id_in_player", id);
        if(db.getContactcount(id) == 0){
            download_btn.setVisibility(View.VISIBLE);
        }
        else{
            download_btn.setVisibility(View.INVISIBLE);
        }
        if(youtube.equals(""))
            youtube_btn.setVisibility(View.INVISIBLE);
        else
            youtube_btn.setVisibility(View.VISIBLE);
        if(itunes.equals(""))
            itunes_btn.setVisibility(View.INVISIBLE);
        else
            itunes_btn.setVisibility(View.VISIBLE);

        Log.e("count", String.valueOf(db.getContactcount(id)));
        db.close();
    }
    private void get_banners(){
        banners = new ArrayList<>();
        banner_liks = new ArrayList<>();
        String url = Settings.SERVER_URL+"banners-json.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("banners");
                    for(int i=0;i<jsonArray.length();i++){

                        banners.add(jsonArray.getJSONObject(i).getString("image"));
                        banner_liks.add(jsonArray.getJSONObject(i).getString("title"));
                        DefaultSliderView defaultSliderView = new DefaultSliderView(getActivity());
                        defaultSliderView.image(jsonArray.getJSONObject(i).getString("image"));
                        final int finalI = i;
                        defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView baseSliderView) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner_liks.get(finalI)));
                                startActivity(browserIntent);
                            }
                        });
                        mDemoSlider.addSlider(defaultSliderView);
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
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    ArrayList<String> banners;
    ArrayList<String> banner_liks;
    public void playbtnclocked(){
        buttonPlayStop.performClick();
    }
}
