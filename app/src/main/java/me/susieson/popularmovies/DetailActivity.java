package me.susieson.popularmovies;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static me.susieson.popularmovies.MainActivity.EXTRA_SELECTED_MOVIE;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.popularmovies.adapters.ReviewAdapter;
import me.susieson.popularmovies.adapters.TrailerAdapter;
import me.susieson.popularmovies.database.MovieDatabase;
import me.susieson.popularmovies.interfaces.OnItemClickListener;
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.models.MovieViewModel;
import me.susieson.popularmovies.models.Review;
import me.susieson.popularmovies.models.ReviewResponse;
import me.susieson.popularmovies.models.Trailer;
import me.susieson.popularmovies.models.TrailerResponse;
import me.susieson.popularmovies.network.GetMovieData;
import me.susieson.popularmovies.network.RetrofitClientInstance;
import me.susieson.popularmovies.tasks.MovieExecutors;
import me.susieson.popularmovies.utils.ImageUtils;
import me.susieson.popularmovies.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements OnItemClickListener {

    private static final String TRAILER_LIST_EXTRA = "trailer_list";
    private static final String REVIEW_LIST_EXTRA = "review_list";
    private static final String CONNECTION_SUCCESSFUL_EXTRA = "connection_successful";

    @BindView(R.id.movie_poster_thumbnail)
    ImageView mThumbnail;
    @BindView(R.id.movie_title)
    TextView mTitle;
    @BindView(R.id.overview)
    TextView mOverviewTv;
    @BindView(R.id.vote_average)
    TextView mVoteAverageTv;
    @BindView(R.id.release_date)
    TextView mReleaseDateTv;
    @BindView(R.id.reviews_rv)
    RecyclerView mReviewRecyclerView;
    @BindView(R.id.trailers_rv)
    RecyclerView mTrailerRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.loading_error)
    TextView mErrorMessage;
    @BindView(R.id.retry_button)
    Button mRetryButton;
    @BindView(R.id.details_favorite_star)
    ToggleButton mToggleButton;

    private ArrayList<Review> mReviewArrayList;
    private ArrayList<Trailer> mTrailerArrayList;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private MovieDatabase mMovieDatabase;

    private int mId;
    private boolean mConnectionSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_SELECTED_MOVIE)) {

            final Movie selectedMovie = getIntent().getParcelableExtra(EXTRA_SELECTED_MOVIE);

            String originalTitle = selectedMovie.getOriginalTitle();
            String posterPath = selectedMovie.getPosterPath();
            String overview = selectedMovie.getOverview();
            double voteAverage = selectedMovie.getVoteAverage();
            String releaseDate = selectedMovie.getReleaseDate();
            mId = selectedMovie.getId();

            if (originalTitle != null && !originalTitle.equals("")) {
                mTitle.setText(originalTitle);
            }

            String imageUrl = ImageUtils.buildUrl(posterPath);
            Picasso.with(this).load(imageUrl).into(mThumbnail);

            if (overview != null && !overview.equals("")) {
                mOverviewTv.setText(overview);
            }

            mVoteAverageTv.setText(String.valueOf(voteAverage));

            if (releaseDate != null && !releaseDate.equals("")) {
                mReleaseDateTv.setText(releaseDate);
            }

            mMovieDatabase = MovieDatabase.getInstance(this);

            MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
            LiveData<List<Movie>> liveDataMovies = movieViewModel.getFavoriteMovies();
            liveDataMovies.observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    if (movies == null) return;
                    if (movies.contains(selectedMovie)) {
                        mToggleButton.setChecked(true);
                    } else {
                        mToggleButton.setChecked(false);
                    }
                }
            });

            mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton,
                        final boolean isChecked) {
                    selectedMovie.setFavorited(isChecked);

                    MovieExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mMovieDatabase.movieDao().updateMovie(selectedMovie.getId(), isChecked);
                        }
                    });
                }
            });

            mReviewArrayList = new ArrayList<>();
            mTrailerArrayList = new ArrayList<>();

            if (savedInstanceState != null && savedInstanceState.containsKey(
                    CONNECTION_SUCCESSFUL_EXTRA)) {
                showProgressLoading();
                if (savedInstanceState.getBoolean(CONNECTION_SUCCESSFUL_EXTRA)) {
                    if (savedInstanceState.containsKey(TRAILER_LIST_EXTRA)) {
                        mTrailerArrayList = savedInstanceState.getParcelableArrayList(
                                TRAILER_LIST_EXTRA);
                    }
                    if (savedInstanceState.containsKey(REVIEW_LIST_EXTRA)) {
                        mReviewArrayList = savedInstanceState.getParcelableArrayList(
                                REVIEW_LIST_EXTRA);
                    }
                    hideProgressLoading();
                } else {
                    showErrorMessage();
                }
            } else {
                tryConnection(mId);
            }

            LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
            LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);

            mReviewAdapter = new ReviewAdapter(mReviewArrayList);
            mTrailerAdapter = new TrailerAdapter(mTrailerArrayList, this);

            mReviewRecyclerView.setNestedScrollingEnabled(false);
            mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
            mReviewRecyclerView.setAdapter(mReviewAdapter);
            mTrailerRecyclerView.setNestedScrollingEnabled(false);
            mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
            mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        }
    }

    @Override
    public void onItemClick(int position) {
        Trailer trailer = mTrailerArrayList.get(position);
        String id = trailer.getKey();

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.trailer_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(REVIEW_LIST_EXTRA, mReviewArrayList);
        outState.putParcelableArrayList(TRAILER_LIST_EXTRA, mTrailerArrayList);
        outState.putBoolean(CONNECTION_SUCCESSFUL_EXTRA, mConnectionSuccessful);
    }

    private void tryConnection(final int id) {
        if (NetworkUtils.isConnected(this)) {
            showProgressLoading();
            final GetMovieData getMovieData = RetrofitClientInstance.getRetrofitInstance().create(
                    GetMovieData.class);

            Call<TrailerResponse> trailerCall = getMovieData.getTrailers(id,
                    BuildConfig.TMDB_API_KEY);
            trailerCall.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(@NonNull Call<TrailerResponse> call,
                        @NonNull Response<TrailerResponse> response) {
                    if (response.body() != null) {

                        mTrailerArrayList = response.body().getResults();
                        mTrailerAdapter.updateData(mTrailerArrayList);

                        Call<ReviewResponse> reviewCall = getMovieData.getReviews(id,
                                BuildConfig.TMDB_API_KEY);
                        reviewCall.enqueue(new Callback<ReviewResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<ReviewResponse> call,
                                    @NonNull Response<ReviewResponse> response) {
                                hideProgressLoading();
                                if (response.body() != null) {
                                    mReviewArrayList = response.body().getResults();
                                    mReviewAdapter.updateData(mReviewArrayList);
                                } else {
                                    showErrorMessage();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ReviewResponse> call,
                                    @NonNull Throwable t) {
                                showErrorMessage();
                            }
                        });

                    } else {
                        showErrorMessage();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TrailerResponse> call, @NonNull Throwable t) {
                    showErrorMessage();
                }
            });
        } else {
            showErrorMessage();
        }
    }

    public void retryConnection(View view) {
        tryConnection(mId);
    }

    private void showErrorMessage() {
        mTrailerRecyclerView.setVisibility(GONE);
        mReviewRecyclerView.setVisibility(GONE);
        mProgressBar.setVisibility(GONE);
        mErrorMessage.setVisibility(VISIBLE);
        mRetryButton.setVisibility(VISIBLE);
        mConnectionSuccessful = false;
    }

    private void showProgressLoading() {
        mTrailerRecyclerView.setVisibility(GONE);
        mReviewRecyclerView.setVisibility(GONE);
        mErrorMessage.setVisibility(GONE);
        mRetryButton.setVisibility(GONE);
        mProgressBar.setVisibility(VISIBLE);
    }

    private void hideProgressLoading() {
        mProgressBar.setVisibility(GONE);
        mErrorMessage.setVisibility(GONE);
        mRetryButton.setVisibility(GONE);
        mTrailerRecyclerView.setVisibility(VISIBLE);
        mReviewRecyclerView.setVisibility(VISIBLE);
        mConnectionSuccessful = true;
    }
}
