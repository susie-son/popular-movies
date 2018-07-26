package me.susieson.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;

import me.susieson.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL builtUrl = NetworkUtils.buildUrl();
        new MovieQueryTask().execute(builtUrl);
    }

    public static class MovieQueryTask extends AsyncTask<URL, Void, String> {
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
    }
}
