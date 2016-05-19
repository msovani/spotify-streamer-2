package com.sovani.spotifystreamer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sovani.spotifystreamer.CentralReader.CentralAPIManager;
import com.sovani.spotifystreamer.model.ParcelableTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayTrackActivityFragment extends DialogFragment {


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
    private boolean touchinprogress;

    private TrackServiceBridgeCommander trackServiceBridgeCommander;

    static PlayTrackActivityFragment newInstance() {
        return new PlayTrackActivityFragment();
    }

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
        }else {
            trackList = savedInstanceState.getParcelableArrayList("TOP_TEN_LIST");
            position = savedInstanceState.getInt("TOP_TEN_POSITION");
            setTrackList(trackList, position);
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
                    touchinprogress = true;
                    if (currPos != null) currPos.setText(getResources().getString(R.string.duration_title, mediaPlayer.getCurrentPosition() / 1000));

                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBarParam) {
                if (mediaPlayer!=null)
                {
                    mediaPlayer.seekTo(seekBarParam.getProgress());
                    mediaPlayer.start();
                    touchinprogress = false;

                }
            }
        });

        if (savedInstanceState == null) {
            gotoTrack(position);
        }else {
            showTrack(position);
            refreshControls();
        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (trackList != null) {
            outState.putParcelableArrayList("TOP_TEN_LIST", trackList);
        }
        outState.putInt("TOP_TEN_POSITION", position);
    }

    @Override
    public void onStart() {

        if (CentralAPIManager.getInstance().getMaudioPlayBackService(getActivity().getApplicationContext()).getMediaPlayer() != null)
        {
            mediaPlayer = CentralAPIManager.getInstance().getMaudioPlayBackService(getActivity().getApplicationContext()).getMediaPlayer();
            isPlaying = mediaPlayer.isPlaying();
        }

        if (mHandler == null) {
            mHandler = new Handler();

            statusUpdater = new Runnable() {

                @Override
                public void run() {
                    try {
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                seekBar.setMax(mediaPlayer.getDuration());

                                int max_duration = mediaPlayer.getDuration()/1000;
                                int hours =  max_duration / 3600;
                                int minutes = (max_duration  % 3600) / 60;
                                int seconds = max_duration  % 60;

                                String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                                if (maxPos != null) maxPos.setText(getResources().getString(R.string.duration_title, timeString));
                                int mCurrentPosition = mediaPlayer.getCurrentPosition();

                                if (!touchinprogress) seekBar.setProgress(mCurrentPosition);
                                int curr_postion = mediaPlayer.getCurrentPosition()/1000;

                                hours = curr_postion / 3600;
                                minutes = (curr_postion % 3600) / 60;
                                seconds = curr_postion  % 60;

                                timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                                if (currPos != null) currPos.setText(getResources().getString(R.string.duration_title, timeString));
                            }
                            refreshControls();
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

    //Displays the track information on screen.
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
                playTrack();
        }

        if (trackServiceBridgeCommander != null)
        {
            trackServiceBridgeCommander.setPosition(trackNumber);
        }


    }
    //This method takes user to previous track. If user is on first track it goes back to last track.
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

    //This method takes user to next track. If user is on last track it goes back to first track.
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

    //This method sets the preview URL of the track to media player service.
    private void playTrack(){

            CentralAPIManager.getInstance().getMaudioPlayBackService(getActivity().getApplicationContext()).setTracks(trackList.get(position).getPreviewURL());
            mediaPlayer = CentralAPIManager.getInstance().getMaudioPlayBackService(getActivity().getApplicationContext()).getMediaPlayer();
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        refreshControls();
                    }
                });
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        refreshControls();
                        return false;
                    }
                });
            }


    }

    //Function where the play/ pause toggle is handled.
    private void playPause()
    {
        if (mediaPlayer == null)
        {
            isPlaying = false;
        }else{
            isPlaying = mediaPlayer.isPlaying();
        }

        if (isPlaying)
        {
            isPlaying = false;
            pauseTrack();

        }else {
            isPlaying = true;
            if (mediaPlayer!= null)
            {
                mediaPlayer.start();
            }else {
                playTrack();
            }

        }
        refreshControls();
    }

    //Refresh screen based on the state of media player and the track length and position.
    private void refreshControls(){
        if (mediaPlayer == null)
        {
            isPlaying = false;
        }else{
            isPlaying = mediaPlayer.isPlaying();
        }
        if (isPlaying)
        {
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            seekBar.setVisibility(View.VISIBLE);
            currPos.setVisibility(View.VISIBLE);
            maxPos.setVisibility(View.VISIBLE);
        }else {
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            seekBar.setVisibility(View.INVISIBLE);
            currPos.setVisibility(View.INVISIBLE);
            maxPos.setVisibility(View.INVISIBLE);
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
            mHandler = null;
        }
        super.onDestroy();

    }


    //Interface through with the fragment communicates with the parent/ caller activity.
    public interface TrackServiceBridgeCommander {

        public ArrayList<ParcelableTrack> getTracks();

        public int getPosition();

        public void setPosition(int pos);

    }

}
