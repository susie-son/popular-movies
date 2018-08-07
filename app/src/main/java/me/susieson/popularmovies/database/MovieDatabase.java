package me.susieson.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import me.susieson.popularmovies.models.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "movies";
    private static MovieDatabase sMovieDatabase;

    public static MovieDatabase getInstance(Context context) {
        if (sMovieDatabase == null) {
            sMovieDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    MovieDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return sMovieDatabase;
    }

    public abstract MovieDao movieDao();

}
