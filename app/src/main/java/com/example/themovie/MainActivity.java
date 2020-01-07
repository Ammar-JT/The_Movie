package com.example.themovie;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.themovie.database.AppDatabase;
import com.example.themovie.database.Favorite;
import com.example.themovie.utilities.JsonMoviesUtils;
import com.example.themovie.utilities.UrlUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements MovieAdapter.ListItemClickListener{

    //Just a global private variables:
    private static final int Movie_LIST_ITEMS = 20;
    private MovieAdapter mAdapter;
    private RecyclerView mMovieList;
    private String [] JsonMoviesArray;
    private MovieAdapter.ListItemClickListener globalListener;
    private static final String TAG = MainActivity.class.getSimpleName();


    //Database instance member variable:
    private AppDatabase mDb;

    // Constant for default sortedBy to be used when not in update mode
    private static final int DEFAULT_SORTED_BY = 1;

    //a variable to know what the user choose to sort the movies list:
    private int mSortedBy = DEFAULT_SORTED_BY;

    // a fixed key for favorite movie's "sort by"  to be received after rotation
    public static final String INSTANCE_SORTED_BY = "instanceSortedBy";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize mDb:
        mDb = AppDatabase.getInstance(getApplicationContext());

        //Recycler View reference variable:
        mMovieList = findViewById(R.id.rv_movies);

        //Grid layout Manager, to make the app present grid arrangement of movie posters:
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mMovieList.setLayoutManager(layoutManager);

        mMovieList.setHasFixedSize(true);

        //We need the listener later, so put the value to this global variable:
        globalListener = this;


        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_SORTED_BY)) {
            mSortedBy = savedInstanceState.getInt(INSTANCE_SORTED_BY, DEFAULT_SORTED_BY);
        }

        if(mSortedBy==DEFAULT_SORTED_BY){
            apiRequest(mSortedBy);
        }else if(mSortedBy==2){
            apiRequest(mSortedBy);
        }else if(mSortedBy==3){
            favoriteMoviesList();
        }




    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_SORTED_BY, mSortedBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // here we get which button been clicked from the top bar:
        int itemId = item.getItemId();
        if(itemId==R.id.popular){

            apiRequest(1);
            mSortedBy = 1;
            return true;
        }else if (itemId==R.id.top_rated){

            apiRequest(2);
            mSortedBy = 2;
            return true;
        }else if (itemId==R.id.favorites){

            favoriteMoviesList();
            mSortedBy = 3;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //this function used when the user sort the movies list by popular movie or top rated
    // the sortByChoice for popular is 1, and for top rated is 2:
    public void apiRequest(int sortByChoice){
        //this is the api request url, which is for popular movies (using UrlUtils class with the function buildUrl:
        URL sortedByApiRequestUrl = UrlUtils.buildUrl(sortByChoice);

        //next, will execute the api request in a background thread:
        new TMDBQueryTask().execute(sortedByApiRequestUrl);
    }



    // Here we used AsyncTask to execute the api request in the background thread:
    public class TMDBQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... params) {
            URL apiURL = params[0];
            String apiRequestResults = null;

            try {
                apiRequestResults = UrlUtils.getResponseFromHttpUrl(apiURL);
            } catch (IOException e){
                e.printStackTrace();
            }
            return apiRequestResults;
        }

        //after getting the api request results, we override onPostExecute function:
        @Override
        protected void onPostExecute(String apiRequestResults) {
            if (apiRequestResults != null && !apiRequestResults.equals("")) {
                //with this line we convert the json request results of all movies,
                // .. into an array of every movie in a single index:
                JsonMoviesArray = JsonMoviesUtils.MoviesJson(apiRequestResults);

                //now we make the adapter for the recycler view and send also the json Movies Array:
                mAdapter = new MovieAdapter(Movie_LIST_ITEMS, globalListener, JsonMoviesArray);
                mMovieList.setAdapter(mAdapter);

            }
        }

    }


    //in this case, we don't need an api request, because we already have the json string of the user's fav movies:
    //all we gonna do is to wrap all these fav movies in a json movies string array:
    public void favoriteMoviesList() {
        //LiveData<List<Favorite>> favoriteMovies = mDb.favoriteDao().loadAllFavorites();

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavorites().observe(this, new Observer<List<Favorite>>() {
            @Override
            public void onChanged(@Nullable List<Favorite> favorites) {

                if (favorites != null) {
                    Log.d(TAG, "the database isn't empty");

                    int favoriteListLength = favorites.size();
                    String[] favoriteMoviesJsonArray = new String[favoriteListLength];

                    //convert the array of Favorite movie objects into favorite movie json array:
                    for (int x = 0; x < favoriteListLength; x++) {
                        favoriteMoviesJsonArray[x] = favorites.get(x).getJsonMovieString();
                        Log.d(TAG, "Movie index:" + x + ", Movie Json:" + favoriteMoviesJsonArray[x]);
                    }

                    //now we make the adapter for the recycler view and send also the json Movies Array:
                    JsonMoviesArray = favoriteMoviesJsonArray;
                    mAdapter = new MovieAdapter(favoriteListLength, globalListener, favoriteMoviesJsonArray);
                    mMovieList.setAdapter(mAdapter);

                } else {
                    Log.d(TAG, "the database is empty");
                }


            }
        });
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        //We make an intent to move to another activity:
        Intent intent = new Intent(this, DetailActivity.class);

        // here we get the specific movie json string:
        String jsonMovieString = JsonMoviesArray[clickedItemIndex];

        //we transfer the that single movie json String:
        intent.putExtra("jsonMovie", jsonMovieString);

        //start the intent
        startActivity(intent);

    }
}
