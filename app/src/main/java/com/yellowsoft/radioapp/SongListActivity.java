package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Toast;

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
public class SongListActivity extends Fragment {
    ArrayList<SongModel> songs;
    SongAdapter songAdapter;
    ProgressBar progressBar;
    CircleImageView artist_image;
    FragmentTouchListner mCallBack;
    TextView photo_album;
    public interface FragmentTouchListner{
        public void songselected(ArrayList<SongModel> songs,int position);
        public void youselected(ArrayList<SongModel> songs, int position);
        public void itunesselected(ArrayList<SongModel> songs, int position);
        public void addtoqueue(SongModel song);
        public void update_follow_unfollow(String following,String artistname,Boolean isfollowing);
        public void gallery_selected(String artist_id,String artist_desc,String artist_image);
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
        return inflater.inflate(R.layout.song_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        songs = new ArrayList<>();
        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        TextView pagetittle = (TextView) view.findViewById(R.id.page_title);
        artist_image = (CircleImageView) view.findViewById(R.id.artist_image);
        photo_album = (TextView) view.findViewById(R.id.photo_album);
        photo_album.setText(Settings.getword(getActivity(),"photo_album"));
        photo_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.gallery_selected(getArguments().getString(Settings.ARTIST_ID),
                        getArguments().getString("desc")
                        , getArguments().getString("artist_image"));
            }
        });
        pagetittle.setText(Html.fromHtml(getArguments().getString("desc")));
       // Toast.makeText(getActivity(), getArguments().getString("desc"), Toast.LENGTH_SHORT).show();
        Picasso.with(getActivity()).load(getArguments().getString("artist_image")).placeholder(R.drawable.aa).into(artist_image);
        Log.e("artist_image", getArguments().getString("artist_image"));
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        songAdapter = new SongAdapter(getActivity(),songs,SongListActivity.this);
        gridview.setAdapter(songAdapter);
        getArtists();
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
check_follow();
            }

    private void getArtists(){

        String url;
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

    private void check_follow(){
        String url;
        url = Settings.SERVER_URL +"follow_check.php?"+
                getArguments().getString(Settings.ARTIST_ID)+"&cust_id="+Settings.get_user_id(getActivity());
        Log.e("url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                Log.e("follow", jsonObject.toString());
                try {
                    String status = jsonObject.getString("status");
                    if(status.equals("0")){
                        mCallBack.update_follow_unfollow("follow",getArguments().getString("title"),false);
                    }
                    else{
                        mCallBack.update_follow_unfollow("unfollow",getArguments().getString("title"),true);
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
                progressBar.setVisibility(View.GONE);
            }
        });

        AppController.getInstance().addToRequestQueue(jsObjRequest);

    }


}

