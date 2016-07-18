package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdvertisementActivity extends Activity {
    ImageView img;
    String url_ad = "http:\\/\\/www.instagram.com\\/danden_com";
    private final Handler handler = new Handler();

    private final Runnable startActivityRunnable = new Runnable() {
        @Override
        public void run() {
                Intent intent = new Intent(AdvertisementActivity.this, NavigationActivity.class);
                intent.putExtra("cat_id",getIntent().getStringExtra("cat_id"));
                startActivity(intent);
                finish();
        }
    };

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisement_screen);
        img = (ImageView) findViewById (R.id.splash_image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(startActivityRunnable);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_ad));
                startActivity(browserIntent);


            }
        });
        LinearLayout skip_btn = (LinearLayout) findViewById(R.id.skip_btn);
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdvertisementActivity.this, NavigationActivity.class);
                intent.putExtra("cat_id", getIntent().getStringExtra("cat_id"));
                startActivity(intent);
                handler.removeCallbacks(startActivityRunnable);
                finish();
            }
        });
        get_adds();

    }


    private void get_adds(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url = Settings.SERVER_URL+"adds-json.php";
        Log.e("url", url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonObject) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Log.e("response is: ", jsonObject.toString());
                try {
                    String image_url = jsonObject.getJSONObject(0).getString("image");
                    url_ad = jsonObject.getJSONObject(0).getString("link");
                    Picasso.with(AdvertisementActivity.this).load(image_url).into(img);
                    handler.postDelayed(startActivityRunnable, 2000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
                //Toast.makeText(SplashScreenActivity.this, "Cannot reach our servers, Check your connection", Toast.LENGTH_SHORT).show();
                //  finish();
                handler.postDelayed(startActivityRunnable, 2000);
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }




    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onPause()
    {
        super.onPause();
        handler.removeCallbacks(startActivityRunnable);
    }
    JSONObject words;
}
