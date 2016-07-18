package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sriven on 1/27/2016.
 */
public class NewsListActivity extends Fragment {
    ArrayList<SongModel> songs;
    NewsAdapter songAdapter;
    ProgressBar progressBar;
    CircleImageView artist_image;
    FragmentTouchListner mCallBack;
    public interface FragmentTouchListner{
        public void newsselected(ArrayList<SongModel> songs, int position);

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
        return inflater.inflate(R.layout.video_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        songs = new ArrayList<>();
        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        TextView pagetittle = (TextView) view.findViewById(R.id.page_title);
        artist_image = (CircleImageView) view.findViewById(R.id.artist_image);
        pagetittle.setText(getArguments().getString("page_title"));
       // Picasso.with(getActivity()).load(getArguments().getString("artist_image")).into(artist_image);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        songAdapter = new NewsAdapter(getActivity(),songs,NewsListActivity.this);
        gridview.setAdapter(songAdapter);
        getArtists();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              //  Intent intent = new Intent(getActivity(), YoutubePlayer.class);
                //intent.putExtra("video", songs.get(i).getSong());
                //startActivity(intent);
                 mCallBack.newsselected(songs,i);
            }
        });
    }

    private void getArtists(){

        String url;
        url = Settings.SERVER_URL + "news-json.php?"+getArguments().getString(Settings.ARTIST_ID);
        Log.e("url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("videos");
                    for(int i=0;i<jsonArray.length();i++){
                        songs.add(new SongModel(jsonArray.getJSONObject(i).getString("title"),jsonArray.getJSONObject(i).getString("title"),
                                jsonArray.getJSONObject(i).getString("id"),
                                jsonArray.getJSONObject(i).getString("image"),
                                jsonArray.getJSONObject(i).getString("description"),
                                jsonArray.getJSONObject(i).getString("description"),
                                jsonArray.getJSONObject(i).getString("description"),
                                jsonArray.getJSONObject(i).getString("id"),
                                jsonArray.getJSONObject(i).getString("id"),
                                jsonArray.getJSONObject(i).getString("id"),
                                jsonArray.getJSONObject(i).getString("id"),
                                jsonArray.getJSONObject(i).getString("id"),
                                jsonArray.getJSONObject(i).getString("id"),
                                "0",getActivity()
                        ));
                    }
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




}

