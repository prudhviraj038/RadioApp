package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Locale;

/**
 * Created by Chinni on 22-06-2015.
 */
public class Settings {
   public static final String SERVER_URL    = "http://danden.com/api/";
   public static final String DEVELOPER_KEY = "AIzaSyDpQ6VmRaiN728aG7TyXrGewgOoisuLJZg";
   public static final String ARTIST_ID    = "artist_id";
   public static final String SONG_ID    = "song_id";
   static String words_key = "danden_words";
   public static final int static_menu    = 7;
   public static final int static_menu_bottom    = 3;
   static String user_key = "radio_user";
   static String lan_key = "radio_lan";

   public static final int normal=0;
   public static final int repeat=1;
   public static final int repeat_all=2;
   public static final int shuffle=3;

   public static   void forceRTLIfSupported(Activity activity)
   {
      SharedPreferences sharedPref;
      sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
      Log.e("lan", sharedPref.getString(lan_key, "-1"));

      if (sharedPref.getString(lan_key, "-1").equals("en")) {
         Resources res = activity.getResources();
         // Change locale settings in the app.
         DisplayMetrics dm = res.getDisplayMetrics();
         android.content.res.Configuration conf = res.getConfiguration();
         conf.locale = new Locale("en".toLowerCase());
         res.updateConfiguration(conf, dm);
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
         }
      }

      else if(sharedPref.getString(lan_key, "-1").equals("ar")){
         Resources res = activity.getResources();
         // Change locale settings in the app.
         DisplayMetrics dm = res.getDisplayMetrics();
         android.content.res.Configuration conf = res.getConfiguration();
         conf.locale = new Locale("ar".toLowerCase());
         res.updateConfiguration(conf, dm);

         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
         }
      }

      else {
         Resources res = activity.getResources();
         // Change locale settings in the app.
         DisplayMetrics dm = res.getDisplayMetrics();
         android.content.res.Configuration conf = res.getConfiguration();
         conf.locale = new Locale("en".toLowerCase());
         res.updateConfiguration(conf, dm);
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
         }
      }

   }
   static SharedPreferences sharedPreferences;
   public static void set_user_language(Context context,String user_id){
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString(lan_key,user_id);
      editor.commit();
   }
   public static String get_user_language(Context context){
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

      return sharedPreferences.getString(lan_key,"en");
   }

   public static JSONObject  get_user_language_words(Context context){
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      JSONObject jsonObject = new JSONObject();
      try {
          jsonObject = new JSONObject(sharedPreferences.getString(words_key,"-1"));
            jsonObject = jsonObject.getJSONObject(get_user_language(context));
      } catch (JSONException e) {
         e.printStackTrace();
      }
      return jsonObject;
   }

   public static void set_user_language_words(Context context,String user_id){
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString(words_key,user_id);
      editor.commit();
   }

   public static String get_append(Context context){
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      if(sharedPreferences==null)
         return "";

      if(sharedPreferences.getString(lan_key,"en").equals("ar"))
      {
         return "_ar";
      }
      return "";
   }

   public static String get_user_id(Context context){
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      return sharedPreferences.getString(user_key,"-1");
   }
   public static  void set_user_id(Context context,String user_id){
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString(user_key,user_id);
      editor.commit();
   }

   public static String getword(Context context,String word)
   {
      JSONObject words = get_user_language_words(context);
      try {
         return words.getString(word);
      } catch (JSONException e) {
         e.printStackTrace();
         return word;
      }
   }



   public static void downloadFilefromlist(final SongModel song,final Context context){
      final ProgressDialog progressDialog = new ProgressDialog(context);
      progressDialog.setMessage("downloading..");
      progressDialog.setCancelable(false);
      progressDialog.show();
      Uri downloadUri = Uri.parse(song.getSong());
      String folder_main = "Radio App";
      File f = new File(Environment.getExternalStorageDirectory(), folder_main);
      if (!f.exists()) {
         f.mkdirs();
         f.setReadable(false,true);
      }
      final Uri destinationUri = Uri.parse(f.getPath() + "/" + song.getSongName() + ".mp3");
      DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
              .addCustomHeader("Auth-Token", "YourTokenApiKey")
              .setRetryPolicy(new DefaultRetryPolicy())
              .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
              .setDownloadListener(new DownloadStatusListener() {
                 @Override
                 public void onDownloadComplete(int idi) {
                    Log.e("down_load", "completed");

                    Toast.makeText(context, "download completed", Toast.LENGTH_SHORT).show();
                    final DatabaseHandler db = new DatabaseHandler(context);
                    ArtistModel download_artist = new ArtistModel(song.getArtist_Name(),
                            song.getArtist_id(),
                            song.getImage(),
                            song.getDescription());

                    db.addContact(song, download_artist,"-1");
                    db.close();
                    //  player_fragmnet.updateThumb(NavigationActivity.this,id, album_art_url, artistname, songname, viewcount);
               if(progressDialog!=null)
                  progressDialog.dismiss();

                 }

                 @Override
                 public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                    Log.e("down_load", "failed");
                    Toast.makeText(context,"download failed",Toast.LENGTH_SHORT).show();
                    if(progressDialog!=null)
                       progressDialog.dismiss();
                 }
                 @Override
                 public void onProgress(int id, long totalBytes, long downlaodedBytes, int progress) {
                    Log.e(String.valueOf(totalBytes), String.valueOf(downlaodedBytes));
                    progressDialog.setMessage("Downloading..."+progress+"%");
                 }
              });
      DownloadManager downloadManager = new ThinDownloadManager();
      downloadManager.add(downloadRequest);
   }

   public static String get_gcmid(Context context) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      return sharedPreferences.getString("gcm_id", "-1");
   }
   public static void set_gcmid(Context context, String gcm_id) {
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString("gcm_id",gcm_id);
      editor.commit();
   }

}
