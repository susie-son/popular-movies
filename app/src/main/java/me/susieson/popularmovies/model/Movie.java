package me.susieson.popularmovies.model;

public class Movie {

    private String originalTitle;
    private String posterPath;
    private String overview;
    private double voteAverage;
    private String releaseDate;

    public Movie(String originalTitle, String posterPath, String overview, double voteAverage, String releaseDate) {
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
