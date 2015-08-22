package com.sovani.spotifystreamer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sovani.spotifystreamer.model.ParcelableTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayTrackActivityFragment extends Fragment {


    private ArrayList<ParcelableTrack> trackList;
    private int position;
    private ImageButton prevButton;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private boolean isPlaying;
    private TextView albumTitle;
    private TextView trackName;
    private TextView artistName;
    private MediaPlayer mediaPlayer;

    private ImageView albumCover;
    private SeekBar seekBar;
    private Handler mHandler = null;
    private Runnable statusUpdater = null;

    private TextView currPos;
    private TextView maxPos;

    private TrackServiceBridgeCommander trackServiceBridgeCommander;


    public TrackServiceBridgeCommander getTrackServiceBridgeCommander() {
        return trackServiceBridgeCommander;
    }

    public void setTrackServiceBridgeCommander(TrackServiceBridgeCommander trackServiceBridgeCommander) {
        this.trackServiceBridgeCommander = trackServiceBridgeCommander;
    }

    public void setTrackList(ArrayList<ParcelableTrack> trackList, int pos) {
        this.trackList = trackList;
        this.position = pos;
    }
    public PlayTrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView  =   inflater.inflate(R.layout.fragment_play_track, container, false);

        albumTitle = (TextView) rootView.findViewById(R.id.album_title);
        trackName = (TextView) rootView.findViewById(R.id.track_title);
        artistName = (TextView) rootView.findViewById(R.id.artist_name);

        seekBar = (SeekBar) rootView.findViewById(R.id.player_seek);

        prevButton = (ImageButton) rootView.findViewById(R.id.button_prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrevious();
            }
        });


        currPos = (TextView) rootView.findViewById(R.id.txt_current_position);
        maxPos = (TextView) rootView.findViewById(R.id.txt_max_position);




        nextButton = (ImageButton) rootView.findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

        playPauseButton = (ImageButton) rootView.findViewById(R.id.button_play_pause);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPause();
            }
        });

        if (savedInstanceState == null) {

            if (trackServiceBridgeCommander != null) {
                setTrackList(trackServiceBridgeCommander.getTracks(), trackServiceBridgeCommander.getPosition());
            }
        }

        if ( (trackList != null) && (trackList.size()>position)) {
             albumCover = (ImageView) rootView.findViewById(R.id.image_track);
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer!=null) {
                    if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                    if (currPos != null) currPos.setText(getResources().getString(R.string.duration_title, mediaPlayer.getCurrentPosition() / 1000));

                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBarParam) {
                if (mediaPlayer!=null)
                {
                    mediaPlayer.seekTo(seekBarParam.getProgress());
                    mediaPlayer.start();
                }
            }
        });

        if (savedInstanceState == null) {
            gotoTrack(position);
        }else {
            showTrack(position);
        }


        return rootView;
    }

    @Override
    public void onStart() {
//        startMusicService(true, trackList.get(position));
        if (mHandler == null) {
            mHandler = new Handler();

            statusUpdater = new Runnable() {

                @Override
                public void run() {
                    try {
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                seekBar.setMax(0);
                                seekBar.setMax(mediaPlayer.getDuration());
                                if (maxPos != null) maxPos.setText(getResources().getString(R.string.duration_title, mediaPlayer.getDuration() / 1000));
                                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(mCurrentPosition);
                                if (currPos != null) currPos.setText(getResources().getString(R.string.duration_title, mediaPlayer.getCurrentPosition() / 1000));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("PlayFragment", "Exception during progress update thread" + e.toString());
                    }
                    mHandler.postDelayed(this, 100);
                }
            };
            getActivity().runOnUiThread(statusUpdater);
        }

        super.onStart();
    }

    private void showTrack(int trackNumber) {
        ParcelableTrack track = trackList.get(trackNumber);

        albumTitle.setText(track.getAlbumName());
        trackName.setText(track.getTrackName());
        artistName.setText(track.getArtists());

        if (track.getUrl().length() > 0) {
            String url = track.getUrl();

            Picasso.with(getActivity()).load(url).placeholder(R.drawable.spotify_placeholder).into(albumCover);
        } else {
            Picasso.with(getActivity()).load(getResources().getResourceName(R.drawable.spotify_placeholder));
        }
    }

    private void gotoTrack(int trackNumber)
    {
        showTrack(trackNumber);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                playTrack();
            }
        }

        if (trackServiceBridgeCommander != null)
        {
            trackServiceBridgeCommander.setPosition(trackNumber);
        }


    }

    private void goPrevious()
    {
        if ( (trackList != null) && (trackList.size()>position)) {
            if (position == 0)
                position = trackList.size() - 1;
            else
                position = position - 1;
        }
        gotoTrack(position);
    }

    private void goNext()
    {
        if ( (trackList != null) && (trackList.size()>position)) {
            if (position == trackList.size() - 1)
                position = 0;
            else
                position = position + 1;
        }
        gotoTrack(position);
    }

    private void playTrack(){
        if (trackServiceBridgeCommander != null) {
            ArrayList<ParcelableTrack> selectedTrackList = new ArrayList<>();
            selectedTrackList.add(trackList.get(position));
            trackServiceBridgeCommander.playTracks(selectedTrackList);
            mediaPlayer = trackServiceBridgeCommander.getServiceMediaPlayer();
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        seekBar.setProgress(0);
                        playPause();
                    }
                });
            }

        }

    }

    public void playPause()
    {
        if (mediaPlayer == null)
        {
            isPlaying = false;
        }
        if (isPlaying)
        {
            isPlaying = false;
            pauseTrack();
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            seekBar.setVisibility(View.INVISIBLE);
            currPos.setVisibility(View.INVISIBLE);
            maxPos.setVisibility(View.INVISIBLE);
        }else {
            isPlaying = true;
            if (mediaPlayer!= null)
            {
                mediaPlayer.start();
            }else {
                playTrack();
            }
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            seekBar.setVisibility(View.VISIBLE);
            currPos.setVisibility(View.VISIBLE);
            maxPos.setVisibility(View.VISIBLE);

        }
    }

    private void pauseTrack(){
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }


    @Override
    public void onDestroy() {
        if ((mHandler != null) && (statusUpdater != null))
        {
            mHandler.removeCallbacks(statusUpdater);
        }
        super.onDestroy();

    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public interface TrackServiceBridgeCommander {
        public MediaPlayer getServiceMediaPlayer();

        public ArrayList<ParcelableTrack> getTracks();

        public int getPosition();

        public void setPosition(int pos);

        public void playTracks(ArrayList<ParcelableTrack> trackList);
    }

}
