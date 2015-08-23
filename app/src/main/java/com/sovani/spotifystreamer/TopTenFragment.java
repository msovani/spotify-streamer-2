package com.sovani.spotifystreamer;


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
    private int selectedPos;
    private PlayTrackHandler playTrackHandler;
    private ListView trackListView;
    private TrackAdapter adapter;

    public PlayTrackHandler getPlayTrackHandler() {
        return playTrackHandler;
    }

    public void setPlayTrackHandler(PlayTrackHandler playTrackHandler) {
        this.playTrackHandler = playTrackHandler;
    }

    public void setTrackList(ArrayList<ParcelableTrack> trackList) {
        this.trackList = trackList;
    }

    public void HighLightTrack(int track)
    {
        selectedPos = track;
        trackListView.setItemChecked(track, true);
        if (adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
        trackListView.smoothScrollToPosition(track);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View fragmentView = inflater.inflate(R.layout.fragment_top_ten, container, false);

        trackListView = (ListView) fragmentView.findViewById(R.id.track_list);
        adapter = new TrackAdapter();
        trackListView.setAdapter(adapter);

        selectedPos = -1;
        if (savedInstance != null ) {
            trackList = savedInstance.getParcelableArrayList("TOP_TEN_RESULTS");
            selectedPos = savedInstance.getInt("SELECTED_POS", -1);
        }
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TOP10", "onItemClick " + position);
                if (playTrackHandler != null) {
                    playTrackHandler.onTrackSelected(trackList, position);
                }
            }
        });

        return fragmentView;
    }


    @Override
    public void onStart() {
        if (selectedPos>=0){
            trackListView.smoothScrollToPosition(selectedPos);
        }
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("TOP_TEN_RESULTS", trackList);
        outState.putInt("SELECTED_POS", selectedPos);

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

            if (position == selectedPos)
            {
                rootView.setActivated(true);
            }else {
                rootView.setActivated(false);
            }


            return rootView;
        }
    }

    public interface PlayTrackHandler {
        public void onTrackSelected(ArrayList<ParcelableTrack> trackList, int position);
    }

}
