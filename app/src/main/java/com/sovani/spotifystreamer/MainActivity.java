package com.sovani.spotifystreamer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class MainActivity extends AppCompatActivity {

    private EditText artistName;
    private ArtistFragment artistFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        artistName = (EditText) findViewById(R.id.artist_name);
        if (savedInstanceState==null)
        {

            artistFragment = new ArtistFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, artistFragment, "ARTIST_FRAGMENT_TAG")
                    .commit();

        }else{

            artistFragment = (ArtistFragment) getSupportFragmentManager().findFragmentByTag("ARTIST_FRAGMENT_TAG");

            String searchTerm = savedInstanceState.getString("SEARCH_TERM");
            if (searchTerm != null) {
                artistName.setText(searchTerm);
            }

        }



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SEARCH_TERM", artistName.getText().toString());
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


}
