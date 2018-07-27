package me.susieson.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import me.susieson.popularmovies.adapters.MovieAdapter;
import me.susieson.popularmovies.model.Movie;
import me.susieson.popularmovies.utils.JsonUtils;
import me.susieson.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "me.susieson.popularmovies.MainActivity.POSITION";
    private static String currentPreference = NetworkUtils.mostPopular;
    private static final int MOVIE_POSTER_GRID_SPAN = 5;

    public static ArrayList<Movie> mMovieArrayList;
    private static MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL builtUrl = NetworkUtils.buildUrl(currentPreference);
        new MovieQueryTask().execute(builtUrl);

        RecyclerView recyclerView = findViewById(R.id.movies_rv);

        mMovieArrayList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, MOVIE_POSTER_GRID_SPAN);
        mMovieAdapter = new MovieAdapter(mMovieArrayList);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_by, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelectedId = item.getItemId();

        URL builtUrl;
        switch (menuItemSelectedId) {
            case R.id.most_popular:
                currentPreference = NetworkUtils.mostPopular;
                builtUrl = NetworkUtils.buildUrl(currentPreference);
                new MovieQueryTask().execute(builtUrl);
                return true;
            case R.id.top_rated:
                currentPreference = NetworkUtils.topRated;
                builtUrl = NetworkUtils.buildUrl(currentPreference);
                new MovieQueryTask().execute(builtUrl);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
