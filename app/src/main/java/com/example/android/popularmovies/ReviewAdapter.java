package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.model.Review;

import java.util.List;

class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    /* On-click handler */
    final ReviewAdapter.ReviewAdapterOnClickHandler mClickHandler;
    /* List for the data obtained in the database */
    private List<Review> mReviewData;

    /**
     * Creates a new {@link ReviewAdapter}
     *
     * @param clickHandler The on-click handler for this adapter
     */
    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review review = mReviewData.get(position);
        holder.mAuthorTextView.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        /* Early return if not valid data */
        if (mReviewData == null) {
            return 0;
        }
        return mReviewData.size();
    }

    /**
     * Update the list of reviews with the given list
     * @param reviewData The new list of reviews
     */
    public void setReviewData(List<Review> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }

    /* The interface that receives the onClick messages */
    interface ReviewAdapterOnClickHandler {
        void onClick(Review reviewItem);
    }

    /**
     * Cache of the children views for a movie list item.
     */
    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /* Display the author of the review */
        TextView mAuthorTextView;

        /* Default constructor */
        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.review_author);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Review reviewItem = mReviewData.get(adapterPosition);
            mClickHandler.onClick(reviewItem);
        }
    }
}


