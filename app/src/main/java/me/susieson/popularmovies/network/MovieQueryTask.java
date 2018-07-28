package me.susieson.popularmovies.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

import me.susieson.popularmovies.utils.NetworkUtils;

public class MovieQueryTask extends AsyncTask<URL, Void, String> {

    private TaskProgress mTaskProgress;

    public MovieQueryTask(TaskProgress taskProgress) {
        mTaskProgress = taskProgress;
    }

    @Override
    protected void onPreExecute() {
        mTaskProgress.onPreTask();
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL url = urls[0];

        String response = null;

        try {
            response = NetworkUtils.getHttpResponse(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        mTaskProgress.onTaskCompleted(s);
    }

    public interface TaskProgress {
        void onPreTask();
        void onTaskCompleted(String response);
    }
}
