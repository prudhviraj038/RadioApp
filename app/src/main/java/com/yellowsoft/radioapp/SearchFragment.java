package com.yellowsoft.radioapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by sriven on 3/21/2016.
 */
public class SearchFragment extends Fragment{
    ArrayList<SongModel> songs;
    ArrayList<String> suggestions;
    SearchAdapterPages songAdapter;
    autosearchadapter autosearch;
    ProgressBar progressBar;
    EditText search_field;
    FragmentTouchListner mCallBack;
    GridView gridviewsug;
    Boolean run_auto = true;
    public interface FragmentTouchListner{
        public void songselected(ArrayList<SongModel> songs, int position);
        public void youselected(ArrayList<SongModel> songs, int position);
        public void itunesselected(ArrayList<SongModel> songs, int position);
        public void addtoqueue(SongModel song);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

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
        return inflater.inflate(R.layout.search_fragment_layout_pages, container, false);
    }
    TextView pagetittle;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        songs = new ArrayList<>();
        suggestions = new ArrayList<>();
        GridView gridview = (GridView)view.findViewById(R.id.gridView);
         gridviewsug = (GridView)view.findViewById(R.id.gridView3);
         pagetittle = (TextView) view.findViewById(R.id.page_title);
        pagetittle.setText("");
        search_field = (EditText) view.findViewById(R.id.editText);
        search_field.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() >= 2){
                    getSuggestions(String.valueOf(s));
                    gridviewsug.setVisibility(View.VISIBLE);
                }
                else
                    gridviewsug.setVisibility(View.GONE);

            }
        });
        search_field.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            getArtists(search_field.getText().toString());
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        songAdapter = new SearchAdapterPages(getActivity(),songs,SearchFragment.this);
        autosearch = new autosearchadapter(getActivity(),suggestions);
        gridview.setAdapter(songAdapter);
        gridviewsug.setAdapter(autosearch);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallBack.songselected(songs, i);
            }
        });

        gridviewsug.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getArtists(suggestions.get(i));
                gridviewsug.setVisibility(View.GONE);
            }
        });
    }

    private void getArtists(String search){

        String url = "f";
        try {
            url = Settings.SERVER_URL + "songs-json.php?search="+ URLEncoder.encode(search,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        pagetittle.setText("search results for '"+search+"'");
        Log.e("url", url);
        songs.clear();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                gridviewsug.setVisibility(View.INVISIBLE);
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
                    gridviewsug.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                progressBar.setVisibility(View.GONE);
                gridviewsug.setVisibility(View.GONE);
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().cancelPendingRequests();
        AppController.getInstance().addToRequestQueue(jsObjRequest);
        progressBar.setVisibility(View.VISIBLE);

    }

    private void getSuggestions(String search){
        suggestions.clear();
        suggestions.add(search);
        String url;
        url = Settings.SERVER_URL + "auto-suggest-json.php?key="+search;
        pagetittle.setText("search results for '"+search+"'");
        Log.e("url", url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonObject) {
                gridviewsug.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Log.e("response is: ", jsonObject.toString());
                for(int i=0;i<jsonObject.length();i++) {
                    try {
                        suggestions.add(jsonObject.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                autosearch.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
                gridviewsug.setVisibility(View.GONE);
                //Toast.makeText(SplashScreenActivity.this, "Cannot reach our servers, Check your connection", Toast.LENGTH_SHORT).show();
                //  finish();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().cancelPendingRequests();
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
