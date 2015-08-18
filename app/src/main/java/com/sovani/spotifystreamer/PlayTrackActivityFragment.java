package com.sovani.spotifystreamer;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private ImageView albumCover;

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
                if (isPlaying)
                {
                    isPlaying = false;
                }else {
                    isPlaying = true;
                    playTrack();
                }
            }
        });



        if ( (trackList != null) && (trackList.size()>position)) {
             albumCover = (ImageView) rootView.findViewById(R.id.image_track);


            gotoTrack(position);
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

    }

}
