package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashScreenActivity extends Activity {
    LinearLayout language_layout;
    LinearLayout english,arabic;
    private final Handler handler = new Handler();

    private final Runnable startActivityRunnable = new Runnable() {
        @Override
        public void run() {

            //language_layout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(SplashScreenActivity.this,SplashScreen.class);
            startActivity(intent);
            finish();

        }

    };
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            language_layout = (LinearLayout) findViewById(R.id.language_layout);
            english = (LinearLayout) findViewById(R.id.english);
            arabic = (LinearLayout) findViewById(R.id.arabic);
           // img = (ImageView) findViewById(R.id.imageView);
            //get_language_words();
            english.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Settings.set_user_language(SplashScreenActivity.this,"en");
                    Intent intent = new Intent(SplashScreenActivity.this,AdvertisementActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            arabic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Settings.set_user_language(SplashScreenActivity.this,"ar");
                    Intent intent = new Intent(SplashScreenActivity.this,AdvertisementActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            get_language_words();
            //show_alert();
        }

        @Override
        protected void onResume() {
            super.onResume();

            AnimationSet set = new AnimationSet(true);

            Animation fadeIn = FadeIn(1000);
            fadeIn.setStartOffset(0);
            set.addAnimation(fadeIn);

            Animation fadeOut = FadeOut(1000);
            fadeOut.setStartOffset(2000);
            // set.addAnimation(fadeOut);

//            img.startAnimation(set);


        }

        public void onPause() {
            super.onPause();
            handler.removeCallbacks(startActivityRunnable);
        }

        private Animation FadeIn(int t) {
            Animation fade;
            fade = new AlphaAnimation(0.0f, 1.0f);
            fade.setDuration(t);
            fade.setInterpolator(new AccelerateInterpolator());
            return fade;
        }

        private Animation FadeOut(int t) {
            Animation fade;
            fade = new AlphaAnimation(1.0f, 0.0f);
            fade.setDuration(t);
            fade.setInterpolator(new AccelerateInterpolator());
            return fade;
        }


    private void get_language_words(){
        String url = Settings.SERVER_URL+"words-json-android.php";
        Log.e("url", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.e("response is: ", jsonObject.toString());
                Settings.set_user_language_words(SplashScreenActivity.this, jsonObject.toString());
              //  Intent intent = new Intent(SplashScreenActivity.this,AdvertisementActivity.class);
               // startActivity(intent);
               // finish();
                handler.postDelayed(startActivityRunnable, 2000);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString());
             //   Intent intent = new Intent(SplashScreenActivity.this, AdvertisementActivity.class);
             //   startActivity(intent);
             //   finish();
                //Toast.makeText(SplashScreenActivity.this, "Cannot reach our servers, Check your connection", Toast.LENGTH_SHORT).show();
              //  finish();
                handler.postDelayed(startActivityRunnable, 2000);
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }

        private void show_alert(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialogBuilder.setMessage("No internet connection, You can listen only downloaded songs");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                }
            });

            alertDialog.show();
        }

    private void get_adds(){
        String url = Settings.SERVER_URL+"adds-json.php";
        Log.e("url", url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonObject) {

                Log.e("response is: ", jsonObject.toString());
                Intent intent = new Intent(SplashScreenActivity.this,AdvertisementActivity.class);
                startActivity(intent);
                finish();



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());
                show_alert();
                //Toast.makeText(SplashScreenActivity.this, "Cannot reach our servers, Check your connection", Toast.LENGTH_SHORT).show();
                  finish();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }

}

