package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
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

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sriven on 1/27/2016.
 */
public class PlaylistSongListActivity extends Fragment {
    ArrayList<SongModel> songs;
    SongAdapterPlaylistSongs songAdapter;
    ProgressBar progressBar;
    CircleImageView artist_image;
    FragmentTouchListner mCallBack;
    TextView pagetittle;
    ArtistModel artist;
    public interface FragmentTouchListner{
        public void songselected(ArrayList<SongModel> songs, int position);
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
        return inflater.inflate(R.layout.song_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getView();
        songs = new ArrayList<>();
        GridView gridview = (GridView)view.findViewById(R.id.gridView);
        pagetittle = (TextView) view.findViewById(R.id.page_title);
        TextView photoalbum = (TextView) view.findViewById(R.id.photo_album);
        photoalbum.setVisibility(View.GONE);
        artist_image = (CircleImageView) view.findViewById(R.id.artist_image);
        artist_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    rename_playlist();
            }
        });
        pagetittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                     rename_playlist();
            }
        });
        artist = new ArtistModel(getArguments().getString("title"), getArguments().getString(Settings.ARTIST_ID),
                getArguments().getString("artist_image"), getArguments().getString("desc"));
        pagetittle.setText(Html.fromHtml(getArguments().getString("title")));
        Uri uri = Uri.fromFile(new File(getArguments().getString("artist_image")));
        Picasso.with(getActivity()).load(uri).placeholder(R.drawable.aa).into(artist_image);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        songAdapter = new SongAdapterPlaylistSongs(getActivity(),songs,PlaylistSongListActivity.this);
        gridview.setAdapter(songAdapter);
        DatabaseHandler db =new DatabaseHandler(getActivity());
        songs = db.getsongs_by_playlist(getArguments().getString(Settings.ARTIST_ID));
        songAdapter = new SongAdapterPlaylistSongs(getActivity(),songs,PlaylistSongListActivity.this);
        gridview.setAdapter(songAdapter);
        Log.e("size is", String.valueOf(songs.size()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               /* Intent intent = new Intent(getActivity(), MyActivity.class);
                intent.putExtra("songs", songs);
                intent.putExtra("position",i);
                startActivity(intent); */
                Log.e("song_url",songs.get(i).getSong());
                mCallBack.songselected(songs, i);
            }
        });
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
                        ) );
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

    public  void rename_playlist(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Edit Playlist Name");
        final EditText input = new EditText(getActivity());
        input.setText(getArguments().getString("title"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,1f);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setPositiveButton("submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseHandler db =new DatabaseHandler(getActivity());

                        artist.name=input.getText().toString();
                        db.updateplaylist(artist);
                        //db.addPlaylist(new ArtistModel(input.getText().toString(),String.valueOf(artists.size()),"no-image","no des"));
                        db.close();
                        pagetittle.setText(Html.fromHtml(getArguments().getString("title")));

                    }
                });

        alertDialog.show();

    }

    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    Bitmap sample;
    String imgPath;
    int selected = 0;
    public void selectphotos(int selected) {
        this.selected = selected;
        final String[] items = new String[]{"camera", "gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setTitle("select_image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mImageCaptureUri = Uri.fromFile(file);
                    try {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(galleryIntent, PICK_FROM_FILE);
                }
            }
        });

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);

        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_FILE && resultCode == Activity.RESULT_OK ) {
            mImageCaptureUri = data.getData();
            String path = getRealPathFromURI(mImageCaptureUri); //from Gallery

            if (path == null)
                path = mImageCaptureUri.getPath();
            //from File Manag\\
            if (path != null)
                imgPath = path;
            Intent intent = new Intent(getActivity(), ImageEditActivity.class);
            intent.putExtra("image_path", imgPath);
            intent.putExtra("image_source", "gallery");
            intent.putExtra("rotation", String.valueOf(getCameraPhotoOrientation(getActivity(),mImageCaptureUri,imgPath)));
            startActivityForResult(intent, 4);

        } else if (requestCode == PICK_FROM_CAMERA && resultCode == Activity.RESULT_OK ) {
            String path = mImageCaptureUri.getPath();
            imgPath = path;
            Intent intent = new Intent(getActivity(), ImageEditActivity.class);
            intent.putExtra("image_path", imgPath);
            intent.putExtra("image_source", "device_cam");
            intent.putExtra("rotation", String.valueOf(getCameraPhotoOrientation(getActivity(), mImageCaptureUri, imgPath)));
            startActivityForResult(intent, 4);

        }
        else if (requestCode == 4) {
            String file_path = data.getStringExtra("image_path");
            Log.e("ile_path", file_path);
            sample = BitmapFactory.decodeFile(file_path);
            imgPath = file_path;
            artist.image=imgPath;
            DatabaseHandler db =new DatabaseHandler(getActivity());
            //db.addPlaylist(new ArtistModel(input.getText().toString(),String.valueOf(artists.size()),"no-image","no des"));
            db.updateplaylist(artist);
            db.close();
            Uri uri = Uri.fromFile(new File(artist.image));
            Picasso.with(getActivity()).load(uri).placeholder(R.drawable.aa).into(artist_image);
        }
        else{
            Log.e("activity","not returned");
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            //  context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

}

