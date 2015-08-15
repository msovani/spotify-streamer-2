package com.sovani.spotifystreamer;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sovani.spotifystreamer.model.ParcelableTrack;

import java.util.ArrayList;

public class PlayTrackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_track);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            String name = this.getIntent().getStringExtra("ARTIST_NAME");
            actionBar.setSubtitle(name);

        }
        ArrayList<ParcelableTrack> tracks = this.getIntent().getParcelableArrayListExtra("TRACK_LIST");
        int position = this.getIntent().getIntExtra("TRACK_POSITION", 0);
        if (savedInstanceState == null) {
            PlayTrackActivityFragment playFragment = new PlayTrackActivityFragment();
            if (tracks != null) {
                playFragment.setTrackList(tracks, position);
            }

            getSupportFragmentManager().beginTransaction().replace(
                    android.R.id.content, playFragment, "TOP_TEN_FRAGMENT_TAG").commit();

        }else{
            PlayTrackActivityFragment playFragment  = (PlayTrackActivityFragment) getSupportFragmentManager().findFragmentByTag("TOP_TEN_FRAGMENT_TAG");
            if (tracks != null) {
                playFragment.setTrackList(tracks, position);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_track, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
