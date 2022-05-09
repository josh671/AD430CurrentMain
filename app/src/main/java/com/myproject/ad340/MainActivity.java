package com.myproject.ad340;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//check

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button movies = findViewById(R.id.movies);
        Button cities = findViewById(R.id.cities);
        Button parks = findViewById(R.id.parks);
        Button music = findViewById(R.id.music);
        Button traffic = findViewById(R.id.traffic);
        Button food = findViewById(R.id.map);


        cities.setOnClickListener(this);
        parks.setOnClickListener(this);
        music.setOnClickListener(this);
        food.setOnClickListener(this);
        movies.setOnClickListener(this);
        traffic.setOnClickListener(this);



    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cities:
                Toast.makeText(this, "Cities", Toast.LENGTH_SHORT).show();
                break;
            case R.id.parks:
                Toast.makeText(this, "Parks", Toast.LENGTH_SHORT).show();
                break;
            case R.id.music:
                Toast.makeText(this, "Music", Toast.LENGTH_SHORT).show();
                break;
            case R.id.map:
                 openMapsPage();
                break;
            case R.id.movies:
                 openMoviesPage();
                break;
            case R.id.traffic:
                openTrafficPage();
                break;
        }
    }
    public void openTrafficPage(){
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }
    public void openMoviesPage(){
        Intent intent = new Intent(MainActivity.this, MoviesActivity.class);
        startActivity(intent)  ;
    }
    public void openMapsPage(){
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

}