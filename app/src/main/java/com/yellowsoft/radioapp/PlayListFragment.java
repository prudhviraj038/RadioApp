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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sriven on 1/27/2016.
 */
public class PlayListFragment extends Fragment {
    ArrayList<ArtistModel> artists;
    PlaylistAdapter aritstAdapter;
    ProgressBar progressBar;
    FragmentTouchListner mCallBack;

    public interface FragmentTouchListner{
        public void playlistselected(String artist_id, String name, String artistimage, String desc);
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
        return inflater.inflate(R.layout.playlist_fragment_layout, container, false);
    }
    GridView gridview;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.artist_fragment_layout);
        View v = getView();
        artists = new ArrayList<>();
        gridview = (GridView)v.findViewById(R.id.gridView);
        aritstAdapter = new PlaylistAdapter(getActivity(),artists,this);
        progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
        gridview.setAdapter(aritstAdapter);
        //getArtists();
        getPlaylists();
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Intent intent = new Intent(getActivity(),SongListActivity.class);
                intent.putExtra("artist_id",artists.get(i).getArtist_id());
                startActivity(intent);*/
                //mCallBack.artistselected(artists.get(i).getArtist_id(), artists.get(i).getName(), artists.get(i).getImage(), artists.get(i).getDescription());

            if(i+1==artists.size())
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setMessage("Enter Playlist Name");
                final EditText input = new EditText(getActivity());
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
                                db.addPlaylist(new ArtistModel(input.getText().toString(),String.valueOf(artists.size()),"no-image","no des"));
                                db.close();
                                getPlaylists();
                            }
                        });

                alertDialog.show();

            }
                else{
                                mCallBack.playlistselected(artists.get(i).getArtist_id(), artists.get(i).getName(), artists.get(i).getImage(), artists.get(i).getDescription());
            }

            }
        });
    }

    public void delete_playlsit(int selected){
        this.selected = selected;

        DatabaseHandler db =new DatabaseHandler(getActivity());
        db.deletePlaylist(artists.get(selected));
        db.close();
        getPlaylists();

    }

    public  void rename_playlist(final int selected){
        this.selected = selected;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Enter Playlist Name");
        final EditText input = new EditText(getActivity());
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
                        artists.get(selected).name = input.getText().toString();
                        db.updateplaylist(artists.get(selected));
                        //db.addPlaylist(new ArtistModel(input.getText().toString(),String.valueOf(artists.size()),"no-image","no des"));
                        db.close();
                        getPlaylists();
                    }
                });

        alertDialog.show();


    }

    private void getPlaylists(){
        Log.e("get playlist","yes");
        DatabaseHandler db = new DatabaseHandler(getActivity());
        artists = db.getAllPlaylist();
        db.close();
        aritstAdapter = new PlaylistAdapter(getActivity(),artists,this);
        gridview.setAdapter(aritstAdapter);
        aritstAdapter.notifyDataSetChanged();
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
            artists.get(selected).image=imgPath;
            DatabaseHandler db =new DatabaseHandler(getActivity());
            //db.addPlaylist(new ArtistModel(input.getText().toString(),String.valueOf(artists.size()),"no-image","no des"));
            db.updateplaylist(artists.get(selected));
            db.close();
            getPlaylists();

            aritstAdapter.notifyDataSetChanged();

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

