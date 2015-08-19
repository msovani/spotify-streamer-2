package com.sovani.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.sovani.spotifystreamer.MediaService.AudioPlayBackService;
import com.sovani.spotifystreamer.model.ParcelableTrack;

import java.util.ArrayList;

public class PlayTrackActivity extends AppCompatActivity {
    private boolean mBound;
    private ServiceConnection mConnection;
    AudioPlayBackService maudioPlayBackService;
    AudioPlayBackService.LocalBinder binder;

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
            binder = (AudioPlayBackService.LocalBinder) savedInstanceState.getBinder("AUDIO_BINDER");
        }

        initAudioServiceConnection();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mConnection != null)
        {
            outState.putBinder("AUDIO_BINDER", binder);
        }
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
            if (maudioPlayBackService != null){
                unbindService(mConnection);
            }
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    public void initAudioServiceConnection(){

        if (binder == null) {
            mConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    binder = (AudioPlayBackService.LocalBinder) service;
                    maudioPlayBackService = binder.getService();
                }

                public void onServiceDisconnected(ComponentName className) {
                    // This is called when the connection with the service has been
                    // unexpectedly disconnected -- that is, its process crashed.
                    maudioPlayBackService = null;
                }
            };

                if (mConnection != null) {
                    Intent startIntent = new Intent(this, AudioPlayBackService.class);
                    mBound = bindService(startIntent, mConnection, Context.BIND_AUTO_CREATE);
                }
        }else {
            maudioPlayBackService = binder.getService();
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
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
    public void pauseTrack()
    {

        if (maudioPlayBackService != null){
            maudioPlayBackService.pausePlayer();
        }
    }

    public void resumeTrack()
    {

        if (maudioPlayBackService != null){
            maudioPlayBackService.resumePlayer();
        }
    }

}
