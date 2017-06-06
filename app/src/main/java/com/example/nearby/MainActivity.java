package com.example.nearby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double mLatitude;
    private double mLongitude;
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String TYPE = "type";
    private GridLayout mGridLayout;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
        prefs.putBoolean(getString(R.string.pref_key), false);
        prefs.commit();
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        GridLayout grid = (GridLayout) findViewById(R.id.grid_layout);
        int childCount = grid.getChildCount();
        for (int i= 0; i < childCount; i++){
            final ImageButton button = (ImageButton) grid.getChildAt(i);
            button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                    prefs.putBoolean(getString(R.string.pref_key), false);
                    prefs.commit();
                    String type = "";
                    switch (button.getId()){
                        case R.id.restaurant : type = "restaurant";break;
                        case R.id.hospital : type = "hospital";break;
                        case R.id.post_office : type = "post_office";break;
                        case R.id.cafe : type = "cafe";break;
                        case R.id.atm : type = "atm";break;
                        case R.id.bank : type = "bank";break;
                        case R.id.clothing_store : type = "clothing_store";break;
                        case R.id.car_repair : type = "car_repair";break;
                    }
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class).putExtra(LATITUDE, mLatitude)
                            .putExtra(LONGITUDE, mLongitude).putExtra(TYPE, type);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else{
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location != null){
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
            }
        }
    }

    public String getText(double latitude, double longitude) {
        String x = "longitude : " + longitude + "\n latitude : " + latitude + "\n";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        x += "city name : " + addresses.get(0).getAddressLine(0) + "\n";
        x += "state name : " + addresses.get(0).getAddressLine(1) + "\n";
        x += "country name : " + addresses.get(0).getAddressLine(2);
        return x;
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        mLongitude = location.getLongitude();
        mLatitude = location.getLatitude();
    }
}
