package me.susieson.popularmovies.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import me.susieson.popularmovies.database.MovieDatabase;

public class MovieViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        movies = MovieDatabase.getInstance(this.getApplication()).movieDao().getFavorites();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return movies;
    }

}
