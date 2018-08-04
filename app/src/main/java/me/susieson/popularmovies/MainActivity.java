package me.susieson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.susieson.popularmovies.adapters.MovieAdapter;
import me.susieson.popularmovies.constants.IntentExtraConstants;
import me.susieson.popularmovies.constants.PreferenceConstants;
import me.susieson.popularmovies.interfaces.OnItemClickListener;
import me.susieson.popularmovies.models.MovieResponse;
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.network.GetMovieData;
import me.susieson.popularmovies.network.RetrofitClientInstance;
import me.susieson.popularmovies.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    private static String currentPreference = PreferenceConstants.MOST_POPULAR;

    private ArrayList<Movie> mMovieArrayList;

    private MovieAdapter mMovieAdapter;

    private Callback<MovieResponse> mCallback;

    @BindView(R.id.movies_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.main_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.main_loading_error)
    TextView mErrorMessage;

    @BindView(R.id.retry_button)
    Button mRetryButton;

    @BindInt(R.integer.movie_poster_grid_span)
    int gridSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMovieArrayList = new ArrayList<>();

        mCallback = new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call,
                    @NonNull Response<MovieResponse> response) {
                hideProgressLoading();

                if (response.body() != null) {
                    mMovieArrayList = response.body().getResults();
                }

                mMovieAdapter.updateData(mMovieArrayList);
                mRecyclerView.scrollToPosition(0);

                if (mMovieArrayList.isEmpty()) {
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                showErrorMessage();
            }
        };

        tryConnection(currentPreference);

        GridLayoutManager gridLayoutManager;

        gridLayoutManager = new GridLayoutManager(this, gridSpan);

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
            if (preference.equals(PreferenceConstants.MOST_POPULAR)) {
                GetMovieData getMovieData = RetrofitClientInstance.getRetrofitInstance().create(
                        GetMovieData.class);
                Call<MovieResponse> call = getMovieData.getMostPopularMovies(
                        BuildConfig.TMDB_API_KEY);
                call.enqueue(mCallback);
            } else if (preference.equals(PreferenceConstants.TOP_RATED)) {
                GetMovieData getMovieData = RetrofitClientInstance.getRetrofitInstance().create(
                        GetMovieData.class);
                Call<MovieResponse> call = getMovieData.getTopRatedMovies(BuildConfig.TMDB_API_KEY);
                call.enqueue(mCallback);
            }
        } else {
            showErrorMessage();
        }
    }
}
