package com.sovani.spotifystreamer;

import android.content.Context;
import android.content.Intent;
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

import com.sovani.spotifystreamer.CentralReader.CentralAPIManager;
import com.sovani.spotifystreamer.model.ParcelableArtist;
import com.sovani.spotifystreamer.model.ParcelableTrack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


public class ArtistFragment extends Fragment  {

    private ArrayList<ParcelableArtist> artistList;
    private ArtistAdapter adapter;
    private TextView searchDisplay;
    private RetrieveCentralFeed rcf;
    private RetrieveTracks rt;
    private ArrayList<ParcelableTrack> trackList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
        View fragmentView = inflater.inflate(R.layout.fragment_artist, container, false);
        searchDisplay = (TextView) fragmentView.findViewById(R.id.search_message);

        if ((savedInstance != null ) && (savedInstance.containsKey("ARTIST_LIST"))){
            artistList = savedInstance.getParcelableArrayList("ARTIST_LIST");
        }

        ListView artistListView = (ListView) fragmentView.findViewById(R.id.artist_list);
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
                    rt = new RetrieveTracks();
                    rt.execute(artist);
                }else{
                    searchDisplay.setText(getResources().getText(R.string.error_no_network));
                    showToast(getResources().getText(R.string.error_no_network).toString());
                }

            }
        });

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("ARTIST_LIST", artistList);

    }

    public void getArtists(String artistName)
    {

        if (isOnline()) {
            if ((artistName != null) && (artistName.length() > 0)) {

                if (rcf != null) {
                    if (rcf.getStatus() == AsyncTask.Status.RUNNING) {
                        rcf.cancel(true);
                    }
                }

                rcf = new RetrieveCentralFeed();

                searchDisplay.setText(getResources().getText(R.string.message_searching));
                rcf.execute(artistName);
            }
        }else{
            searchDisplay.setText(getResources().getText(R.string.error_no_network));
            showToast(getResources().getString(R.string.error_no_network));


        }
    }

    private class ArtistAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            int count = 0;
            if (artistList!= null)
            {
                count = artistList.size();
            }
            return count;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public ParcelableArtist getItem(int position) {
            return artistList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return artistList.get(position).getId().hashCode();
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



            return rootView;
        }
    }

    private class RetrieveCentralFeed extends AsyncTask<String, Void, ArtistsPager>
    {
        protected ArtistsPager doInBackground(String... params){
            ArtistsPager pagers = null;
            if (params.length >0) {
                pagers = CentralAPIManager.getArtistPager(params[0]);
            }
            if(pagers != null) {
                Log.d("MainScreen", pagers.toString());
            }

            return pagers;
        }
        protected void onPostExecute(ArtistsPager pager)
        {
            if (pager!= null) {
                if (artistList == null)
                {
                    artistList = new ArrayList<>();
                }
                    artistList.clear();

                for (Artist artist : pager.artists.items)
                {
                    String url;
                    if (artist.images.size()>0)
                    {
                        Image image = artist.images.get(0);
                        url = image.url;
                    }else {
                        url = "";
                    }
                    ParcelableArtist pArtist = new ParcelableArtist(artist.name, url, artist.id);
                    artistList.add(pArtist);
                }
            }

            if ((pager == null) || (pager.artists.items.size()==0))
            {
                searchDisplay.setText(getResources().getText(R.string.error_no_albums));

            }else {
                searchDisplay.setText("");
            }

            adapter.notifyDataSetChanged();
            if (pager != null) {
                Log.d("AsyncTask", pager.toString());
            }
        }
    }

    //This inner class checks tracks for selected artist.
    class RetrieveTracks extends AsyncTask<ParcelableArtist, Void, Tracks>
    {
        ParcelableArtist artist;
        protected Tracks doInBackground(ParcelableArtist... params){
            Tracks tracks = null;
            if (params.length >0) {
                artist = params[0];
                tracks = CentralAPIManager.getTopTenTracks(artist.getId());
            }
            if(tracks != null) {
                Log.d("MainScreen", tracks.toString());
            }

            return tracks;
        }
        protected void onPostExecute(Tracks tracks)
        {
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

                ParcelableTrack pTrack = new ParcelableTrack(track.album.name, track.name, url, track.id, track.preview_url);

                trackList.add(pTrack);
            }
            if (trackList.size()>0)
            {
                //clear our old hit
                searchDisplay.setText("");

                //We have found tracks, hence we can now start the top10 activity
                Intent topTenIntent = new Intent(getActivity(), TopTenActivity.class);
                topTenIntent.putExtra("ARTIST_NAME", artist.getArtistName());
                topTenIntent.putParcelableArrayListExtra("TRACK_LIST", trackList);
                getActivity().startActivity(topTenIntent);

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


}
