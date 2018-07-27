package me.susieson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import me.susieson.popularmovies.constants.IntentExtraConstants;
import me.susieson.popularmovies.model.Movie;
import me.susieson.popularmovies.utils.ImageUtils;
import me.susieson.popularmovies.utils.JsonUtils;

public class DetailActivity extends AppCompatActivity {

    private ImageView mThumbnail;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private TextView mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mThumbnail = findViewById(R.id.movie_poster_thumbnail);
        mOverview = findViewById(R.id.overview);
        mVoteAverage = findViewById(R.id.vote_average);
        mReleaseDate = findViewById(R.id.release_date);

        mErrorMessage = findViewById(R.id.movie_detail_no_info);

        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();

        if (intent.hasExtra(IntentExtraConstants.EXTRA_POSITION)) {
            int position = intent.getIntExtra(IntentExtraConstants.EXTRA_POSITION, -1);
            Movie movie = JsonUtils.getMovieList().get(position);

            String originalTitle = movie.getOriginalTitle();
            String posterPath = movie.getPosterPath();
            String overview = movie.getOverview();
            double voteAverage = movie.getVoteAverage();
            String releaseDate = movie.getReleaseDate();

            if (actionBar!= null && originalTitle != null) {
                actionBar.setTitle(originalTitle);
            }

            String imageUrl = ImageUtils.buildUrl(posterPath);
            Picasso.with(this).load(imageUrl).error(R.drawable.image_not_available).into(mThumbnail);

            if (overview != null && !overview.equals("")) {
                mOverview.setText(overview);
            } else {
                mOverview.setText(R.string.not_available);
            }

            mVoteAverage.setText(String.format(Locale.getDefault(), getString(R.string.vote_average_out_of), voteAverage));

            if (releaseDate != null && !releaseDate.equals("")) {
                mReleaseDate.setText(releaseDate);
            } else {
                mReleaseDate.setText(R.string.not_available);
            }

        } else {
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }
}
