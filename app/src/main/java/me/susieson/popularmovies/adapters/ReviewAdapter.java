package me.susieson.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.susieson.popularmovies.R;
import me.susieson.popularmovies.models.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static ArrayList<Review> mReviewArrayList;

    public ReviewAdapter(ArrayList<Review> reviewArrayList) {
        mReviewArrayList = reviewArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.review_list_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mReviewArrayList.size();
    }

    public void updateData(ArrayList<Review> reviewArrayList) {
        mReviewArrayList = reviewArrayList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.author)
        TextView authorTextView;

        @BindView(R.id.content)
        TextView contentTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position) {
            Review review = mReviewArrayList.get(position);
            String author = review.getAuthor();
            String content = review.getContent();

            authorTextView.setText(author);
            contentTextView.setText(content);
        }
    }
}
