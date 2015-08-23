package com.sovani.spotifystreamer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.sovani.spotifystreamer.CentralReader.CentralAPIManager;
import com.sovani.spotifystreamer.model.ParcelableTrack;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ArtistFragment.TrackListSelectedResultsHandler, TopTenFragment.PlayTrackHandler, PlayTrackActivityFragment.TrackServiceBridgeCommander {

    private EditText artistName;
    private ArtistFragment artistFragment;
    private boolean mTabletMode;
    private TopTenFragment topTenFragment;

    private ArrayList<ParcelableTrack> trackList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artistName = (EditText) findViewById(R.id.artist_name);

        mTabletMode = findViewById(R.id.dynamic_fragment_container) != null;

        //Create the Music Service here
        CentralAPIManager.getInstance().getMaudioPlayBackService(getApplicationContext());

        if (savedInstanceState==null)
        {

            artistFragment = new ArtistFragment();
            artistFragment.setTrackListSelectedResultsHandler(this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, artistFragment, "ARTIST_FRAGMENT_TAG")
                    .commit();

        }else{

            trackList = savedInstanceState.getParcelableArrayList("TOP_TEN_LIST");
            position = savedInstanceState.getInt("TOP_TEN_POSITION");


            artistFragment = (ArtistFragment) getSupportFragmentManager().findFragmentByTag("ARTIST_FRAGMENT_TAG");
            artistFragment.setTrackListSelectedResultsHandler(this);

            String searchTerm = savedInstanceState.getString("SEARCH_TERM");
            if (searchTerm != null) {
                artistName.setText(searchTerm);
            }

            TopTenFragment topTenFragment  = (TopTenFragment) getSupportFragmentManager().findFragmentByTag("TOP_TEN_FRAGMENT_TAG");
            if (topTenFragment != null)
            {
                if (trackList != null) {
                    topTenFragment.setTrackList(trackList);
                }
                topTenFragment.setPlayTrackHandler(this);
            }

        }



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SEARCH_TERM", artistName.getText().toString());
        if (trackList != null) {
            outState.putParcelableArrayList("TOP_TEN_LIST", trackList);
        }
        outState.putInt("TOP_TEN_POSITION", position);
    }



    @Override
    protected void onResume()
    {
        super.onResume();

        artistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                artistFragment.getArtists(s.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("MainActivity", "onOptionsItemSelected ");
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_select_country);
                Object[] sArray = {"US - USA", "GB - England", "ES - Spain", "FR - France"};
                final ArrayAdapter adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sArray);
                builder.setAdapter(adp, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedCountryString = (String) adp.getItem(which);
                        Log.d("Pref", "onClick " + adp.getItem(which));

                        String[] countryParts = selectedCountryString.split(" ");

                        if (countryParts.length >0) {
                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("COUNTRY_CODE", countryParts[0]);
                            editor.apply();
                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrackListSelected(String artist, ArrayList<ParcelableTrack> listOfTracks) {

        if (!mTabletMode) {
            Intent topTenIntent = new Intent(MainActivity.this, TopTenActivity.class);
            topTenIntent.putExtra("ARTIST_NAME", artist);
            topTenIntent.putParcelableArrayListExtra("TRACK_LIST", listOfTracks);
            MainActivity.this.startActivity(topTenIntent);
        }else{
            topTenFragment = new TopTenFragment();


            if (listOfTracks != null) {
                topTenFragment.setTrackList(listOfTracks);
            }
            topTenFragment.setPlayTrackHandler(this);
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.dynamic_fragment_container, topTenFragment, "TOP_TEN_FRAGMENT_TAG").commit();

        }
    }

    @Override
    public void onTrackSelected(ArrayList<ParcelableTrack> trackListParam, int positionParam) {

        trackList = trackListParam;
        position = positionParam;

        ParcelableTrack track = trackList.get(position);

        CentralAPIManager.getInstance().getMaudioPlayBackService(getApplicationContext()).setTracks(track.getPreviewURL());

        // Create the fragment and show it as a dialog.
        PlayTrackActivityFragment newFragment = PlayTrackActivityFragment.newInstance();
        newFragment.setTrackList(trackList, position);
        newFragment.setTrackServiceBridgeCommander(this);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public ArrayList<ParcelableTrack> getTracks() {
        return trackList;
    }

    @Override
    public void setPosition(int pos) {
        position = pos;

        topTenFragment = (TopTenFragment) getSupportFragmentManager().findFragmentByTag("TOP_TEN_FRAGMENT_TAG");
        if (topTenFragment != null)
        {
            topTenFragment.HighLightTrack(pos);
        }

    }
}
