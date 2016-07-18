package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import de.hdodenhof.circleimageview.CircleImageView;

/* This file contains the source code for examples discussed in Tutorials 1-9 of developerglowingpigs YouTube channel.
 *  The source code is for your convenience purposes only. The source code is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*/

//---Implement OnSeekBarChangeListener to keep track of seek bar changes ---
public class MyActivity extends Activity implements OnSeekBarChangeListener {
	Intent serviceIntent;
	private ImageView buttonPlayStop;
	private ImageView buttonPlayNext;
	private ImageView buttonPlayPrevious;
	private CircleImageView albumArt;
	private ImageView download_btn;
	private ProgressBar progressBar;
	// -- PUT THE NAME OF YOUR AUDIO FILE HERE...URL GOES IN THE SERVICE
	String strAudioLink = "";
	String album_art_url;
	
	private boolean isOnline;
	private boolean boolMusicPlaying = false;
	private boolean isPaused = false;
	TelephonyManager telephonyManager;
	PhoneStateListener listener;

	// --Seekbar variables --
	private SeekBarCompat seekBar;
	private int seekMax;
	private static int songEnded = 0;
	boolean mBroadcastIsRegistered;
	TextView total_duration,seek_position;
	private Utilities utils;
	// --Set up constant ID for broadcast of seekbar position--
	public static final String BROADCAST_SEEKBAR = "com.yellowsoft.radioapp.sendseekbar";
	Intent intent;

	// Progress dialogue and broadcast receiver variables
	boolean mBufferBroadcastIsRegistered;
	private ProgressDialog pdBuff = null;
	private ArrayList<SongModel> songs;
	private int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_fragment);
		songs = (ArrayList<SongModel>)getIntent().getSerializableExtra("songs");
		position = getIntent().getIntExtra("position",0);
		strAudioLink = songs.get(position).getSong();
		album_art_url = songs.get(position).getImage();
		utils = new Utilities();
		try {
			serviceIntent = new Intent(this, myPlayService.class);

			// --- set up seekbar intent for broadcasting new position to service ---
			intent = new Intent(BROADCAST_SEEKBAR);

			initViews();
			setListeners();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					e.getClass().getName() + " " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		buttonPlayStop.performClick();
	}

	// -- Broadcast Receiver to update position of seekbar from service --
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent serviceIntent) {
			updateUI(serviceIntent);
		}
	};

	private void updateUI(Intent serviceIntent) {
		String counter = serviceIntent.getStringExtra("counter");
		String mediamax = serviceIntent.getStringExtra("mediamax");
		String strSongEnded = serviceIntent.getStringExtra("song_ended");
		Log.e("songednded",strSongEnded);
		int seekProgress = Integer.parseInt(counter);
		seekMax = Integer.parseInt(mediamax);
		songEnded = Integer.parseInt(strSongEnded);
		seekBar.setMax(seekMax);
		seekBar.setProgress(seekProgress);
		total_duration.setText("" + utils.milliSecondsToTimer(Integer.parseInt(mediamax)));
		seek_position.setText(""+utils.milliSecondsToTimer(Integer.parseInt(counter)));
		if (songEnded == 1) {
			buttonPlayNext.performClick();

		}
	}

	// --End of seekbar update code--

	// --- Set up initial screen ---
	private void initViews() {
		buttonPlayStop = (ImageView) findViewById(R.id.btnPlay);
		buttonPlayNext = (ImageView) findViewById(R.id.btnNext);
		buttonPlayPrevious = (ImageView) findViewById(R.id.btnPrevious);
		albumArt = (CircleImageView) findViewById(R.id.album_art);
		total_duration = (TextView) findViewById(R.id.songTotalDurationLabel);
		seek_position = (TextView) findViewById(R.id.songCurrentDurationLabel);
		download_btn = (ImageView) findViewById(R.id.download_btn);
		// --Reference seekbar in main.xml
		seekBar = (SeekBarCompat) findViewById(R.id.materialSeekBar);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	// --- Set up listeners ---
	private void setListeners() {
		buttonPlayStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonPlayStopClick();
			}
		});

		buttonPlayNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				playNext();
			}
		});
		buttonPlayPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				playPrevious();
			}
		});
		download_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				downloadFile();
			}
		});

		seekBar.setOnSeekBarChangeListener(this);
		
	}
		
	// --- invoked from ButtonPlayStop listener above ----
	private void buttonPlayStopClick() {
		if (!boolMusicPlaying) {
			playAudio();
			boolMusicPlaying = true;
		} else {
			if (boolMusicPlaying) {
				boolMusicPlaying = false;
			}
		}
	}

	// --- Stop service (and music) ---
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
		boolMusicPlaying = false;
	}

	// --- Start service and play music ---
	private void playAudio() {

		checkConnectivity();
		if (isOnline) {
			stopMyPlayService();

			serviceIntent.putExtra("sentAudioLink", strAudioLink);
			Picasso.with(this).load(album_art_url).into(albumArt);

			try {
				startService(serviceIntent);
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

	private void playNext() {
		if((position+1)<songs.size()) {
			strAudioLink = songs.get(position + 1).getSong();
			album_art_url = songs.get(position + 1).getImage();
			stopMyPlayService();
			playAudio();
			position++;
		}
			}
	private void playPrevious() {
		if(position>0){
			strAudioLink = songs.get(position - 1).getSong();
			album_art_url = songs.get(position - 1).getImage();
			stopMyPlayService();
			playAudio();
			position--;
		}
	}

	private void downloadFile(){
		Uri downloadUri = Uri.parse(songs.get(position).getSong());
		String folder_main = "a.Radio App";
		File f = new File(Environment.getExternalStorageDirectory(), folder_main);
		if (!f.exists()) {
			f.mkdirs();
			f.setReadable(false,true);
		}
		Uri destinationUri = Uri.parse(f.getPath() + "/" + songs.get(position).getSongName() + ".mp3");
		DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
				.addCustomHeader("Auth-Token", "YourTokenApiKey")
				.setRetryPolicy(new DefaultRetryPolicy())
				.setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
				.setDownloadListener(new DownloadStatusListener() {
					@Override
					public void onDownloadComplete(int id) {
						Log.e("down_load", "completed");
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onDownloadFailed(int id, int errorCode, String errorMessage) {
						Log.e("down_load", "failed");
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
						Log.e(String.valueOf(totalBytes), String.valueOf(downlaodedBytes));
					}
				});
		DownloadManager downloadManager = new ThinDownloadManager();
		downloadManager.add(downloadRequest);
		progressBar.setVisibility(View.VISIBLE);


	}
	// Handle progress dialogue for buffering...
	private void showPD(Intent bufferIntent) {
		String bufferValue = bufferIntent.getStringExtra("buffering");
		int bufferIntValue = Integer.parseInt(bufferValue);

		// When the broadcasted "buffering" value is 1, show "Buffering"
		// progress dialogue.
		// When the broadcasted "buffering" value is 0, dismiss the progress
		// dialogue.

		switch (bufferIntValue) {
		case 0:
			// Log.v(TAG, "BufferIntValue=0 RemoveBufferDialogue");
			// txtBuffer.setText("");
			if (pdBuff != null) {
				pdBuff.dismiss();
			}
			break;

		case 1:
			BufferDialogue();
			break;

		// Listen for "2" to reset the button to a play button
		case 2:

			break;

		}
	}

	// Progress dialogue...
	private void BufferDialogue() {

		pdBuff = ProgressDialog.show(MyActivity.this, "Buffering...",
				"Acquiring song...", true);
	}

	// Set up broadcast receiver
	private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent bufferIntent) {
			showPD(bufferIntent);
		}
	};

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

	// -- onPause, unregister broadcast receiver. To improve, also save screen data ---
	@Override
	protected void onPause() {
		// Unregister broadcast receiver
		if (mBufferBroadcastIsRegistered) {
			unregisterReceiver(broadcastBufferReceiver);
			mBufferBroadcastIsRegistered = false;
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		// Unregister broadcast receiver
		if (mBufferBroadcastIsRegistered) {
			unregisterReceiver(broadcastBufferReceiver);
			mBufferBroadcastIsRegistered = false;
		}
		if (mBroadcastIsRegistered) {
			unregisterReceiver(broadcastReceiver);
			mBufferBroadcastIsRegistered = false;
		}
		super.onPause();
	}

	// -- onResume register broadcast receiver. To improve, retrieve saved screen data ---
	@Override
	protected void onResume() {
		// Register broadcast receiver
		if (!mBufferBroadcastIsRegistered) {
			registerReceiver(broadcastBufferReceiver, new IntentFilter(
					myPlayService.BROADCAST_BUFFER));
			mBufferBroadcastIsRegistered = true;
		}
		super.onResume();
	}

	
	// --- When user manually moves seekbar, broadcast new position to service ---
	@Override
	public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		 if (fromUser) {
			 int seekPos = sb.getProgress();
				intent.putExtra("seekpos", seekPos);
				sendBroadcast(intent);
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

}