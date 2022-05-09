package com.myproject.ad340;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback  {
    public GoogleMap mMap;
    public ArrayList<MapCamera> cameraLocations = new ArrayList<>();
    public ArrayList<LatLng> locationArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        //adding Latlng array



        //getting array of camera info
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String cameraApiURL = "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, cameraApiURL, null,
                    response -> {
                        try {
                            JSONArray featuresArray = response.getJSONArray("Features");
                            for (int i = 0; i < featuresArray.length(); i++) {
                                JSONObject feature = featuresArray.getJSONObject(i);
                                JSONArray pointPositions = feature.getJSONArray("PointCoordinate");

                                    double latitude = pointPositions.getDouble(0);
                                    double longitude = pointPositions.getDouble(1);
                                    Double[] coordinates = {latitude, longitude};
                                    LatLng sydney = new LatLng(latitude, longitude);
                                    locationArrayList.add(sydney);
                                    cameraLocations.add(new MapCamera(coordinates));


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.d("JSON", "Error: " + error.getMessage()));
            queue.add(objectRequest);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap ) {

        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-20, 151);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }
}
