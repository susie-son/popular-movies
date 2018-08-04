package me.susieson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.popularmovies.adapters.ReviewAdapter;
import me.susieson.popularmovies.constants.IntentExtraConstants;
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.models.Review;
import me.susieson.popularmovies.models.ReviewResponse;
import me.susieson.popularmovies.network.GetMovieData;
import me.susieson.popularmovies.network.RetrofitClientInstance;
import me.susieson.popularmovies.utils.ImageUtils;
import me.susieson.popularmovies.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.movie_poster_thumbnail)
    ImageView mThumbnail;

    @BindView(R.id.overview)
    TextView mOverviewTv;

    @BindView(R.id.vote_average)
    TextView mVoteAverageTv;

    @BindView(R.id.release_date)
    TextView mReleaseDateTv;

    @BindView(R.id.movie_detail_no_info)
    TextView mErrorMessageTv;

    @BindView(R.id.reviews_rv)
    RecyclerView mReviewRecyclerView;

    ArrayList<Review> mReviewArrayList;
    ReviewAdapter mReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();

        if (intent.hasExtra(IntentExtraConstants.EXTRA_SELECTED_MOVIE)) {

            Movie selectedMovie = getIntent().getParcelableExtra(
                    IntentExtraConstants.EXTRA_SELECTED_MOVIE);

            String originalTitle = selectedMovie.getOriginalTitle();
            String posterPath = selectedMovie.getPosterPath();
            String overview = selectedMovie.getOverview();
            double voteAverage = selectedMovie.getVoteAverage();
            String releaseDate = selectedMovie.getReleaseDate();
            int id = selectedMovie.getId();

            if (actionBar != null && originalTitle != null) {
                actionBar.setTitle(originalTitle);
            }

            String imageUrl = ImageUtils.buildUrl(posterPath);
            Picasso.with(this).load(imageUrl).error(R.drawable.image_not_available).into(
                    mThumbnail);

            if (overview != null && !overview.equals("")) {
                mOverviewTv.setText(overview);
            } else {
                mOverviewTv.setText(R.string.not_available);
            }

            mVoteAverageTv.setText(
                    String.format(Locale.getDefault(), getString(R.string.vote_average_out_of),
                            voteAverage));

            if (releaseDate != null && !releaseDate.equals("")) {
                mReleaseDateTv.setText(releaseDate);
            } else {
                mReleaseDateTv.setText(R.string.not_available);
            }

            mReviewArrayList = new ArrayList<>();

            tryConnection(id);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);

            mReviewAdapter = new ReviewAdapter(mReviewArrayList);

            mReviewRecyclerView.setNestedScrollingEnabled(false);
            mReviewRecyclerView.setLayoutManager(layoutManager);
            mReviewRecyclerView.setAdapter(mReviewAdapter);

        } else {
            mErrorMessageTv.setVisibility(View.VISIBLE);
        }
    }

    private void tryConnection(int id) {
        if (NetworkUtils.isConnected(this)) {
            GetMovieData getMovieData = RetrofitClientInstance.getRetrofitInstance().create(
                    GetMovieData.class);
            Call<ReviewResponse> call = getMovieData.getReviews(id, BuildConfig.TMDB_API_KEY);
            call.enqueue(new Callback<ReviewResponse>() {
                @Override
                public void onResponse(Call<ReviewResponse> call,
                        Response<ReviewResponse> response) {
                    mReviewArrayList = response.body().getResults();
                    mReviewAdapter.updateData(mReviewArrayList);
                }

                @Override
                public void onFailure(Call<ReviewResponse> call, Throwable t) {

                }
            });
        }
    }

}
