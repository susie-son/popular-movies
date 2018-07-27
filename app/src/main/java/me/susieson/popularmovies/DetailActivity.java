package me.susieson.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.susieson.popularmovies.constants.IntentExtraConstants;
import me.susieson.popularmovies.model.Movie;
import me.susieson.popularmovies.utils.ImageUtils;
import me.susieson.popularmovies.utils.JsonUtils;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView thumbnail = findViewById(R.id.movie_poster_thumbnail);
        TextView overview1 = findViewById(R.id.overview);
        TextView voteAverage1 = findViewById(R.id.vote_average);
        TextView releaseDate1 = findViewById(R.id.release_date);

        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();

        if (intent.hasExtra(IntentExtraConstants.EXTRA_POSITION)) {
            int position = intent.getIntExtra(IntentExtraConstants.EXTRA_POSITION, -1);
            Movie movie = JsonUtils.getMovieList().get(position);

            String originalTitle = movie.getOriginalTitle();
            String posterPath = movie.getPosterPath();
            String overview = movie.getOverview();
            int voteAverage = movie.getVoteAverage();
            String releaseDate = movie.getReleaseDate();

            actionBar.setTitle(originalTitle);

            String imageUrl = ImageUtils.buildUrl(posterPath);
            Picasso.with(this).load(imageUrl).into(thumbnail);

            overview1.setText(overview);
            voteAverage1.setText(String.valueOf(voteAverage));
            releaseDate1.setText(releaseDate);
        }
    }
}
