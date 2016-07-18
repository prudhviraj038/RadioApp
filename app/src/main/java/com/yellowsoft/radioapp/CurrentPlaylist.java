package com.yellowsoft.radioapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
 * Created by sriven on 3/21/2016.
 */
public class CurrentPlaylist extends Fragment{
    ArrayList<SongModel> songs;
    PlaylistAdapterPages songAdapter;
    ProgressBar progressBar;
    EditText search_field;
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
        return inflater.inflate(R.layout.current_playlist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        songs = new ArrayList<>();

        songs = (ArrayList<SongModel>)getArguments().getSerializable("songs");

        songAdapter = new PlaylistAdapterPages(getActivity(),songs,CurrentPlaylist.this,getArguments().getInt("pos"));
        GridView gridview = (GridView) view.findViewById(R.id.gridView);
        gridview.setAdapter(songAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               /* Intent intent = new Intent(getActivity(), MyActivity.class);
                intent.putExtra("songs", songs);
                intent.putExtra("position",i);
                startActivity(intent); */
                mCallBack.songselected(songs, i);
            }
        });



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
