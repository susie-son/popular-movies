package me.susieson.popularmovies.utils;

public class ImageUtils {

    private static final String BASE_URL = "http://image.tmdb.org/t/p";
    private static final String SIZE = "/w185";

    public static String buildUrl(String path) {
        return BASE_URL + SIZE + path;
    }
}
