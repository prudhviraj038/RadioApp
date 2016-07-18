package com.yellowsoft.radioapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sriven on 1/27/2016.
 */
public class PageListFragment extends Fragment {
    ArrayList<SongModel> songs;
    SongAdapterPages songAdapter;
    SongAdapterDownloaded songAdapterDownload;
    ProgressBar progressBar;

    FragmentTouchListner mCallBack;
    public interface FragmentTouchListner{
        public void songselected(ArrayList<SongModel> songs, int position);
        public void youselected(ArrayList<SongModel> songs, int position);
        public void itunesselected(ArrayList<SongModel> songs, int position);
        public void addtoqueue(SongModel song);
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
        return inflater.inflate(R.layout.song_fragment_layout_pages, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        songs = new ArrayList<>();
        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        TextView pagetittle = (TextView) view.findViewById(R.id.page_title);
        pagetittle.setText(getArguments().getString("title"));
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        songAdapter = new SongAdapterPages(getActivity(),songs,PageListFragment.this);
        gridview.setAdapter(songAdapter);

        if(getArguments().getString("type","no").equals("down"))
        {
            Log.e("artists","downloads");
            //SongsManager sm = new SongsManager();
            //songs = sm.getPlayList();\
            final DatabaseHandler db = new DatabaseHandler(getActivity());
            songs = db.getsongs_by_playlist("-1");
            db.close();
            songAdapterDownload = new SongAdapterDownloaded(getActivity(),songs,PageListFragment.this);
            gridview.setAdapter(songAdapterDownload);
            songAdapterDownload.notifyDataSetChanged();
        }
        else {
            Log.e("artists","adminplaylist");
            getArtists();
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               /* Intent intent = new Intent(getActivity(), MyActivity.class);
                intent.putExtra("songs", songs);
                intent.putExtra("position",i);
                startActivity(intent); */
                mCallBack.songselected(songs,i);
            }
        });

    }

    private void getArtists(){

        String url;
        if(getArguments().getBoolean("adminplaylist",false))
            url = Settings.SERVER_URL + "admin-playlist-songs-json.php?playlist_id="+getArguments().getString(Settings.ARTIST_ID);
        else
            url = Settings.SERVER_URL + "songs-json.php?"+getArguments().getString(Settings.ARTIST_ID);

        Log.e("url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
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
                                "0",getActivity()
                        ));
                    }
                    if(getArguments().getString(Settings.ARTIST_ID).equals("3"))
                        songAdapter.topten=true;
                    else
                        songAdapter.topten=false;

                    songAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                progressBar.setVisibility(View.GONE);
                            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
        progressBar.setVisibility(View.VISIBLE);

    }

    public void addtoqueue(int pos){
        mCallBack.addtoqueue(songs.get(pos));
    }
    public void youtubeselected(int pos){
        mCallBack.youselected(songs, pos);
    }

    public void ituneselected(int pos){
        mCallBack.itunesselected(songs, pos);
    }


}

