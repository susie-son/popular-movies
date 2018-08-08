package me.susieson.popularmovies.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReviewResponse {

    @SerializedName("results")
    private final ArrayList<Review> results = null;

    public ArrayList<Review> getResults() {
        return results;
    }

}
