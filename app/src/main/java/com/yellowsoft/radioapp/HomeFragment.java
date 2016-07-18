package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
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
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import dev.dworks.libs.astickyheader.SimpleSectionedGridAdapter;

/**
 * Created by sriven on 1/28/2016.
 */
public class HomeFragment extends Fragment {
    ArrayList<SongModel> songs;
    SongAdapterHome songAdapter;
    ProgressBar progressBar,artprogressBar;
    private SliderLayout mDemoSlider;
    TextView newsongs,following;
    FragmentTouchListner mCallBack;
    ArrayList<ArtistModel> artists;
    AritstAdapteHomePage aritstAdapter;
    HorizontalGridView gridview;
    GridElementAdapter adapter;

    public interface FragmentTouchListner{
        public void songselected(ArrayList<SongModel> songs, int position);
        public void youselected(ArrayList<SongModel> songs, int position);
        public void itunesselected(ArrayList<SongModel> songs, int position);
        public void artistselected(String artist_id,String name,String artistimage,String desc);
        public void addtoqueue(SongModel song);
        public void followingclicked();
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
        return inflater.inflate(R.layout.home_fragment, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);

        newsongs = (TextView) view.findViewById(R.id.page_title);
        following = (TextView) view.findViewById(R.id.following);

        newsongs.setText(Settings.getword(getActivity(),"new_songs"));
        following.setText(Settings.getword(getActivity(),"following"));

        artists = new ArrayList<>();
        gridview = (HorizontalGridView) view.findViewById(R.id.gridView2);
        aritstAdapter = new AritstAdapteHomePage(getActivity(),artists,this);
        adapter = new GridElementAdapter(getActivity(),artists,this);
        gridview.setAdapter(adapter);
        newsongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsongs.setBackgroundResource(R.drawable.black_border_full_color);
                following.setBackgroundResource(R.drawable.black_border_empty_color);
                getArtists(true);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Settings.get_user_id(getActivity()).equals("-1"))
                {
                    mCallBack.followingclicked();
                }
                else {
                    newsongs.setBackgroundResource(R.drawable.black_border_empty_color);
                    following.setBackgroundResource(R.drawable.black_border_full_color);
                    getArtists(false);
                }
            }
        });
        songs = new ArrayList<>();

        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        TextView pagetittle = (TextView) view.findViewById(R.id.page_title);
       // pagetittle.setText("New Songs");
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        artprogressBar = (ProgressBar)view.findViewById(R.id.progressBar3);
        songAdapter = new SongAdapterHome(getActivity(),songs,HomeFragment.this);
        gridview.setAdapter(songAdapter);
        get_banners();
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

    private void getArtists(final Boolean bnewsongs){

        String url;
        url = Settings.SERVER_URL + "songs-json.php?type=newsongs";
        if(!bnewsongs)
            url = Settings.SERVER_URL + "songs-json.php?cust_id="+Settings.get_user_id(getActivity());
        Log.e("url", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("songs");
                    if(jsonArray.length()>0) {
                        songs.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            songs.add(new SongModel(jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(i).getString("title_ar"),
                                    jsonArray.getJSONObject(i).getString("id"),
                                    jsonArray.getJSONObject(i).getString("image"),
                                    jsonArray.getJSONObject(i).getString("description"), jsonArray.getJSONObject(i).getString("description_ar"),
                                    jsonArray.getJSONObject(i).getString("song"),
                                    jsonArray.getJSONObject(i).getString("view_count"),
                                    jsonArray.getJSONObject(i).getString("artist_name"), jsonArray.getJSONObject(i).getString("artist_name_ar"),
                                    jsonArray.getJSONObject(i).getString("artist_id"),
                                    jsonArray.getJSONObject(i).getString("youtube"),
                                    jsonArray.getJSONObject(i).getString("itunes"),
                                    "0", getActivity()
                            ));
                        }
                        songAdapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(getActivity(), Settings.getword(getActivity(), "no_songs_in_follow"), Toast.LENGTH_SHORT).show();
                        newsongs.performClick();
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
    private void get_banners(){
        banners = new ArrayList<>();
        banner_liks = new ArrayList<>();
        String url = Settings.SERVER_URL+"banners-json.php";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                getArtists(true);
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("banners");
                    for(int i=0;i<jsonArray.length();i++){
                        banners.add(jsonArray.getJSONObject(i).getString("image"));
                        banner_liks.add(jsonArray.getJSONObject(i).getString("title"));
                        DefaultSliderView defaultSliderView = new DefaultSliderView(getActivity());
                        defaultSliderView.image(jsonArray.getJSONObject(i).getString("image"));
                        final int finalI = i;
                        defaultSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView baseSliderView) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner_liks.get(finalI)));
                                startActivity(browserIntent);
                            }
                        });
                        mDemoSlider.addSlider(defaultSliderView);
                    }
                    songAdapter.notifyDataSetChanged();
                    getArtists();
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
                getArtists(true);
            }
        });
        AppController.getInstance().addToRequestQueue(jsObjRequest);
        progressBar.setVisibility(View.VISIBLE);
    }
    ArrayList<String> banners;
    ArrayList<String> banner_liks;
    private void getArtists(){
        String url;
        artists.clear();
        url = Settings.SERVER_URL + "category-home-json.php";
        Log.e("cat_url",url);
        artprogressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                artprogressBar.setVisibility(View.GONE);
                try {
                    JSONArray jsonArraymain = jsonObject.getJSONArray("artists");

                        JSONArray jsonArray = jsonArraymain;
                    Log.e("cat_url",jsonArray.toString());
                        for(int i=0;i<jsonArray.length();i++){
                            artists.add(new ArtistModel(jsonArray.getJSONObject(i).getString("title"+Settings.get_append(getActivity())),
                                    jsonArray.getJSONObject(i).getString("id"),jsonArray.getJSONObject(i).getString("image"),
                                    jsonArray.getJSONObject(i).getString("description"+Settings.get_append(getActivity()))));
                        }

                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                artprogressBar.setVisibility(View.GONE);
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
        artprogressBar.setVisibility(View.VISIBLE);
    }
    public void artist_selected(int i){
        mCallBack.artistselected(artists.get(i).getArtist_id(), artists.get(i).getName(), artists.get(i).getImage(), artists.get(i).getDescription());

    }
}
