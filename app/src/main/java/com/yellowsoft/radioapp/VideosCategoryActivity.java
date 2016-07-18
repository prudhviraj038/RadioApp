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
public class VideosCategoryActivity extends Fragment {
    ArrayList<ArtistModel> artists;
    AritstAdapterVideos aritstAdapter;
    ProgressBar progressBar;
    FragmentTouchListner mCallBack;
    public interface FragmentTouchListner{
        public void videocategoryselected(String artist_id, String name, String artistimage, String desc);
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
        return inflater.inflate(R.layout.artist_fragment_layout_videos, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.artist_fragment_layout);
        View v = getView();
        artists = new ArrayList<>();
        GridView gridview = (GridView)v.findViewById(R.id.gridView);
        aritstAdapter = new AritstAdapterVideos(getActivity(),artists);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        gridview.setAdapter(aritstAdapter);
        getArtists();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Intent intent = new Intent(getActivity(),SongListActivity.class);
                intent.putExtra("artist_id",artists.get(i).getArtist_id());
                startActivity(intent);*/
                mCallBack.videocategoryselected(artists.get(i).getArtist_id(), artists.get(i).getName(), artists.get(i).getImage(), artists.get(i).getDescription());
            }
        });
    }

    private void getArtists(){
        String url;
        url = Settings.SERVER_URL + "video-category-json.php";
        Log.e("url", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("categories");
                    for(int i=0;i<jsonArray.length();i++){
                        artists.add(new ArtistModel(jsonArray.getJSONObject(i).getString("title"+Settings.get_append(getActivity())),
                                jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("image"),
                                jsonArray.getJSONObject(i).getString("title")));
                    }
                    aritstAdapter.notifyDataSetChanged();

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

