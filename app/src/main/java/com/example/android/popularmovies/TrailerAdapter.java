package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    /* On-click handler */
    final TrailerAdapter.TrailerAdapterOnClickHandler mClickHandler;
    /* List for the data obtained in the database */
    private List<Trailer> mTrailerData;

    /* Default constructor */
    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, int position) {
        Trailer trailer = mTrailerData.get(position);
        holder.mNameTextView.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        /* Early return if not valid data */
        if (mTrailerData == null) {
            return 0;
        }
        return mTrailerData.size();
    }

    public void setTrailerData(List<Trailer> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }

    /* The interface that receives the onClick messages */
    interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailerItem);
    }

    /**
     * Cache of the children views for a movie list item.
     */
    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /* Display the author of the review */
        TextView mNameTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Trailer trailerItem = mTrailerData.get(adapterPosition);
            mClickHandler.onClick(trailerItem);
        }
    }
}
