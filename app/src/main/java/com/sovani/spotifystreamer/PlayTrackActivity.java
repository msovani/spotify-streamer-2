package com.sovani.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sovani.spotifystreamer.MediaService.AudioPlayBackService;
import com.sovani.spotifystreamer.CentralReader.CentralAPIManager;
import com.sovani.spotifystreamer.model.ParcelableTrack;

import java.util.ArrayList;

public class PlayTrackActivity extends AppCompatActivity implements PlayTrackActivityFragment.TrackServiceBridgeCommander {



    private ArrayList<ParcelableTrack> tracks = null;
    private int position = 0;

    AudioPlayBackService maudioPlayBackService;

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

    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracks = this.getIntent().getParcelableArrayListExtra("TRACK_LIST");

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

        }else{

            setPosition(savedInstanceState.getInt("SAVED_TRACK_POSITION", 0));



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

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("SAVED_TRACK_POSITION", position);
        super.onSaveInstanceState(outState);
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

    @Override
    public void onBackPressed() {
        if (maudioPlayBackService!= null)
        {
            maudioPlayBackService.getMediaPlayer().stop();
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    public void playTracks(ArrayList<ParcelableTrack> trackList){


        if (maudioPlayBackService != null){
            String[] tracks = new String[trackList.size()];
            int i=0;
            for (ParcelableTrack track : trackList)
            {
                tracks[i] =   track.getPreviewURL();
                i++;
            }
            maudioPlayBackService.setTracks(tracks);
        }
    }


    public MediaPlayer getServiceMediaPlayer(){
        MediaPlayer mediaPlayer = null;
        if (maudioPlayBackService != null){
            mediaPlayer = maudioPlayBackService.getMediaPlayer();
            if (fragment != null)
            {
                fragment.setMediaPlayer(mediaPlayer);

            }
        }
        return mediaPlayer;
    }


}
