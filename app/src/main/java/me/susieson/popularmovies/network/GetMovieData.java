package me.susieson.popularmovies.network;

import me.susieson.popularmovies.models.MovieResponse;
import me.susieson.popularmovies.models.ReviewResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMovieData {

    @GET("3/movie/popular")
    Call<MovieResponse> getMostPopularMovies(@Query("api_key") String apiKey);

    @GET("3/movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("3/movie/{movie_id}/reviews")
    Call<ReviewResponse> getReviews(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

}
