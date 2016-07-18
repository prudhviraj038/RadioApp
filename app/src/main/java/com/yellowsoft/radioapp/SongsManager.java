package com.yellowsoft.radioapp;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {
	// SDCard Path
	String youFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Radio App";
	final String MEDIA_PATH = youFilePath;
	private ArrayList<SongModel> songsList = new ArrayList<SongModel>();
	
	// Constructor
	public SongsManager(){
		
	}
	
	/**
	 * Function to read all mp3 files from sdcard
	 * and store the details in ArrayList
	 * */
	public ArrayList<SongModel> getPlayList(){
		File home = new File(MEDIA_PATH);
		Log.e("file_PATH",MEDIA_PATH);

		/*if (home.listFiles(new FileExtensionFilter()).length > 0) {

			for (File file : home.listFiles(new FileExtensionFilter())) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
				song.put("songPath", file.getPath());
				
				// Adding each song to SongList
				songsList.add(song);
			}
		}*/
		// return songs list array
		walkdir(home);

		return songsList;
	}




	public void walkdir(File dir) {
		File listFile[] = dir.listFiles();

		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].isDirectory()) {
					walkdir(listFile[i]);
				} else {
					if (listFile[i].getName().endsWith(".mp3") || listFile[i].getName().endsWith(".mp3")){
							HashMap<String, String> song = new HashMap<String, String>();
							song.put("songTitle", listFile[i].getName().substring(0, (listFile[i].getName().length() - 4)));
							song.put("songPath", listFile[i].getPath());
							Log.e("songPath", listFile[i].getPath());
							// Adding each song to SongList
						SongModel tmp = new SongModel(listFile[i].getName().substring(0, (listFile[i].getName().length() - 4)),"1","1","desc",

								listFile[i].getPath(),"100","artist name","11","-1","-1","0","-1","-1","0",null);
							songsList.add(tmp);
						}
					}
				}
			}
		}

	
	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
