package com.yellowsoft.radioapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SlidingPaneLayout;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class AndroidBuildingMusicPlayerActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener,HoloCircleSeekBar.OnCircleSeekBarChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private CircleImageView album_art;
	private SeekBarCompat songProgressBar;
	private HoloCircleSeekBar songProgressBarcircle;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private  MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0; 
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	Bitmap sample;
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	String [] MenuTitles = new String[]{"Home","Artist","New Songs"
			,"News","Top 10","Playlist","Videos","Remix","Songs","Radio","Contact Us","English" };
	public static int [] prgmImages={R.drawable.img_btn_play,
			R.drawable.img_btn_play,R.drawable.img_btn_play,
			R.drawable.img_btn_play,R.drawable.img_btn_play,
			R.drawable.img_btn_play,R.drawable.img_btn_play,
			R.drawable.img_btn_play,R.drawable.img_btn_play,
			R.drawable.img_btn_play,R.drawable.img_btn_play,
			R.drawable.img_btn_play};
	SlidingPaneLayout mSlidingPanel;
	ListView mMenuList;
	ImageView appImage;
	TextView TitleText;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_fragment);

		songManager = new SongsManager();
		//songsList = songManager.getPlayList();
		mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
//		mMenuList = (ListView) findViewById(R.id.MenuList);
		appImage = (ImageView)findViewById(R.id.home_btn);

		TitleText = (TextView)findViewById(android.R.id.title);


//		mMenuList.setAdapter(new CustomAdapter(this, MenuTitles, prgmImages));

	//	mSlidingPanel.setPanelSlideListener(panelListener);
//		mSlidingPanel.setParallaxDistance(200);

		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		album_art = (CircleImageView) findViewById(R.id.album_art);
		songProgressBar = (SeekBarCompat) findViewById(R.id.materialSeekBar);
		songProgressBarcircle = (HoloCircleSeekBar) findViewById(R.id.picker);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		
		// Mediaplayer
		mp = new MediaPlayer();
		mp.start();
		songManager = new SongsManager();
		utils = new Utilities();
		
		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		songProgressBarcircle.setOnSeekBarChangeListener(this);
		mp.setOnCompletionListener(this); // Important
	//	new DownloadMusicfromInternet().execute();
		// Getting all songs list

		
		// By default play first song
		playSong(0);
				
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check for already playing
				Log.e("pause_btn","clicked");
				if(mp.isPlaying()){
					if(mp!=null){
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				}else{
					// Resume song
					if(mp!=null){
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}
				
			}
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if(currentPosition + seekForwardTime <= mp.getDuration()){
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				}else{
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// get current song position				
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if(currentPosition - seekBackwardTime >= 0){
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				}else{
					// backward to starting position
					mp.seekTo(0);
				}
				
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				if(isShuffle) {
					// shuffle is on - play a random song
					Random rand = new Random();
					currentSongIndex = rand.nextInt((songsList.size() - 1) + 1);
					playSong(currentSongIndex);
				}
				else {
					if (currentSongIndex < (songsList.size() - 1)) {
						playSong(currentSongIndex + 1);
						currentSongIndex = currentSongIndex + 1;
					} else {
						// play first song
						playSong(0);
						currentSongIndex = 0;
					}
				}
			}
		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentSongIndex > 0){
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				}else{
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}
				
			}
		});
		
		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isRepeat){
					isRepeat = false;
					Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}else{
					// make repeat to true
					isRepeat = true;
					Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isShuffle = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}	
			}
		});
		
		/**
		 * Button Click event for Shuffle button
		 * Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isShuffle){
					isShuffle = false;
					Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}else{
					// make repeat to true
					isShuffle= true;
					Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isRepeat = false;
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}	
			}
		});
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
	btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
				i.putExtra("songslist",songsList);
				startActivityForResult(i, 100);			
			}
		});
		
	}
	
	/**
	 * Receiving song index from playlist view
	 * and play the song
	 * */
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
         	 currentSongIndex = data.getExtras().getInt("songIndex");
         	 // play selected song
             playSong(currentSongIndex);
        }
 
    }
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void  playSong(int songIndex){
		// Play song
		try {
        	mp.reset();
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
        	songTitleLabel.setText(songTitle);

			// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);
			
			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			sample = GetImage(songsList.get(songIndex).get("songPath"));
			album_art.setImageBitmap(sample);


		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

			}

	Bitmap GetImage(String filepathmini){
		Bitmap image;

		MediaMetadataRetriever mData=new MediaMetadataRetriever();
		mData.setDataSource(filepathmini);
		try{
			byte art[]=mData.getEmbeddedPicture();
			image= BitmapFactory.decodeByteArray(art, 0, art.length);
		}
		catch(Exception e)
		{
			image=null;
		}
		return image;
	}
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);
			   songProgressBarcircle.setValue(progress);
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
    }

	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {
		
		// check for repeat is ON or OFF
		if(isRepeat){
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if(isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else{
			// no repeat or shuffle ON - play next song
			if(currentSongIndex < (songsList.size() - 1)){
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			}else{
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}
	
	@Override
	 public void onDestroy(){
	 super.onDestroy();
	    mp.release();
	 }

	@Override
	public void onProgressChanged(HoloCircleSeekBar holoCircleSeekBar, int i, boolean b) {

	}

	@Override
	public void onStartTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(HoloCircleSeekBar holoCircleSeekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(holoCircleSeekBar.getValue(), totalDuration);

		// forward or backward to certain seconds
		mp.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();

	}
}