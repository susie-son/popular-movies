package me.susieson.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrailerResponse {

    @SerializedName("results")
    private final ArrayList<Trailer> results = null;

    public ArrayList<Trailer> getResults() {
        return results;
    }

}
