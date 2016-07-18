package com.yellowsoft.radioapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    String [] MenuTitles = new String[]{"Home","Artist","New Songs"
            ,"News","Top 10","Playlist","Videos","Remix","Songs","Radio","Contact Us","English" };
    public  int [] prgmImages={R.drawable.home,
            R.drawable.artist,R.drawable.new_songs,
            R.drawable.news,R.drawable.topten,
            R.drawable.playlist,R.drawable.videos,
            R.drawable.remix,R.drawable.songs,
            R.drawable.radio,R.drawable.contact,
            R.drawable.launguage};
    SlidingPaneLayout mSlidingPanel;
    ListView mMenuList;
    ImageView menu_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        mMenuList = (ListView) findViewById(R.id.MenuList);
      //  mMenuList.setAdapter(new CustomAdapter(this, MenuTitles, prgmImages));
        //	mSlidingPanel.setPanelSlideListener(panelListener);
        mSlidingPanel.setParallaxDistance(200);
        mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i==1){
                        Intent intent = new Intent(MainActivity.this,ArtistListActivity.class);
                        startActivity(intent);
                    }
                if(i==8){
                    Intent intent = new Intent(MainActivity.this,AndroidBuildingMusicPlayerActivity.class);
                    startActivity(intent);
                }


            }
        });
        menu_icon = (ImageView) findViewById(R.id.menu_icon);
        menu_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSlidingPanel.isOpen())
                    mSlidingPanel.closePane();
                    else
                    mSlidingPanel.openPane();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
