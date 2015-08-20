package com.sovani.spotifystreamer.MediaService;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class AudioPlayBackService extends Service {

    private static final String DEBUG_TAG = "AudioPBService";
    private MediaPlayer mp;
    private String[] tracks = {

    };
    private int currentTrack = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(DEBUG_TAG, "In onCreate.");

    }

    private void playTracks(){
        try {
            Uri file = Uri.parse(tracks[this.currentTrack]);
            mp = new MediaPlayer();
            mp.setDataSource(this, file);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();

                }
            });
            mp.prepare();


            mp.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    //We need to go to next track if we have more than 1 track.
                    if (tracks.length>1) {
                        currentTrack = (currentTrack + 1) % tracks.length;
                        Uri nextTrack = Uri.parse(tracks[currentTrack]);
                        try {
                            mp.reset();
                            mp.setDataSource(AudioPlayBackService.this, nextTrack);
                            mp.prepare();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }

            });

        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Player failed", e);
        }

    }



    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(DEBUG_TAG, "In onDestroy.");
        if(mp != null) {
            mp.stop();
        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        super.onStart(intent, startId);
        Log.d(DEBUG_TAG, "In onStart.");
        mp.start();
        return Service.START_STICKY_COMPATIBILITY;
    }

    public void setTracks(String[] passedTracks)
    {
        tracks = passedTracks;
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
        }
        playTracks();
    }

    public void pausePlayer()
    {

        if (mp != null) {
            if (mp.isPlaying()) {
                mp.pause();
            }
        }

    }

    public void resumePlayer()
    {

        if (mp != null) {
            if (mp.getCurrentPosition()>0) {
                mp.start();
            }
        }

    }


    public void startPlayer()
    {

        if (mp != null) {
            if (!mp.isPlaying()) {
                mp.start();
            }
        }

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

    public MediaPlayer getMediaPlayer(){
        return mp;
    }

}