package me.susieson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.susieson.popularmovies.model.Movie;
import me.susieson.popularmovies.utils.ImageUtils;

public class DetailActivity extends AppCompatActivity {

    private ImageView mThumbnail;
    private TextView mOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mThumbnail = findViewById(R.id.movie_poster_thumbnail);
        mOverview = findViewById(R.id.overview);
        mVoteAverage = findViewById(R.id.vote_average);
        mReleaseDate = findViewById(R.id.release_date);

        mActionBar = getSupportActionBar();

        Intent intent = getIntent();

        if (intent.hasExtra(MainActivity.EXTRA_POSITION)) {
            int position = intent.getIntExtra(MainActivity.EXTRA_POSITION, -1);
            Movie movie = MainActivity.mMovieArrayList.get(position);

            String originalTitle = movie.getOriginalTitle();
            String posterPath = movie.getPosterPath();
            String overview = movie.getOverview();
            int voteAverage = movie.getVoteAverage();
            String releaseDate = movie.getReleaseDate();

            mActionBar.setTitle(originalTitle);

            String imageUrl = ImageUtils.buildUrl(posterPath);
            Picasso.with(this).load(imageUrl).into(mThumbnail);

            mOverview.setText(overview);
            mVoteAverage.setText(String.valueOf(voteAverage));
            mReleaseDate.setText(releaseDate);
        }
    }
}
