package me.susieson.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.susieson.popularmovies.model.Movie;

public class JsonUtils {

    private static ArrayList<Movie> mMovieArrayList;

    public static void parseMovieJson(String json) {
        if (json == null) {
            mMovieArrayList = null;
            return;
        }

        ArrayList<Movie> movies = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(json);
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String originalTitle = result.getString("original_title");
                String posterPath = result.getString("poster_path");
                String overview = result.getString("overview");
                double voteAverage = result.getDouble("vote_average");
                String releaseDate = result.getString("release_date");
                Movie movie = new Movie(originalTitle, posterPath, overview, voteAverage, releaseDate);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mMovieArrayList = movies;
    }

    public static ArrayList<Movie> getMovieList() {
        if (mMovieArrayList != null)
            return mMovieArrayList;
        return new ArrayList<>();
    }
}
