package com.sovani.spotifystreamer;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.sovani.spotifystreamer.MediaService.AudioPlayBackService;
import com.sovani.spotifystreamer.CentralReader.CentralAPIManager;
import com.sovani.spotifystreamer.model.ParcelableTrack;

import java.util.ArrayList;

/**
 * This activity is used in phone ui mode to show the fragment for playing tracks.
 * It implements a TrackServiceBridgeCommander callback handler so that fragment can
 * get the track details.
 */
public class PlayTrackActivity extends AppCompatActivity implements PlayTrackActivityFragment.TrackServiceBridgeCommander {



    private ArrayList<ParcelableTrack> tracks = null;
    private int position = 0;

    private AudioPlayBackService maudioPlayBackService;

    private PlayTrackActivityFragment fragment;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<ParcelableTrack> getTracks() {
        return tracks;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b55c5c")));
        }
        tracks = this.getIntent().getParcelableArrayListExtra("TRACK_LIST");

        //Get instance of the Media Player service from Central API singleton.
        maudioPlayBackService = CentralAPIManager.getInstance().getMaudioPlayBackService(getApplicationContext());

        if (savedInstanceState == null) {
            setPosition(this.getIntent().getIntExtra("TRACK_POSITION", 0));


            fragment = new PlayTrackActivityFragment();
            if (tracks != null) {
                fragment.setTrackList(tracks, position );
            }
            fragment.setTrackServiceBridgeCommander(this);

            getSupportFragmentManager().beginTransaction().replace(
                    android.R.id.content, fragment, "PLAY_TRACK").commit();

            //Rubric states that app should start playing song as soon as user clicks on it.
            CentralAPIManager.getInstance().getMaudioPlayBackService(getApplicationContext()).setTracks(tracks.get(position).getPreviewURL());

        }else{

            //App has rotated hence we need to restore the state.
            setPosition(savedInstanceState.getInt("SAVED_TRACK_POSITION", 0));

            //No need to create a new fragment, we just find old fragment by its tag.
            fragment  = (PlayTrackActivityFragment) getSupportFragmentManager().findFragmentByTag("PLAY_TRACK");
            fragment.setTrackServiceBridgeCommander(this);
            if (tracks != null) {
                fragment.setTrackList(tracks, position);
            }
        }

        setContentView(R.layout.activity_play_track);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            String name = this.getIntent().getStringExtra("ARTIST_NAME");
            actionBar.setSubtitle(name);

        }

    }

    //Save position so that we can restore on rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("SAVED_TRACK_POSITION", position);
        super.onSaveInstanceState(outState);
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

    // Stop playing song when user goes back.
    @Override
    public void onBackPressed() {
        if (maudioPlayBackService!= null)
        {
            //Since the rubric does not explicitly state if we should stop playback when user presses back button.
            //The track is stopped here.
            maudioPlayBackService.getMediaPlayer().stop();
        }
        super.onBackPressed();
    }

}
