package me.susieson.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

import me.susieson.popularmovies.adapters.MovieAdapter;
import me.susieson.popularmovies.constants.IntentExtraConstants;
import me.susieson.popularmovies.constants.PreferenceConstants;
import me.susieson.popularmovies.model.Movie;
import me.susieson.popularmovies.network.MovieQueryTask;
import me.susieson.popularmovies.utils.JsonUtils;
import me.susieson.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements MovieQueryTask.TaskProgress,
        MovieAdapter.OnItemClickListener {

    private static String currentPreference = PreferenceConstants.mostPopular;
    private static final int MOVIE_POSTER_GRID_SPAN_PORTRAIT = 2;
    private static final int MOVIE_POSTER_GRID_SPAN_LANDSCAPE = 3;

    private MovieAdapter mMovieAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessage = findViewById(R.id.main_loading_error);
        mProgressBar = findViewById(R.id.main_progress_bar);
        mRecyclerView = findViewById(R.id.movies_rv);

        if (NetworkUtils.isConnected(this)) {
            URL builtUrl = NetworkUtils.buildUrl(currentPreference);
            new MovieQueryTask(this).execute(builtUrl);
        } else {
            showErrorMessage();
        }

        GridLayoutManager gridLayoutManager;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, MOVIE_POSTER_GRID_SPAN_PORTRAIT);
        } else {
            gridLayoutManager = new GridLayoutManager(this, MOVIE_POSTER_GRID_SPAN_LANDSCAPE);
        }

        mMovieAdapter = new MovieAdapter(JsonUtils.getMovieList(), this);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMovieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_by, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (NetworkUtils.isConnected(this)) {
            int menuItemSelectedId = item.getItemId();

            URL builtUrl;
            switch (menuItemSelectedId) {
                case R.id.most_popular:
                    currentPreference = PreferenceConstants.mostPopular;
                    builtUrl = NetworkUtils.buildUrl(currentPreference);
                    new MovieQueryTask(this).execute(builtUrl);
                    return true;
                case R.id.top_rated:
                    currentPreference = PreferenceConstants.topRated;
                    builtUrl = NetworkUtils.buildUrl(currentPreference);
                    new MovieQueryTask(this).execute(builtUrl);
                    return true;
            }
        }

        showErrorMessage();
        return super.onOptionsItemSelected(item);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showProgressLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);
    }

    private void hideProgressLoading() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.GONE);
    }

    @Override
    public void onPreTask() {
        showProgressLoading();
    }

    @Override
    public void onTaskCompleted(String response) {
        JsonUtils.parseMovieJson(response);

        hideProgressLoading();
        mMovieAdapter.updateData(JsonUtils.getMovieList());

        if (JsonUtils.getMovieList().isEmpty()) {
            showErrorMessage();
        }
    }

    @Override
    public void onItemClick(int position) {
        Movie selectedMovie = JsonUtils.getMovieList().get(position);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(IntentExtraConstants.EXTRA_SELECTED_MOVIE, selectedMovie);
        startActivity(intent);
    }
}
