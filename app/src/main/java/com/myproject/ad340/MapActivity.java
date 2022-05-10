package com.myproject.ad340;


import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Location lastKnownLocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String Tag = "TAG_TEST";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defLocation = new LatLng(47.69907063241689, -122.33263951586274);
    public GoogleMap googleMap;
    private boolean locationPermissionGranted;
    //adding location
    FusedLocationProviderClient client;
    private LatLng defaultLocation =  new LatLng(47.69907063241689, -122.33263951586274);


    public ArrayList<Camera> cameraLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        //adding Latlng array
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //instantiates map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }
    //sequencing
        //load map
        //detect user location
        //put marker on map
        //fire request to get locations
    //get permission before update locationUI get location


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap ) {
        this.googleMap = googleMap;


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(47.69907063241689,-122.33263951586274 ), 12));
        getLocationPermission();
        getDeviceLocation();

        getCamData();
    }

    public void getCamData(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String cameraApiURL = "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, cameraApiURL, null,
                    response -> {
                        try {
                            JSONArray features = response.getJSONArray("Features");
                            for (int i = 0; i < features.length(); i++) {
                                JSONObject point = features.getJSONObject(i);
                                JSONArray pointCoordinates = point.getJSONArray("PointCoordinate");
                                Double[] coordinates = new Double[2];

                                for(int k = 0; k < pointCoordinates.length(); k++){
                                    coordinates[k] = pointCoordinates.getDouble(k);
                                }

                                JSONArray camerasArray = point.getJSONArray("Cameras");

                                for (int j = 0; j < camerasArray.length(); j++) {
                                    JSONObject camera = camerasArray.getJSONObject(j);
                                    Camera cam = new Camera();
                                    cam.setDescription(camera.getString("Description"));



                                    String camImageURL = camera.getString("ImageUrl");
                                    String camType = camera.getString("Type");
                                    if (camType.equals("sdot"))
                                        cam.setImageURL( "https://www.seattle.gov/trafficcams/images/" + camImageURL);
                                    else {
                                        cam.setImageURL( "https://images.wsdot.wa.gov/nw/" + camImageURL);
                                    }

                                    cam.setCoordinates(coordinates);
                                    cameraLocations.add( cam);
                                }

                            }
                            //show the camera locations
                            Log.d("camCount", String.valueOf(cameraLocations));
                            showMarkers();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.d("JSON", "Error: " + error.getMessage()));
            queue.add(objectRequest);
        } else {
            Toast.makeText(MapActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void showMarkers(){
        for(Camera cam : cameraLocations){
            Double[] latLong = cam.getCoordinates();
            double lat = latLong[0];
            double lng = latLong[1];
            googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(cam.getDescription())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getDeviceLocation() {
        Log.d("location3", "device location");
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Log.d("location", "location Permission granted");
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Log.d("location", "on complete");
                        if (task.isSuccessful()) {

                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            Log.d("location", lastKnownLocation.toString());
                            if (lastKnownLocation != null) {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 15));
                                Log.d("location", lastKnownLocation.toString());
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            googleMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom( defaultLocation , 15));
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

}
