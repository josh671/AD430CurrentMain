package com.myproject.ad340;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CameraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        ArrayList<Camera> cameraList = new ArrayList<>();

        RecyclerView cameraListRecyclerView = findViewById(R.id.cameraListRecyclerView);
        CameraAdapter cameraAdapter = new CameraAdapter(cameraList);
        cameraListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cameraListRecyclerView.setAdapter(cameraAdapter);
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
                                JSONArray camerasArray = feature.getJSONArray("Cameras");
                                for (int j = 0; j < camerasArray.length(); j++) {
                                    JSONObject camera = camerasArray.getJSONObject(j);
                                    String camDescription = camera.getString("Description");
                                    String camImageURL = camera.getString("ImageUrl");
                                    String camType = camera.getString("Type");
                                    if (camType.equals("sdot"))
                                        camImageURL = "https://www.seattle.gov/trafficcams/images/" + camImageURL;
                                    else {
                                        camImageURL = "https://images.wsdot.wa.gov/nw/" + camImageURL;
                                    }
                                    cameraList.add(new Camera(camDescription, camImageURL));
                                }
                            }
                            cameraAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.d("JSON", "Error: " + error.getMessage()));
            queue.add(objectRequest);
        } else {
            Toast.makeText(CameraActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
}