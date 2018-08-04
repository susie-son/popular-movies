package me.susieson.popularmovies.network;

import me.susieson.popularmovies.models.JsonResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetMovieData {

    @GET("3/movie/popular")
    Call<JsonResponse> getMostPopularMovies(@Query("api_key") String apiKey);

    @GET("3/movie/top_rated")
    Call<JsonResponse> getTopRatedMovies(@Query("api_key") String apiKey);
}
