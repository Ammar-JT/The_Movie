/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.themovie.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class UrlUtils {

    // a base url for popular movie and top rated movie:
    final static String TMDB_BASE_URL1 =
            "https://api.themoviedb.org/3/movie/popular";
    final static String TMDB_BASE_URL2 =
            "https://api.themoviedb.org/3/movie/top_rated";

    // a parameter for the url:
    final static String PARAM_QUERY1 = "api_key";
    final static String PARAM_QUERY2 = "language";

    //value for the parameter
    final static String PARAM_QUERY1_val = "eb766f85ed760bf3ada7c7b7a8f5ef45";
    final static String PARAM_QUERY2_val = "en-US";

    //basic url for the poster image:
    final static String TMDB_IMAGE_BASE =
            "http://image.tmdb.org/t/p/w500/";

    /**
     * @param sortBy The keyword that will be queried for.
     * @return The URL to use to query the TMDB server.
     */
    public static URL buildUrl(int sortBy){
        //this url will be built by default, and for popular sort:
        Uri builtUri = Uri.parse(TMDB_BASE_URL1).buildUpon()
                .appendQueryParameter(PARAM_QUERY1, PARAM_QUERY1_val)
                .appendQueryParameter(PARAM_QUERY2, PARAM_QUERY2_val)
                .build();

        //this url will be built for the top rated movie only:
        if(sortBy==2){
            builtUri = Uri.parse(TMDB_BASE_URL2).buildUpon()
                    .appendQueryParameter(PARAM_QUERY1, PARAM_QUERY1_val)
                    .appendQueryParameter(PARAM_QUERY2, PARAM_QUERY2_val)
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    //this method build the poster image url, after it receive a parameter with the poster image path only:
    public static String buildImageUrl(String imgPath){
        Uri builtUri = Uri.parse(TMDB_IMAGE_BASE).buildUpon()
                .appendPath(imgPath)
                .build();
        String url = builtUri.toString();
        return url;
    }

}