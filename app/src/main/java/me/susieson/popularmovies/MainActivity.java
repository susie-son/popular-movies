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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.susieson.popularmovies.adapters.MovieAdapter;
import me.susieson.popularmovies.constants.IntentExtraConstants;
import me.susieson.popularmovies.constants.PreferenceConstants;
import me.susieson.popularmovies.interfaces.OnItemClickListener;
import me.susieson.popularmovies.interfaces.TaskProgress;
import me.susieson.popularmovies.models.JsonResponse;
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.network.MovieQueryTask;
import me.susieson.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements TaskProgress, OnItemClickListener {

    private static String currentPreference = PreferenceConstants.MOST_POPULAR;
    private static final int MOVIE_POSTER_GRID_SPAN_PORTRAIT = 2;
    private static final int MOVIE_POSTER_GRID_SPAN_LANDSCAPE = 3;

    private ArrayList<Movie> mMovieArrayList;

    private MovieAdapter mMovieAdapter;

    @BindView(R.id.movies_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.main_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.main_loading_error)
    TextView mErrorMessage;

    @BindView(R.id.retry_button)
    Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        tryConnection(currentPreference);

        GridLayoutManager gridLayoutManager;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, MOVIE_POSTER_GRID_SPAN_PORTRAIT);
        } else {
            gridLayoutManager = new GridLayoutManager(this, MOVIE_POSTER_GRID_SPAN_LANDSCAPE);
        }

        mMovieArrayList = new ArrayList<>();

        mMovieAdapter = new MovieAdapter(mMovieArrayList, this);

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
        int menuItemSelectedId = item.getItemId();

        switch (menuItemSelectedId) {
            case R.id.most_popular:
                currentPreference = PreferenceConstants.MOST_POPULAR;
                tryConnection(currentPreference);
                return true;
            case R.id.top_rated:
                currentPreference = PreferenceConstants.TOP_RATED;
                tryConnection(currentPreference);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseMovieJson(String json) {
        if (json == null) {
            mMovieArrayList = null;
            return;
        }

        Gson gson = new Gson();
        JsonResponse jsonResponse = gson.fromJson(json, JsonResponse.class);

        mMovieArrayList = jsonResponse.getResults();
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.VISIBLE);
    }

    private void showProgressLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
    }

    private void hideProgressLoading() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
    }

    @Override
    public void onPreTask() {
        showProgressLoading();
    }

    @Override
    public void onTaskCompleted(String response) {
        parseMovieJson(response);

        hideProgressLoading();
        mMovieAdapter.updateData(mMovieArrayList);

        if (mMovieArrayList.isEmpty()) {
            showErrorMessage();
        }
    }

    @Override
    public void onItemClick(int position) {
        Movie selectedMovie = mMovieArrayList.get(position);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(IntentExtraConstants.EXTRA_SELECTED_MOVIE, selectedMovie);
        startActivity(intent);
    }

    @OnClick(R.id.retry_button)
    public void retryConnection(View view) {
        tryConnection(currentPreference);
    }

    private void tryConnection(String preference) {
        showProgressLoading();
        if (NetworkUtils.isConnected(this)) {
            URL builtUrl = NetworkUtils.buildUrl(preference);
            new MovieQueryTask(this).execute(builtUrl);
        } else {
            showErrorMessage();
        }
    }
}
