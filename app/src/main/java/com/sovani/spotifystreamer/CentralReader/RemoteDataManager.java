package com.sovani.spotifystreamer.CentralReader;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sovani.spotifystreamer.model.ParcelableArtist;
import com.sovani.spotifystreamer.model.ParcelableTrack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by msovani on 3/27/16.
 */
public class RemoteDataManager {
    //Volley Queues
    private static RequestQueue queue;

    private static String ALBUM_URL = "http://www.vedantausa.org/app/config/products.json";
    public static ArrayList<ParcelableArtist> vedantaAlbums;
    private static HashMap<String, ArrayList<ParcelableTrack>>albumTracks;

    public static RequestQueue sharedManager (Context context)
    {
        if (queue == null)
        {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }

    public static ArrayList<ParcelableTrack> getTracks (String album)
    {
        if (albumTracks.containsKey(album))
            return albumTracks.get(album);
        else
            return null;
    }

    public static void getArtists(Context context, final Response.Listener<JSONObject> successCallBack, final Response.ErrorListener failureCallBack){

        if (vedantaAlbums != null)
            vedantaAlbums.clear();
        else
            vedantaAlbums = new ArrayList<>();

        if (albumTracks != null)
        {
            albumTracks.clear();
        }else{
            albumTracks = new HashMap<>();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ALBUM_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jObject) {
                        Log.d("RemoteData", jObject.toString());

                        Iterator<?> keys = jObject.keys();
                        try {
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                if (jObject.get(key) instanceof JSONArray) {
                                    JSONArray album = jObject.getJSONArray(key);

                                    for (int i=0; i < album.length(); i++) {
                                        JSONObject jsonObject = album.getJSONObject(i);
                                        ParcelableArtist vedantaAlbum =
                                                new ParcelableArtist(jsonObject.getString("title"),
                                                        jsonObject.getString("image_url"),
                                                        jsonObject.getString("title")
                                                        );
                                        ArrayList<ParcelableTrack> trackList = new ArrayList<>();
                                        if (jsonObject.has("tracks"))
                                        {
                                            JSONArray tracks = jsonObject.getJSONArray("tracks");
                                            for (int j=0; j < tracks.length(); j++)
                                            {
                                                JSONObject trackObject = tracks.getJSONObject(j);
                                                ParcelableTrack pTrack = new ParcelableTrack(jsonObject.getString("title"),
                                                        trackObject.getString("track_name"),
                                                        jsonObject.getString("image_url"),
                                                        trackObject.getString("track_name"),
                                                        trackObject.getString("track_url"),
                                                        trackObject.getString("track_name"));
                                                trackList.add(pTrack);
                                            }
                                            albumTracks.put(jsonObject.getString("title"), trackList);
                                        }else {
                                            ParcelableTrack pTrack = new ParcelableTrack(jsonObject.getString("title"),
                                                    jsonObject.getString("title"),
                                                    jsonObject.getString("image_url"),
                                                    jsonObject.getString("title"),
                                                    "http://vedantausa.org/app/media/gita1.mp3",
                                                    jsonObject.getString("title"));
                                            trackList.add(pTrack);
                                            albumTracks.put(jsonObject.getString("title"), trackList);
                                        }
                                        RemoteDataManager.vedantaAlbums.add(vedantaAlbum);
                                    }
                                }
                            }
                            Log.d("RemoteData", RemoteDataManager.vedantaAlbums.toString());
                            if (successCallBack != null) {
                                successCallBack.onResponse(jObject);
                            }
                        }catch (JSONException je)
                        {
                            Log.e("RemoteData", je.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RemoteDataManager.sharedManager(context).add(jsonObjectRequest);
    }
}
