package me.susieson.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.susieson.popularmovies.adapters.MovieAdapter;
import me.susieson.popularmovies.constants.IntentExtraConstants;
import me.susieson.popularmovies.constants.PreferenceConstants;
import me.susieson.popularmovies.database.MovieDatabase;
import me.susieson.popularmovies.interfaces.OnItemClickListener;
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.models.MovieResponse;
import me.susieson.popularmovies.network.GetMovieData;
import me.susieson.popularmovies.network.RetrofitClientInstance;
import me.susieson.popularmovies.tasks.MovieExecutors;
import me.susieson.popularmovies.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {

    public static final String SORT_BY = "sort-by";

    private ArrayList<Movie> mMovieArrayList;

    private MovieAdapter mMovieAdapter;

    private Callback<MovieResponse> mCallback;

    private SharedPreferences mSharedPreferences;

    private MovieDatabase mMovieDatabase;

    private LiveData<List<Movie>> mLiveDataMovies;

    private Observer<List<Movie>> mObserver;

    @BindView(R.id.movies_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.main_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.main_loading_error)
    TextView mErrorMessage;

    @BindView(R.id.retry_button)
    Button mRetryButton;

    @BindView(R.id.favorites_error)
    TextView mFavoritesError;

    @BindInt(R.integer.movie_poster_grid_span)
    int gridSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mMovieArrayList = new ArrayList<>();

        mMovieDatabase = MovieDatabase.getInstance(this);

        mObserver = new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies == null || movies.isEmpty()) {
                    showFavoritesError();
                } else {
                    hideProgressLoading();
                    mMovieArrayList = (ArrayList<Movie>) movies;

                    mMovieAdapter.updateData(mMovieArrayList);
                }
            }
        };

        mLiveDataMovies = mMovieDatabase.movieDao().getFavorites();

        mCallback = new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call,
                    @NonNull Response<MovieResponse> response) {
                hideProgressLoading();

                if (response.body() != null) {
                    mMovieArrayList = response.body().getResults();
                    MovieExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mMovieDatabase.movieDao().insertMovies(mMovieArrayList);
                        }
                    });
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

        tryConnection(mSharedPreferences.getString(SORT_BY, PreferenceConstants.MOST_POPULAR));

        GridLayoutManager gridLayoutManager;

        gridLayoutManager = new GridLayoutManager(this, gridSpan);

        mMovieAdapter = new MovieAdapter(this, mMovieArrayList, this);

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

        SharedPreferences.Editor editor = mSharedPreferences.edit();

        switch (menuItemSelectedId) {
            case R.id.most_popular:
                editor.putString(SORT_BY, PreferenceConstants.MOST_POPULAR);
                editor.apply();
                tryConnection(PreferenceConstants.MOST_POPULAR);
                return true;
            case R.id.top_rated:
                editor.putString(SORT_BY, PreferenceConstants.TOP_RATED);
                editor.apply();
                tryConnection(PreferenceConstants.TOP_RATED);
                return true;
            case R.id.favorites:
                editor.putString(SORT_BY, PreferenceConstants.FAVORITES);
                editor.apply();
                tryConnection(PreferenceConstants.FAVORITES);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showErrorMessage() {
        mFavoritesError.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.VISIBLE);
    }

    private void showProgressLoading() {
        mFavoritesError.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressLoading() {
        mFavoritesError.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showFavoritesError() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.GONE);
        mRetryButton.setVisibility(View.GONE);
        mFavoritesError.setVisibility(View.VISIBLE);
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
        tryConnection(mSharedPreferences.getString(SORT_BY, PreferenceConstants.MOST_POPULAR));
    }

    private void tryConnection(String preference) {
        showProgressLoading();
        if (NetworkUtils.isConnected(this)) {
            switch (preference) {
                case PreferenceConstants.MOST_POPULAR: {
                    mLiveDataMovies.removeObserver(mObserver);
                    GetMovieData getMovieData = RetrofitClientInstance.getRetrofitInstance().create(
                            GetMovieData.class);
                    Call<MovieResponse> call = getMovieData.getMostPopularMovies(
                            BuildConfig.TMDB_API_KEY);
                    call.enqueue(mCallback);
                    break;
                }
                case PreferenceConstants.TOP_RATED: {
                    mLiveDataMovies.removeObserver(mObserver);
                    GetMovieData getMovieData = RetrofitClientInstance.getRetrofitInstance().create(
                            GetMovieData.class);
                    Call<MovieResponse> call = getMovieData.getTopRatedMovies(
                            BuildConfig.TMDB_API_KEY);
                    call.enqueue(mCallback);
                    break;
                }
                case PreferenceConstants.FAVORITES:
                    mLiveDataMovies.removeObserver(mObserver);
                    mLiveDataMovies.observe(this, mObserver);
                    break;
            }
        } else if (preference.equals(PreferenceConstants.FAVORITES)) {
            mLiveDataMovies.removeObserver(mObserver);
            mLiveDataMovies.observe(this, mObserver);
        } else {
            showErrorMessage();
        }
    }
}
