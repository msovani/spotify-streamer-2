package com.sovani.spotifystreamer;

//import android.app.ActionBar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.sovani.spotifystreamer.model.ParcelableTrack;

import java.util.ArrayList;


public class TopTenActivity extends AppCompatActivity implements TopTenFragment.PlayTrackHandler{

    @Override
    public void onTrackSelected(ArrayList<ParcelableTrack> trackList, int position) {
        ParcelableTrack track = trackList.get(position);

        //User has clicked on a track, and we need to play the track
        Intent trackIntent = new Intent(this, PlayTrackActivity.class);
        trackIntent.putExtra("ARTIST_NAME", track.getArtists());
        trackIntent.putParcelableArrayListExtra("TRACK_LIST", trackList);
        trackIntent.putExtra("TRACK_POSITION", position);
        this.startActivity(trackIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            String name = this.getIntent().getStringExtra("ARTIST_NAME");
            actionBar.setSubtitle(name);

        }
        ArrayList<ParcelableTrack> tracks = this.getIntent().getParcelableArrayListExtra("TRACK_LIST");


        if (savedInstanceState == null) {
            TopTenFragment topTenFragment = new TopTenFragment();
            if (tracks != null) {
                topTenFragment.setTrackList(tracks);
            }
            topTenFragment.setPlayTrackHandler(this);
            getSupportFragmentManager().beginTransaction().replace(
                    android.R.id.content, topTenFragment, "TOP_TEN_FRAGMENT_TAG").commit();

        }else{
            TopTenFragment topTenFragment  = (TopTenFragment) getSupportFragmentManager().findFragmentByTag("TOP_TEN_FRAGMENT_TAG");
            topTenFragment.setPlayTrackHandler(this);
            if (tracks != null) {
                topTenFragment.setTrackList(tracks);
            }
        }

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
