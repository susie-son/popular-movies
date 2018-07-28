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
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.utils.ImageUtils;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView thumbnail = findViewById(R.id.movie_poster_thumbnail);
        TextView overviewTv = findViewById(R.id.overview);
        TextView voteAverageTv = findViewById(R.id.vote_average);
        TextView releaseDateTv = findViewById(R.id.release_date);

        TextView errorMessage = findViewById(R.id.movie_detail_no_info);

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

            if (actionBar != null && originalTitle != null) {
                actionBar.setTitle(originalTitle);
            }

            String imageUrl = ImageUtils.buildUrl(posterPath);
            Picasso.with(this).load(imageUrl).error(R.drawable.image_not_available).into(
                    thumbnail);

            if (overview != null && !overview.equals("")) {
                overviewTv.setText(overview);
            } else {
                overviewTv.setText(R.string.not_available);
            }

            voteAverageTv.setText(
                    String.format(Locale.getDefault(), getString(R.string.vote_average_out_of),
                            voteAverage));

            if (releaseDate != null && !releaseDate.equals("")) {
                releaseDateTv.setText(releaseDate);
            } else {
                releaseDateTv.setText(R.string.not_available);
            }

        } else {
            errorMessage.setVisibility(View.VISIBLE);
        }
    }
}
