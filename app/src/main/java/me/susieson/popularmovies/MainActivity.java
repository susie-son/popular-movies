package me.susieson.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import me.susieson.popularmovies.adapters.MovieAdapter;
import me.susieson.popularmovies.model.Movie;
import me.susieson.popularmovies.utils.JsonUtils;
import me.susieson.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "me.susieson.popularmovies.MainActivity.POSITION";
    private static final int MOVIE_POSTER_GRID_SPAN = 5;

    public static ArrayList<Movie> mMovieArrayList;
    private static MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL builtUrl = NetworkUtils.buildUrl();
        new MovieQueryTask().execute(builtUrl);

        RecyclerView recyclerView = findViewById(R.id.movies_rv);

        mMovieArrayList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, MOVIE_POSTER_GRID_SPAN);
        mMovieAdapter = new MovieAdapter(mMovieArrayList);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mMovieAdapter);
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

        @Override
        protected void onPostExecute(String s) {
            mMovieArrayList = JsonUtils.parseMovieJson(s);

            mMovieAdapter.updateData(mMovieArrayList);
        }
    }
}
