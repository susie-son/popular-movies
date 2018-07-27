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
import me.susieson.popularmovies.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static ArrayList<Movie> mMovieArrayList;

    public MovieAdapter(ArrayList<Movie> movieArrayList) {
        mMovieArrayList = movieArrayList;
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
        holder.bind(position);
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

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final String BASE_URL = "http://image.tmdb.org/t/p";
        private final String SIZE = "/w185";

        private ImageView mImageView;
        private Context mContext;

        ViewHolder(View itemView, Context context) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.movie_poster_image);
            mContext = context;
        }

        void bind(int position) {
            final String PATH = mMovieArrayList.get(position).getPosterPath();
            final String URL = BASE_URL + SIZE + PATH;

            mImageView.setOnClickListener(this);

            Picasso.with(mContext).load(URL).into(mImageView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
        }
    }
}
