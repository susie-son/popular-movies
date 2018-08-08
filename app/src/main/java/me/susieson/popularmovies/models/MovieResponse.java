package me.susieson.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MovieResponse {

    @SerializedName("results")
    private final ArrayList<Movie> results = null;

    public ArrayList<Movie> getResults() {
        return results;
    }

}
