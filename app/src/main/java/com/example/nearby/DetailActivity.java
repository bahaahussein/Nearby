package com.example.nearby;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nearby.data.PlacesContract;
import com.example.nearby.data.PlacesDb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class DetailActivity extends AppCompatActivity {

    private ListView mListView;
    private ListElementAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mListView = (ListView)findViewById(R.id.detail_list_view);
        mAdapter = new ListElementAdapter(this, new ArrayList<Item>());
        mListView.setAdapter(mAdapter);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getResources().getString(R.string.pref_key), false)){
            new FetchFavorite().execute();
        }else{
            Bundle b = getIntent().getExtras();
            String type = b.getString(MainActivity.TYPE);
            String latitude = b.getDouble(MainActivity.LATITUDE)+"";
            String longitude = b.getDouble(MainActivity.LONGITUDE)+"";
            new FetchPlaces().execute(latitude, longitude, type);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.settings_item){
            //start settings activity
            Intent intent = new Intent(DetailActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private class FetchPlaces extends AsyncTask<String, Void, Item[]>{

        private String LOG_TAG = FetchPlaces.class.getSimpleName();

        @Override
        protected Item[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String forecastJsonStr = null;
                try{

                    URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+params[0]+","+params[1]
                            + "&radius=500&type="+params[2]+"&key="+BuildConfig.GOOGLE_PLACES_API_KEY);


                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    forecastJsonStr = buffer.toString();

                    Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);

                }catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                }finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    JSONArray jsonArray = new JSONObject(forecastJsonStr).getJSONArray("results");
                Log.i(LOG_TAG, "jsonArray length : "+jsonArray.length());
                Item[] x = new Item[jsonArray.length()];
                for (int i=0; i<jsonArray.length(); i++){
                    String name = "null";
                    String address = "null";
                    int price = -1;
                    double rating = -1;
                    String open = "null";
                    String photo = "null";
                    double latitude = -1, longitude = -1;

                    String id = jsonArray.getJSONObject(i).getString("place_id");

                    if(!jsonArray.getJSONObject(i).isNull("geometry") &&
                            !jsonArray.getJSONObject(i).getJSONObject("geometry").isNull("location")){
                        latitude = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        longitude = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    }

                    if(!jsonArray.getJSONObject(i).isNull("name"))
                        name = jsonArray.getJSONObject(i).getString("name");

                    if(!jsonArray.getJSONObject(i).isNull("vicinity"))
                        address = jsonArray.getJSONObject(i).getString("vicinity");

                    if(!jsonArray.getJSONObject(i).isNull("price_level"))
                        price = jsonArray.getJSONObject(i).getInt("price_level");

                    if(!jsonArray.getJSONObject(i).isNull("opening_hours") &&
                            !jsonArray.getJSONObject(i).getJSONObject("opening_hours").isNull("open_now"))
                        open = String.valueOf(jsonArray.getJSONObject(i).getJSONObject("opening_hours").getBoolean("open_now"));

                    if(!jsonArray.getJSONObject(i).isNull("rating"))
                        rating = jsonArray.getJSONObject(i).getDouble("rating");

                    if(!jsonArray.getJSONObject(i).isNull("photos") &&
                            jsonArray.getJSONObject(i).getJSONArray("photos").length()>0){
                        JSONObject obj = jsonArray.getJSONObject(i).getJSONArray("photos").getJSONObject(0);
                        String ref = obj.getString("photo_reference");
                        int height = obj.getInt("height");
                        photo = "https://maps.googleapis.com/maps/api/place/photo?maxheight="
                                +height+"&photoreference="+ref+"&key="+BuildConfig.GOOGLE_PLACES_API_KEY;
                    }
                    Item item = new Item(photo, name, address, price, open, rating, latitude, longitude, id, params[2]);
                    Log.i(LOG_TAG, item.toString());
                    x[i] = item;
                }
                return x;
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Item[] items) {
            if(items!=null){
                mAdapter.addAll(items);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FetchFavorite extends AsyncTask<Void, Void, ArrayList<Item>>{

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            PlacesDb mDbHelper = new PlacesDb(getBaseContext());
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor cursor = db.query(PlacesContract.FeedEntry.TABLE_NAME,
                    null, null, null, null, null, null, null);
            ArrayList<Item> items = new ArrayList<Item>();
            while (cursor.moveToNext()){
                String photo = cursor.getString(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry.COLUMN_PHOTO));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry.COLUMN_NAME));
                String id = cursor.getString(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry._ID));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry.COLUMN_ADDRESS));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry.COLUMN_PRICE_LEVEL));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry.COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry.COLUMN_LONGITUDE));
                double rating = cursor.getDouble(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry.COLUMN_RATING));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(PlacesContract.FeedEntry.COLUMN_TYPE));
                items.add(new Item(photo, name, address, price, "null", rating, latitude, longitude, id, type));
            }
            cursor.close();
            mDbHelper.close();
            return items;
        }

        @Override
        protected void onPostExecute(ArrayList<Item> items) {
            if (items!=null){
                mAdapter.clear();
                mAdapter.addAll(items);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
