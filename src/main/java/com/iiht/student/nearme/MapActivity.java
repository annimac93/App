package com.iiht.student.nearme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.iiht.student.nearme.R.id.recycle_view;

public class MapActivity extends MenuActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    LocationManager locationManager;
    boolean GpsStatus;
    String myJSON;
    String userid;
    RecyclerView recyclerView;
    String photores = null;
    PlacesAdapter placeadpt;
    private ArrayList<String> place_image = new ArrayList<String>();
    private ArrayList<String> place_name = new ArrayList<String>();
    private ArrayList<String> place_vicinity = new ArrayList<String>();
    private ArrayList<String> place_lat = new ArrayList<String>();
    private ArrayList<String> place_lng = new ArrayList<String>();
    //ListView placelist;

    JSONArray results = null;

    JSONArray photos = null;
    //    Context context;
    private AlertDialog.Builder build;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(com.iiht.student.nearme.R.layout.activity_map, content);
        setTitle("Foodz Near Me");
        // placelist = (ListView)findViewById(com.iiht.student.nearme.R.id.listview_place);
        recyclerView = (RecyclerView) findViewById(recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setUpGClient();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.iiht.student.nearme.R.id.map1);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        userid = i.getStringExtra("userid");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


    }

    private void setUpGClient() {
//  mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, 0, this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();


//        GoogleApiClient googleApiClient = null;
//        if (mGoogleApiClient == null) {
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.iiht.student.nearme.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                System.exit(0);
            } else {
                Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
            }

        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getApplicationContext(),"in first if",Toast.LENGTH_LONG).show();
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                Toast.makeText(getContext(),"in second if",Toast.LENGTH_LONG).show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
//                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));


            } else {
//                Toast.makeText(getContext(),"in second else",Toast.LENGTH_LONG).show();
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
//                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
            return false;
        } else {
//            Toast.makeText(getApplicationContext(),"in first else",Toast.LENGTH_SHORT).show();
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


//            Toast.makeText(getContext(),"gps status :"+String.valueOf(GpsStatus),Toast.LENGTH_SHORT).show();
//            if(!GpsStatus)
//            {
//                build = new AlertDialog.Builder(this);
//                build.setMessage("To continue, let your device turn on location using Google's location service.");
//                build.setPositiveButton("Ok",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
////                                String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
////
////                                if(!provider.contains("gps")){ //if gps is disabled
////                                    final Intent poke = new Intent();
////                                    poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
////                                    poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
////                                    poke.setData(Uri.parse("3"));
////                                    sendBroadcast(poke);
////                                }
//                                dialog.cancel();
//                            }
//                        });
//
//                build.setNegativeButton("Cancel",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int which) {
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alert = build.create();
//                alert.show();
//            }
            return true;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=1000&type=restaurant,bar,cafe&name=restaurant&key=AIzaSyClvmT1pUito9fm7azRO-fgfa8Jyo00qMc";

//        Log.d("$$$$$$url",url);
//        Toast.makeText(getContext(),url,Toast.LENGTH_LONG).show();
//        myJSON = getData(url);
        new ShowData().execute(url);


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
//               checkLocationPermission();
                // startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
//                            toast("Success");
//                        Toast.makeText(getApplicationContext(),"Success" ,Toast.LENGTH_LONG).show();
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                            toast("GPS is not on");
//                        Toast.makeText(getApplicationContext(),"Gps is not on" ,Toast.LENGTH_LONG).show();
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapActivity.this, 1000);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                            toast("Setting change not allowed");
//                        Toast.makeText(getApplicationContext(),"Setting change not allowed" ,Toast.LENGTH_LONG).show();
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
//
    }


    public void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            String status = jsonObj.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                results = jsonObj.getJSONArray("results");
                int len = results.length();
                for (int i = 0; i < len; i++) {
                    JSONObject c = results.getJSONObject(i);

                    String name = c.getString("name");
                    // Toast.makeText(getApplicationContext(),"name : "+name,Toast.LENGTH_LONG).show();
                    String vicinity = c.getString("vicinity");

                    JSONObject geo = c.getJSONObject("geometry");

                    JSONObject location = geo.getJSONObject("location");

                    Double lat = Double.valueOf(location.getString("lat"));

                    Double lng = Double.valueOf(location.getString("lng"));
//                    photos = c.getJSONArray("photos");
                    photos = c.optJSONArray("photos");
                    if (photos == null) {
                        //Toast.makeText(getApplicationContext(),"photo not found : ",Toast.LENGTH_LONG).show();
                        photores = "noimage";
                        place_image.add(photores);
                    } else {
                        for (int j = 0; j < photos.length(); j++) {
                            JSONObject d = photos.optJSONObject(j);
                            if(d == null){
                                photores = "noimage";
                                place_image.add(photores);
                                //Toast.makeText(getApplicationContext(),"photo not found : ",Toast.LENGTH_LONG).show();
                            }else{
                                photores = d.getString("photo_reference");
                                //Toast.makeText(getApplicationContext(),photores,Toast.LENGTH_LONG).show()
                            }
                            place_image.add(photores);
                        }
                    }
                    place_name.add(name);
                    place_vicinity.add(vicinity);
                    place_lat.add(String.valueOf(lat));
                    place_lng.add(String.valueOf(lng));

//                Toast.makeText(getContext(),String.valueOf(place_name) ,Toast.LENGTH_LONG).show();
//                Toast.makeText(getContext(),lat + " ,"+lng,Toast.LENGTH_LONG).show();

                    //setting marker
                    LatLng latLng = new LatLng(lat, lng);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(name);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);

//                    placeadpt.notifyDataSetChanged();
                    // placeadpt = new PlaceAdapter(MapActivity.this, place_image, place_name, "map");

                }
                placeadpt = new PlacesAdapter(MapActivity.this, place_image, place_name,place_vicinity,place_lat,place_lng,userid, "map");
                recyclerView.setAdapter(placeadpt);
//                    placelist.setScrollingCacheEnabled(false);


//                    placelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                            Intent placeintent = new Intent(MapActivity.this,PlaceActivity.class);
//                            placeintent.putExtra("place_name",place_name.get(position));
//                            placeintent.putExtra("place_image",place_image.get(position));
//                            placeintent.putExtra("place_vicinity",place_vicinity.get(position));
//                            placeintent.putExtra("place_lat",place_lat.get(position));
//                            placeintent.putExtra("place_lng",place_lng.get(position));
//                            placeintent.putExtra("userid",userid);
//                            startActivity(placeintent);
//                        }
//                    });

            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
//        Toast.makeText(getContext(),"req code :"+String.valueOf(requestCode),Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(getApplicationContext(),"In first if",Toast.LENGTH_SHORT).show();
                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);


//                        Toast.makeText(getContext(),"gps status :"+String.valueOf(GpsStatus),Toast.LENGTH_SHORT).show();
//                        if(!GpsStatus)
//                        {
//                            build = new AlertDialog.Builder(MapActivity.this);
//                            build.setMessage("To continue, let your device turn on location using Google's location service.");
//                            build.setPositiveButton("Ok",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                                            dialog.dismiss();
//                                        }
//                                    });
//
//                            build.setNegativeButton("Cancel",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog,int which) {
//                                            dialog.cancel();
//                                        }
//                                    });
//                            AlertDialog alert = build.create();
//                            alert.show();
//                        }


                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    class ShowData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String[] objects) {
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(objects[0]);


//        Toast.makeText(getContext(),"in getdata",Toast.LENGTH_LONG).show();
            try {

                HttpResponse response = client.execute(httpget);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 200) //status ok
                {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        str.append(line);
                    }
                } else {
                    Log.e("Log", "Failed to download result...");
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//    	Log.d("result",str.toString());
            return str.toString();
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
//            Log.w("@@@@@@@@",o);
            myJSON = o;
            showList();
        }
    }
}
