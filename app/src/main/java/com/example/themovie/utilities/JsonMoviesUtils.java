package com.example.themovie.utilities;

import android.util.Log;

import com.example.themovie.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonMoviesUtils {

    //this function is used to separate the movie json String into single movies for each index in the array:
    public static String [] MoviesJson(String MoviesJsonString) {
        //we make movie array before the try, because if an error happened,
        //the the MoviesJson method can return the object as null:
        String [] moviesArray = null;
        try{
            //First, Initialize JSON Object from the Movies Json String:
            JSONObject MoviesJsonObject = new JSONObject(MoviesJsonString);

            //we get a json array of all movies:
            JSONArray moviesJsonArray = MoviesJsonObject.getJSONArray("results");

            //here we convert the json array into regular array:
            moviesArray = new String[moviesJsonArray.length()];
            for(int i=0; i<moviesJsonArray.length(); i++) {
                moviesArray[i]=moviesJsonArray.optString(i);
            }


        }catch (JSONException e) {

            Log.e("JsonMoviesUtils", "Problem getting the movies array", e);
        }
        return moviesArray;
    }

    //this function is used to
    public static String imageMoviePath(String movieJsonString) {
        String posterPath = null;
        try{
            //First, Initialize JSON Object from the single movie Json String:
            JSONObject movieJsonObject = new JSONObject(movieJsonString);

            //Second, we get the poster path, but the problem is it has a redundant back slash in the begin:
            String posterBadPath = movieJsonObject.getString("poster_path");

            //third, we remove that redundant back slash:
            posterPath = posterBadPath.substring(1);

        }catch (JSONException e) {
            Log.e("JsonMoviesUtils", "Problem getting the movie poster path", e);
        }

        return posterPath;
    }


    //this function is used to parse the movie json string for a single movie, and then make a Movie object:
    public static Movie parseJsonMovie(String jsonMovieString) {
        Movie parsedMovie = null;
        try{
            //First, Initialize JSON Object from the Movie Json String:
            JSONObject movieJsonObject = new JSONObject(jsonMovieString);

            //then we get all the string from this json object:
            String originalTitle = movieJsonObject.getString("original_title");
            String overview = movieJsonObject.getString("overview");
            String voteAverage = movieJsonObject.getString("vote_average");
            String releaseDate = movieJsonObject.getString("release_date");

            int id = movieJsonObject.getInt("id");

            // we get the poster path, but the problem is it has a redundant back slash in the begin:
            String posterBadPath = movieJsonObject.getString("poster_path");
            //we remove that redundant back slash:
            String posterPath = posterBadPath.substring(1);





            //we make an object from the class Movie, because we will use it in the DetailActivity.java
            parsedMovie = new Movie(originalTitle, posterPath, overview, voteAverage, releaseDate,id);

        }catch (JSONException e) {

            Log.e("JsonUtils", "Problem parsing the Sandwich JSON Object", e);
        }
        return parsedMovie;
    }
}
