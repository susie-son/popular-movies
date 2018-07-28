package me.susieson.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

        return new ViewHolder(view, context);
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
        mMovieArrayList.clear();
        mMovieArrayList.addAll(movieArrayList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageView;
        private final Context mContext;

        ViewHolder(View itemView, Context context) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.movie_poster_image);
            mContext = context;
        }

        void bind(final int position, final OnItemClickListener onItemClickListener) {
            final String URL = ImageUtils.buildUrl(mMovieArrayList.get(position).getPosterPath());

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position);
                }
            });

            Picasso.with(mContext).load(URL).error(R.drawable.image_not_available).into(mImageView);
        }
    }
}
