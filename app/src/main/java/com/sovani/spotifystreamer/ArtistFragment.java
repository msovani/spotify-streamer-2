package com.sovani.spotifystreamer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.android.volley.Response;
import com.sovani.spotifystreamer.CentralReader.CentralAPIManager;
import com.sovani.spotifystreamer.CentralReader.RemoteDataManager;
import com.sovani.spotifystreamer.model.ParcelableArtist;
import com.sovani.spotifystreamer.model.ParcelableTrack;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class ArtistFragment extends Fragment  {

    private ArrayList<ParcelableArtist> artistList;
    private ArtistAdapter adapter;
    private TextView searchDisplay;
//    private RetrieveCentralFeed rcf;
    private RetrieveTracks rt;
    private ArrayList<ParcelableTrack> trackList;
    private TrackListSelectedResultsHandler trackListSelectedResultsHandler;
    private int selectedPos;
    private ListView artistListView;

    private TrackListSelectedResultsHandler getTrackListSelectedResultsHandler() {
        return trackListSelectedResultsHandler;
    }

    public void setTrackListSelectedResultsHandler(TrackListSelectedResultsHandler trackListSelectedResultsHandler) {
        this.trackListSelectedResultsHandler = trackListSelectedResultsHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View fragmentView = inflater.inflate(R.layout.fragment_artist, container, false);
        searchDisplay = (TextView) fragmentView.findViewById(R.id.search_message);

        selectedPos = -1;
        if ((savedInstance != null ) && (savedInstance.containsKey("ARTIST_LIST"))){
            artistList = savedInstance.getParcelableArrayList("ARTIST_LIST");
            selectedPos = savedInstance.getInt("SELECTED_POS", -1);
        }

        artistListView = (ListView) fragmentView.findViewById(R.id.artist_list);
        adapter = new ArtistAdapter();
        artistListView.setAdapter(adapter);
        artistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ParcelableArtist artist = adapter.getItem(position);
                Log.d("ClickHandler", adapter.getItem(position).toString());

                if (isOnline()) {
                    if (rt != null) {
                        if (rt.getStatus() == AsyncTask.Status.RUNNING) {
                            rt.cancel(true);
                        }
                    }
                    showTracks(ArtistFragment.this, artist.getArtistName());
//                    rt = new RetrieveTracks(ArtistFragment.this);
//                    rt.execute(artist);
                } else {
                    searchDisplay.setText(getResources().getText(R.string.error_no_network));
                    showToast(getResources().getText(R.string.error_no_network).toString());
                }
                view.setSelected(true);
                selectedPos = position;
                adapter.notifyDataSetChanged();

            }
        });
        RemoteDataManager.getArtists(getActivity(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                adapter.notifyDataSetChanged();
            }
        }, null);
        return fragmentView;
    }

    @Override
    public void onStart() {
        if (selectedPos>=0){
            artistListView.smoothScrollToPosition(selectedPos);
        }


        super.onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("ARTIST_LIST", artistList);
        outState.putInt("SELECTED_POS", selectedPos);

    }

//    public void getArtists(String artistName)
//    {
//
//        if (isOnline()) {
//            if ((artistName != null) && (artistName.length() > 0)) {
//
//                if (rcf != null) {
//                    if (rcf.getStatus() == AsyncTask.Status.RUNNING) {
//                        rcf.cancel(true);
//                    }
//                }
//
//                rcf = new RetrieveCentralFeed();
//
//                searchDisplay.setText(getResources().getText(R.string.message_searching));
//                rcf.execute(artistName);
//            }
//        }else{
//            searchDisplay.setText(getResources().getText(R.string.error_no_network));
//            showToast(getResources().getString(R.string.error_no_network));
//
//
//        }
//    }

    private class ArtistAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            int count = 0;
            if (RemoteDataManager.vedantaAlbums!= null)
            {
                count = RemoteDataManager.vedantaAlbums.size();
            }
            return count;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public ParcelableArtist getItem(int position) {
            return RemoteDataManager.vedantaAlbums.get(position);
        }


        @Override
        public long getItemId(int position) {
            return RemoteDataManager.vedantaAlbums.get(position).getId().hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.layout_album, null);
            TextView artisteName = (TextView) rootView.findViewById(R.id.frame_artist_name);

            ParcelableArtist artist = getItem(position);
            artisteName.setText(artist.getArtistName());

            ImageView albumCover = (ImageView) rootView.findViewById(R.id.frame_album_cover);

            if (artist.getUrl().length()>0) {

                Picasso.with(getActivity()).load(artist.getUrl()).placeholder(R.drawable.spotify_placeholder).into(albumCover);
            }else{
                Picasso.with(getActivity()).load(R.drawable.spotify_placeholder).into(albumCover);
            }

            if (position == selectedPos)
            {
                rootView.setActivated(true);
            }else{
                rootView.setActivated(false);
            }


            return rootView;
        }
    }

//    private class RetrieveCentralFeed extends AsyncTask<String, Void, ArtistsPager>
//    {
//        protected ArtistsPager doInBackground(String... params){
//            ArtistsPager pagers = null;
//            if (params.length >0) {
//                pagers = CentralAPIManager.getArtistPager(params[0]);
//            }
//            if(pagers != null) {
//                Log.d("MainScreen", pagers.toString());
//            }
//
//            return pagers;
//        }
//        protected void onPostExecute(ArtistsPager pager)
//        {
//            if (pager!= null) {
//                if (artistList == null)
//                {
//                    artistList = new ArrayList<>();
//                }
//                    artistList.clear();
//
//                for (Artist artist : pager.artists.items)
//                {
//                    String url;
//                    if (artist.images.size()>0)
//                    {
//                        Image image = artist.images.get(0);
//                        url = image.url;
//                    }else {
//                        url = "";
//                    }
//                    ParcelableArtist pArtist = new ParcelableArtist(artist.name, url, artist.id);
//                    artistList.add(pArtist);
//                }
//            }
//
//            if ((pager == null) || (pager.artists.items.size()==0))
//            {
//                searchDisplay.setText(getResources().getText(R.string.error_no_albums));
//
//            }else {
//                searchDisplay.setText("");
//            }
//
//            adapter.notifyDataSetChanged();
//            if (pager != null) {
//                Log.d("AsyncTask", pager.toString());
//            }
//        }
//    }
    public void showTracks (ArtistFragment fragment,String artist){
        trackList = RemoteDataManager.getTracks(artist);
        if ((trackList != null) && (trackList.size()>0))
        {
            //clear our old hit
            searchDisplay.setText("");

            //We have found tracks, hence we can now start the top10 activity
            if (fragment.getTrackListSelectedResultsHandler() != null) {
                fragment.getTrackListSelectedResultsHandler().onTrackListSelected(artist, trackList);
            }

        }
    }
    //This inner class checks tracks for selected artist.
    class RetrieveTracks extends AsyncTask<ParcelableArtist, Void, Tracks>
    {
        ParcelableArtist artist;
        public ArtistFragment fragment;

        public RetrieveTracks(ArtistFragment fragmentParam)
        {
            this.fragment = fragmentParam;
        }

        protected Tracks doInBackground(ParcelableArtist... params){
            Tracks tracks = null;
            if (params.length >0) {
                artist = params[0];
                tracks = CentralAPIManager.getTopTenTracks(artist.getId(), getActivity());
            }
            if(tracks != null) {
                Log.d("MainScreen", tracks.toString());
            }

            return tracks;
        }
        protected void onPostExecute(Tracks tracks)
        {
            if (tracks == null)
            {
                searchDisplay.setText(getResources().getText(R.string.error_no_tracks));
                showToast(getResources().getString(R.string.error_no_tracks));
                return;
            }

            if (tracks.tracks.size()==0)
            {
                //We do not have any tracks, we need to show message and exit.
                searchDisplay.setText(getResources().getText(R.string.error_no_tracks));
                showToast(getResources().getString(R.string.error_no_tracks));
                return;
            }

            if (trackList != null) {
                trackList.clear();
            }else{
                trackList = new ArrayList<>();
            }

            for (Track track : tracks.tracks) {
                String url = null;


                if (track.album.images.size()>0) {
                    Image image = track.album.images.get(0);
                    url = image.url;
                }

                 StringBuilder artist = new StringBuilder();
                 for (ArtistSimple artistName : track.artists)
                 {
                     if (artist.length()>0)
                         artist.append(", ");

                     artist.append(artistName.name);
                 }

                ParcelableTrack pTrack = new ParcelableTrack(track.album.name, track.name, url, track.id, track.preview_url, artist.toString());

                trackList.add(pTrack);
            }
            if (trackList.size()>0)
            {
                //clear our old hit
                searchDisplay.setText("");

                //We have found tracks, hence we can now start the top10 activity
                if (fragment.getTrackListSelectedResultsHandler() != null) {
                    fragment.getTrackListSelectedResultsHandler().onTrackListSelected(artist.getArtistName(), trackList);
                }

            }
        }
    }


    //    credits : http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showToast(String message)
    {
        Toast.makeText(getActivity().getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }

    //This is a callback handler interface through which this fragment speaks with the parent activity.
    public interface TrackListSelectedResultsHandler {
        public void onTrackListSelected(String artist, ArrayList<ParcelableTrack> listOfTracks);
    }

}
