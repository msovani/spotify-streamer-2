package com.sovani.spotifystreamer.MediaService;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Main service which handles MediaPlayer instantiation and track play/ pause functions.
 **/
public class AudioPlayBackService extends Service {

    private static final String DEBUG_TAG = "AudioPBService";
    private MediaPlayer mp;

    private String track;



    private void playTracks(){
        try {
            Uri file = Uri.parse(track);
            if (mp == null ) {
                mp = new MediaPlayer();
            }else{
                mp.release();
                mp = new MediaPlayer();
            }
            mp.setDataSource(this, file);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            mp.prepareAsync();

        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Player failed", e);
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mp != null) {
            mp.stop();
        }

    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        super.onStart(intent, startId);
        mp.start();
        return Service.START_STICKY_COMPATIBILITY;
    }

    //This function takes a track URL as a string and sets it as a member variable.
    public void setTracks(String passedTrack)
    {
        track = passedTrack;
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
        }
        playTracks();
    }




    @Override
    public IBinder onBind(Intent intent) {
        Log.d(DEBUG_TAG, "In onBind with intent=" + intent.getAction());
        return mBinder;
    }
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public AudioPlayBackService getService() {
            // Return this instance of LocalService so clients can call public methods
            return AudioPlayBackService.this;
        }
    }

    //Expose the MediaPlayer so that the player dialog can set call backs on it directly.
    public MediaPlayer getMediaPlayer(){
        return mp;
    }

}