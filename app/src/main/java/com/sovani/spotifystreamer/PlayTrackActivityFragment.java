package com.sovani.spotifystreamer;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sovani.spotifystreamer.model.ParcelableTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayTrackActivityFragment extends Fragment {


    private ArrayList<ParcelableTrack> trackList;
    private int position;

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
        if ( (trackList != null) && (trackList.size()>position)) {
            ParcelableTrack track = trackList.get(position);

            ImageView albumCover = (ImageView) rootView.findViewById(R.id.image_track);

            if (track.getUrl().length() > 0) {
                String url = track.getUrl();

                Picasso.with(getActivity()).load(url).placeholder(R.drawable.spotify_placeholder).into(albumCover);
            } else {
                Picasso.with(getActivity()).load(getResources().getResourceName(R.drawable.spotify_placeholder));
            }
        }

        return rootView;
    }


}
