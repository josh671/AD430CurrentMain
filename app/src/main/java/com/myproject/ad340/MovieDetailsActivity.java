package com.myproject.ad340;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        TextView title = findViewById(R.id.detailTitle);
        TextView movieYear = findViewById(R.id.detailYear);
        ImageView movieImage = findViewById(R.id.detailImage);
        TextView movieDescription = findViewById(R.id.detailDescription);

        Bundle bundle = this.getIntent().getExtras();
        String[] movieItem = bundle.getStringArray("movie");
        title.setText(movieItem[0]);
        movieYear.setText(movieItem[1]);

        Picasso instance = Picasso.get() ;
        instance.setIndicatorsEnabled(true);
        instance.load(movieItem[3]).into(movieImage);

        movieDescription.setText(movieItem[4]);

    }


}