package me.susieson.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MovieApiResponse<T> {

    @SerializedName("results")
    private final ArrayList<T> results = null;

    public ArrayList<T> getResults() {
        return results;
    }

}
