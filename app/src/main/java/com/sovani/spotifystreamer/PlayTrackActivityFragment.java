package com.sovani.spotifystreamer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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



    public void setTrackList(ArrayList<ParcelableTrack> trackList, int pos) {
        this.trackList = trackList;
        this.position = pos;
    }
    public PlayTrackActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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


        if ( (trackList != null) && (trackList.size()>position)) {
             albumCover = (ImageView) rootView.findViewById(R.id.image_track);


            gotoTrack(position);
        }

        if (mHandler == null) {
            mHandler = new Handler();
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            seekBar.setMax(0);
                            seekBar.setMax(mediaPlayer.getDuration());
                            int mCurrentPosition = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(mCurrentPosition);
                        }else {
                            seekBar.setMax(0);
                            seekBar.setProgress(0);
                        }
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
        }

        return rootView;
    }

    private void gotoTrack(int trackNumber)
    {
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
        if (isPlaying)
        {
            playTrack();
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
            ArrayList<ParcelableTrack> selectedTrackList = new ArrayList<ParcelableTrack>();
            selectedTrackList.add(trackList.get(position));
            ((PlayTrackActivity) getActivity()).playTracks(selectedTrackList);
            mediaPlayer = ((PlayTrackActivity) getActivity()).getServiceMediaPlayer();
            if (mediaPlayer!=null) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        seekBar.setProgress(0);
                        playPause();
                    }
                });
            }



    }

    private void playPause()
    {
        if (isPlaying)
        {
            isPlaying = false;
            pauseTrack();
            playPauseButton.setImageResource(R.drawable.ic_play_arrow_black);
            seekBar.setVisibility(View.GONE);
        }else {
            isPlaying = true;
            playTrack();
            playPauseButton.setImageResource(R.drawable.ic_pause_black);
            seekBar.setVisibility(View.VISIBLE);
        }
    }

    private void pauseTrack(){
        ((PlayTrackActivity) getActivity()).pauseTrack();
    }
    private void resumeTrack(){
        ((PlayTrackActivity) getActivity()).resumeTrack();
    }



}
