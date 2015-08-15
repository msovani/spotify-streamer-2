package com.sovani.spotifystreamer.CentralReader;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * This is intendend to act as a central api manager and in future can be refactored as a singleton
 * to serve api requests from different areas of code.
 */
public class CentralAPIManager {
    private static  SpotifyService getService()
    {
        SpotifyApi api = new SpotifyApi();
        return api.getService();

    }
    public static ArtistsPager getArtistPager(String artistName)
    {
        ArtistsPager results = null;
        try {
             results = CentralAPIManager.getService().searchArtists(artistName);
        }
         catch (RetrofitError e) {
             Log.d("CentralAPIManager", e.toString());
        }
        return results;
    }

    public static Tracks getTopTenTracks (String spotifyID)
    {

        Tracks tracks = null;
        try {
            Map<String, Object> query = new HashMap<>();
            query.put("country", "us");

            tracks = CentralAPIManager.getService().getArtistTopTrack(spotifyID, query);
        }catch (RetrofitError e) {
                Log.d("CentralAPIManager", e.toString());
            }
        return tracks;
    }
}
