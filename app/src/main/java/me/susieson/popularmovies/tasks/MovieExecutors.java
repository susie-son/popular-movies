package me.susieson.popularmovies.tasks;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MovieExecutors {

    private static MovieExecutors sInstance;
    private final Executor diskIO;

    private MovieExecutors(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static MovieExecutors getInstance() {
        if (sInstance == null) {
            sInstance = new MovieExecutors(Executors.newSingleThreadExecutor());
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

}
