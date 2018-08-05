package me.susieson.popularmovies;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.popularmovies.adapters.ReviewAdapter;
import me.susieson.popularmovies.adapters.TrailerAdapter;
import me.susieson.popularmovies.constants.IntentExtraConstants;
import me.susieson.popularmovies.interfaces.OnItemClickListener;
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.models.Review;
import me.susieson.popularmovies.models.ReviewResponse;
import me.susieson.popularmovies.models.Trailer;
import me.susieson.popularmovies.models.TrailerResponse;
import me.susieson.popularmovies.network.GetMovieData;
import me.susieson.popularmovies.network.RetrofitClientInstance;
import me.susieson.popularmovies.utils.ImageUtils;
import me.susieson.popularmovies.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements OnItemClickListener {

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

    ArrayList<Review> mReviewArrayList;
    ReviewAdapter mReviewAdapter;

    ArrayList<Trailer> mTrailerArrayList;
    TrailerAdapter mTrailerAdapter;

    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent.hasExtra(IntentExtraConstants.EXTRA_SELECTED_MOVIE)) {

            Movie selectedMovie = getIntent().getParcelableExtra(
                    IntentExtraConstants.EXTRA_SELECTED_MOVIE);

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
            Picasso.with(this).load(imageUrl).error(R.drawable.image_not_available).into(
                    mThumbnail);

            if (overview != null && !overview.equals("")) {
                mOverviewTv.setText(overview);
            }

            mVoteAverageTv.setText(String.valueOf(voteAverage));

            if (releaseDate != null && !releaseDate.equals("")) {
                mReleaseDateTv.setText(releaseDate);
            }

            mReviewArrayList = new ArrayList<>();
            mTrailerArrayList = new ArrayList<>();

            tryConnection(mId);

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
                            public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
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
    }
}
