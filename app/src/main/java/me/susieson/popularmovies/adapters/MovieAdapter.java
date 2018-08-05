package me.susieson.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.popularmovies.R;
import me.susieson.popularmovies.interfaces.OnItemClickListener;
import me.susieson.popularmovies.models.Movie;
import me.susieson.popularmovies.utils.ImageUtils;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static ArrayList<Movie> mMovieArrayList;
    private final OnItemClickListener mOnItemClickListener;

    public MovieAdapter(ArrayList<Movie> movieArrayList, OnItemClickListener onItemClickListener) {
        mMovieArrayList = movieArrayList;
        mOnItemClickListener = onItemClickListener;
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

    public void updateData(ArrayList<Movie> movieArrayList) {
        mMovieArrayList = movieArrayList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster_image)
        ImageView mImageView;

        @BindView(R.id.movie_title)
        TextView mTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final int position, final OnItemClickListener onItemClickListener) {
            Movie movie = mMovieArrayList.get(position);
            final String URL = ImageUtils.buildUrl(movie.getPosterPath());
            mTitle.setText(movie.getTitle());

            mImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position);
                }
            });

            Picasso.with(itemView.getContext()).load(URL).error(R.drawable.image_not_available).into(mImageView);
        }
    }
}
