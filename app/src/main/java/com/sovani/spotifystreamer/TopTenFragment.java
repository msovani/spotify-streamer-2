package com.sovani.spotifystreamer;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sovani.spotifystreamer.model.ParcelableTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;




public class TopTenFragment extends Fragment {

    private ArrayList<ParcelableTrack> trackList;

    public void setTrackList(ArrayList<ParcelableTrack> trackList) {
        this.trackList = trackList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View fragmentView = inflater.inflate(R.layout.fragment_top_ten, container, false);

        ListView trackListView = (ListView) fragmentView.findViewById(R.id.track_list);
        TrackAdapter adapter = new TrackAdapter();
        trackListView.setAdapter(adapter);

        if (savedInstance != null ) {
            trackList = savedInstance.getParcelableArrayList("TOP_TEN_RESULTS");
        }
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TOP10", "onItemClick " + position);
                ParcelableTrack track = trackList.get(position);
                //User has clicked on a track, and we need to play the track
                Intent trackIntent = new Intent(getActivity(), PlayTrackActivity.class);
                trackIntent.putExtra("ARTIST_NAME", track.getArtists());
                trackIntent.putParcelableArrayListExtra("TRACK_LIST", trackList);
                trackIntent.putExtra("TRACK_POSITION", position);
                getActivity().startActivity(trackIntent);
            }
        });

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("TOP_TEN_RESULTS", trackList);

    }



    private class TrackAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            int count = 0;
            if (trackList!= null)
            {
                count = trackList.size();
            }
            return count;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public ParcelableTrack getItem(int position) {
            return trackList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return trackList.get(position).getId().hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.layout_track, null);
            TextView artisteName = (TextView) rootView.findViewById(R.id.frame_artist_name);
            TextView trackName = (TextView) rootView.findViewById(R.id.frame_track_name);

            ParcelableTrack track = getItem(position);
            artisteName.setText(track.getAlbumName());
            trackName.setText(track.getTrackName());

            ImageView albumCover = (ImageView) rootView.findViewById(R.id.frame_album_cover);

            if (track.getUrl().length()>0) {
                String url = track.getUrl();

                Picasso.with(getActivity()).load(url).placeholder(R.drawable.spotify_placeholder).into(albumCover);
            }else{
                Picasso.with(getActivity()).load(getResources().getResourceName(R.drawable.spotify_placeholder));
            }



            return rootView;
        }
    }



}
