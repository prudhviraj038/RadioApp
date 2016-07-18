package com.yellowsoft.radioapp;

import android.app.Activity;
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
public class GalleryListActivity extends Fragment {
    ArrayList<String> images;
    ArrayList<String> desc;
    GalleryAdapter songAdapter;
    ProgressBar progressBar;
    CircleImageView artist_image;
    FragmentTouchListner mCallBack;
    TextView photo_album;
    public interface FragmentTouchListner{
        public void songselected(ArrayList<String> image,ArrayList<String> desc, int position);
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
        return inflater.inflate(R.layout.song_fragment_layout_gallery, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        images = new ArrayList<>();
        desc = new ArrayList<>();

        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        TextView pagetittle = (TextView) view.findViewById(R.id.page_title);
        artist_image = (CircleImageView) view.findViewById(R.id.artist_image);
        photo_album = (TextView) view.findViewById(R.id.photo_album);
        pagetittle.setText(Html.fromHtml(getArguments().getString("desc")));
        Picasso.with(getActivity()).load(getArguments().getString("artist_image")).into(artist_image);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        songAdapter = new GalleryAdapter(getActivity(),images);
        gridview.setAdapter(songAdapter);
        getArtists();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallBack.songselected(images,desc,i);
            }
        });

            }

    private void getArtists(){

        String url;
        url = Settings.SERVER_URL + "artist-gallery-json.php?"+getArguments().getString(Settings.ARTIST_ID);
        Log.e("url",url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("artists");
                    for(int i=0;i<jsonArray.length();i++){
                        images.add(jsonArray.getJSONObject(i).getString("image"));
                        desc.add(jsonArray.getJSONObject(i).getString("description"+Settings.get_append(getActivity())));
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

