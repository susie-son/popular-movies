package me.susieson.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import me.susieson.popularmovies.models.Movie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie WHERE is_favorited = 1")
    LiveData<List<Movie>> getFavorites();

    @Query("UPDATE movie SET is_favorited = :isFavorited WHERE id = :id")
    void updateMovie(int id, boolean isFavorited);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovies(List<Movie> movies);

}
