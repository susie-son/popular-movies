package me.susieson.popularmovies.network;

import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.models.MovieApiResponse;
import me.susieson.popularmovies.models.Review;
import me.susieson.popularmovies.models.Trailer;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMovieData {

    @GET("3/movie/popular")
    Call<MovieApiResponse<Movie>> getMostPopularMovies(@Query("api_key") String apiKey);

    @GET("3/movie/top_rated")
    Call<MovieApiResponse<Movie>> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("3/movie/{movie_id}/reviews")
    Call<MovieApiResponse<Review>> getReviews(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{movie_id}/videos")
    Call<MovieApiResponse<Trailer>> getTrailers(@Path("movie_id") int movieId,
            @Query("api_key") String apiKey);

}
