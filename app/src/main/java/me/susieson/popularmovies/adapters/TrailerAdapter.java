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
import me.susieson.popularmovies.interfaces.OnItemClickListener;
import me.susieson.popularmovies.models.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private static ArrayList<Trailer> mTrailerArrayList;
    private final OnItemClickListener mOnItemClickListener;

    public TrailerAdapter(ArrayList<Trailer> trailerArrayList,
            OnItemClickListener onItemClickListener) {
        mTrailerArrayList = trailerArrayList;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.trailer_list_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position, mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mTrailerArrayList.size();
    }

    public void updateData(ArrayList<Trailer> trailerArrayList) {
        mTrailerArrayList = trailerArrayList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.trailer_name)
        TextView trailerNameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final int position, final OnItemClickListener onItemClickListener) {
            Trailer trailer = mTrailerArrayList.get(position);

            trailerNameTextView.setText(trailer.getName());

            trailerNameTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }
}
