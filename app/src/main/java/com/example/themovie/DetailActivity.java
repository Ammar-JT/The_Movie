package com.example.themovie;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.themovie.database.AppDatabase;
import com.example.themovie.database.Favorite;
import com.example.themovie.model.Movie;
import com.example.themovie.utilities.JsonMoviesUtils;
import com.example.themovie.utilities.UrlUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    // Constant for logging
    private static final String TAG = DetailActivity.class.getSimpleName();

    //Database instance member variable:
    private AppDatabase mDb;

    //button member var:
    Button mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //connect the java code with the layout:
        setContentView(R.layout.activity_detail);

        //initialize mDb:
        mDb = AppDatabase.getInstance(getApplicationContext());

        // make a java object for the image view to handle it:
        ImageView posterIv = findViewById(R.id.image_iv);


        // intent is like an a way to move form main to here.
        // in this case we see if the intent has a value or not:
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        //here we receive the movie json string through the intent:
        final String jsonMovieString = intent.getExtras().getString("jsonMovie");




        //we take the json String and parse it in JsonMoviesUtils, and then we make an object from the class Movie:
        final Movie movie = JsonMoviesUtils.parseJsonMovie(jsonMovieString);
        if (movie == null) {
            // Movie data unavailable
            closeOnError();
            return;
        }

        //we get the full movie poster url and load it to the image view:
        String movieUrl = UrlUtils.buildImageUrl(movie.getPosterPath());
        Picasso.with(this)
                .load(movieUrl)
                .into(posterIv);

        //we use this function to populate all the values to the views:
        populateUI(movie);
        setTitle(movie.getOriginalTitle());

        //button:
        mButton = findViewById(R.id.fav_button);

        int movieId = movie.getId();

        LiveData<Favorite> isFavorite = mDb.favoriteDao().loadFavoriteById(movieId);

        Log.d(TAG, "onCreate: " + isFavorite);
        isFavorite.observe(this, new Observer<Favorite>() {
            @Override
            public void onChanged(Favorite favorite) {
                final boolean movieExistInDB;

                if(favorite!=null){
                    Log.d(TAG, "this movie exist in the database");
                    movieExistInDB = true;
                    mButton.setText("Remove from Favorite");
                    mButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else{
                    Log.d(TAG, "This movie doesn't exist in the database!");
                    movieExistInDB = false;
                    mButton.setText("Add to Favorite");
                    mButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


                }

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFavButtonClicked(movie,jsonMovieString, movieExistInDB);
                    }
                });

            }
        });
    }


    private void closeOnError() {
        finish();
        Toast.makeText(this, "This movie is not available", Toast.LENGTH_SHORT).show();
    }


    public void onFavButtonClicked(Movie movie, String jsonMovieString, final boolean movieExistInDB) {


        final Favorite favorite = new Favorite(movie.getId(),movie.getOriginalTitle() , jsonMovieString);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(movieExistInDB){
                    mDb.favoriteDao().deleteFavorite(favorite);
                }else{
                    mDb.favoriteDao().insertFavorite(favorite);
                }

            }

        });
    }

    private void populateUI(Movie movie) {
        //make a java text view Object to handle the layout:
        TextView originalTitle = findViewById(R.id.original_title_tv);
        TextView overview = findViewById(R.id.overview_tv);
        TextView voteAverage = findViewById(R.id.vote_average_tv);
        TextView releaseDate = findViewById(R.id.release_date_tv);

        //set the text for the String text views:
        originalTitle.setText(movie.getOriginalTitle());
        overview.setText(movie.getOverview());
        voteAverage.setText(movie.getVoteAverage());
        releaseDate.setText(movie.getReleaseDate());

    }



}
