package me.susieson.popularmovies.interfaces;

public interface TaskProgress {
    void onPreTask();

    void onTaskCompleted(String response);
}
