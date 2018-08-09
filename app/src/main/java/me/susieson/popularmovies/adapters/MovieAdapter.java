package me.susieson.popularmovies.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.popularmovies.R;
import me.susieson.popularmovies.database.MovieDatabase;
import me.susieson.popularmovies.interfaces.OnItemClickListener;
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.models.MovieViewModel;
import me.susieson.popularmovies.tasks.MovieExecutors;
import me.susieson.popularmovies.utils.ImageUtils;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static ArrayList<Movie> mMovieArrayList;
    private static MovieDatabase mMovieDatabase;
    private final OnItemClickListener mOnItemClickListener;
    private final Context mContext;

    public MovieAdapter(Context context, ArrayList<Movie> movieArrayList,
            OnItemClickListener onItemClickListener) {
        mMovieArrayList = movieArrayList;
        mOnItemClickListener = onItemClickListener;
        mMovieDatabase = MovieDatabase.getInstance(context);
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.movie_list_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position, mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mMovieArrayList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.recycle();
        super.onViewRecycled(holder);
    }

    public void updateData(ArrayList<Movie> movieArrayList) {
        mMovieArrayList = movieArrayList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster_image)
        ImageView mImageView;

        @BindView(R.id.movies_favorite_star)
        ToggleButton mToggleButton;

        @BindView(R.id.view_button)
        Button mViewButton;

        private LiveData<List<Movie>> mLiveDataMovies;
        private Observer<List<Movie>> mObserver;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final int position, final OnItemClickListener onItemClickListener) {
            final Movie movie = mMovieArrayList.get(position);

            final String URL = ImageUtils.buildUrl(movie.getPosterPath());
            Picasso.with(itemView.getContext()).load(URL).into(mImageView);

            mViewButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position);
                }
            });

            final CompoundButton.OnCheckedChangeListener listener =
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton,
                                final boolean isChecked) {

                            movie.setFavorited(isChecked);
                            MovieExecutors.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mMovieDatabase.movieDao().updateMovie(movie.getId(), isChecked);
                                }
                            });
                        }
                    };

            mObserver = new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    if (movies != null) {
                        if (movies.contains(movie)) {
                            mToggleButton.setOnCheckedChangeListener(null);
                            mToggleButton.setChecked(true);
                            mToggleButton.setOnCheckedChangeListener(listener);
                        } else {
                            mToggleButton.setOnCheckedChangeListener(null);
                            mToggleButton.setChecked(false);
                            mToggleButton.setOnCheckedChangeListener(listener);
                        }
                    }
                }
            };

            MovieViewModel movieViewModel = ViewModelProviders.of((FragmentActivity) mContext).get(
                    MovieViewModel.class);

            mLiveDataMovies = movieViewModel.getFavoriteMovies();
            mLiveDataMovies.observe((LifecycleOwner) mContext, mObserver);

            mToggleButton.setOnCheckedChangeListener(listener);
        }

        private void recycle() {
            mToggleButton.setOnCheckedChangeListener(null);
            mLiveDataMovies.removeObserver(mObserver);
        }
    }
}
