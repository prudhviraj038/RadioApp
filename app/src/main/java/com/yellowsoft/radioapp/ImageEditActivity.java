package com.yellowsoft.radioapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Chinni on 13-11-2015.
 */
public class ImageEditActivity extends Activity {
    Boolean saveandfinish = false;
    Boolean saveasnew = false;
    String photopath,photosource;
    File imagefile;
    Float angle = 0f;
    CropImageView imageView;
    ProgressBar image_progres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.image_edit_view);

        photopath = getIntent().getStringExtra("image_path");
        Log.e("new_image_path", photopath);
        photosource = getIntent().getStringExtra("image_source");
        image_progres = (ProgressBar)(findViewById(R.id.image_progress));
        imagefile = new File(photopath);
        imageView = (CropImageView) findViewById(R.id.imageView38);
        imageView.setGuidelines(2);
        imageView.setImageBitmap(BitmapFactory.decodeFile(photopath));
        imageView.setAspectRatio(4,3);
        imageView.setFixedAspectRatio(true);
        TextView save_btn = (TextView) findViewById(R.id.save_btn);
        TextView crop_btn = (TextView) findViewById(R.id.crop_btn);

        ImageView rotate_right_btn = (ImageView) findViewById(R.id.rotate_right);
        ImageView rotate_left_btn = (ImageView) findViewById(R.id.rotate_left);
        TextView cancel_btn = (TextView) findViewById(R.id.cancel_btn);
        //angle = 90f;
        //new DownloadFilesTask().execute(photopath);
        rotate_right_btn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                   // new DownloadFilesTask().execute(photopath);
                                                  //  RotateAnimation anim = new RotateAnimation(angle, angle+90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                   /// anim.setInterpolator(new LinearInterpolator());
                                                  //  anim.setRepeatCount(Animation.INFINITE);
                                                    //anim.setDuration(700);
                                                    //anim.setFillAfter(true);
                                                    //imageView.startAnimation(anim);
                                                    //angle = angle + 90f;
                                                     imageView.rotateImage(90);

                                                }

                                            }
        );
        rotate_left_btn.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                  // new DownloadFilesTask().execute(photopath);
                                                  // RotateAnimation anim = new RotateAnimation(angle, angle-90f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                   //anim.setInterpolator(new LinearInterpolator());
                                                 //  anim.setRepeatCount(Animation.INFINITE);
                                                   //anim.setDuration(700);
                                                   //anim.setFillAfter(true);
                                                   //imageView.startAnimation(anim);
                                                   //angle = angle - 90f;
                                                   imageView.rotateImage(270);
                                               }

                                           }
        );
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_progres.setVisibility(View.VISIBLE);
                saveandfinish=true;



                new DownloadFilesTask().execute(photopath);

            }
        });

        crop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveandfinish=false;
                //new DownloadFilesTask().execute(photopath);
                Bitmap  croppedImage = imageView.getCroppedImage();
                imageView.setImageBitmap(croppedImage);

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("image_path",photopath);
                Log.e("image_path",photopath);
                setResult(4, intent);
                finish();
            }
        });

        rotate_right_btn.performClick();

    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");

        Intent intent=new Intent();
        intent.putExtra("image_path",photopath);
        Log.e("image_path",photopath);
        setResult(4, intent);
        finish();
    }

    private boolean rotate_image(File tempphoto,Float angle){

        String photopath_min = tempphoto.getPath().toString();
        Bitmap bmp = BitmapFactory.decodeFile(photopath_min);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Log.e("angle",String.valueOf(angle));
        FileOutputStream fOut;
        try {

          //  bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getHeight(), bmp.getHeight(), matrix, true);
            fOut = new FileOutputStream(photopath_min);
            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();

            final Bitmap finalBmp = bmp;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(finalBmp);
                }
            });
return  true;
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    return  false;
    }

    private class DownloadFilesTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = new ProgressDialog(ImageEditActivity.this);
        FileOutputStream out = null;
        Bitmap  croppedImage;
        @Override
        protected void onPreExecute() {
            image_progres.setVisibility(View.VISIBLE);
            croppedImage = imageView.getCroppedImage();
            imageView.setImageBitmap(croppedImage);

        }

        protected String doInBackground(String... imagepath) {
                        try {
                out = new FileOutputStream(photopath);
                croppedImage.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "completed";
                    }

        protected void onProgressUpdate(Integer... progress) {

        }
        @Override
        protected void onPostExecute(String result) {
                try {
                    if (out != null) {
                        out.close();
                        Intent intent=new Intent();
                        intent.putExtra("image_path",photopath);
                        Log.e("image_path",photopath);
                        setResult(4, intent);
                        finish();//finishing ac

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


    }


    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "Tbee_Camera");

        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }



}
