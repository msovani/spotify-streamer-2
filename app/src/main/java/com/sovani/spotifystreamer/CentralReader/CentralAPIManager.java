package com.sovani.spotifystreamer.CentralReader;

import android.content.Context;
import android.content.SharedPreferences;
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

    public static Tracks getTopTenTracks (String spotifyID, Context context)
    {

        Tracks tracks = null;
        try {
            Map<String, Object> query = new HashMap<>();



            //Try to get country code from preferences. If not, default to US.
            SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
            String countryCode = preferences.getString("COUNTRY_CODE", "US");

            query.put("country", countryCode);

            tracks = CentralAPIManager.getService().getArtistTopTrack(spotifyID, query);


        }catch (RetrofitError e) {
                Log.d("CentralAPIManager", e.toString());
            }
        return tracks;
    }





}
