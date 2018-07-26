package me.susieson.popularmovies.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String PARAM_API_KEY = "api_key";
    private static final String PARAM_SORT_BY = "sort_by";

    private static final String apiKey = "";
    private static final String sortBy = "popularity.desc";

    public static URL buildUrl() {
        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey)
                .appendQueryParameter(PARAM_SORT_BY, sortBy)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
