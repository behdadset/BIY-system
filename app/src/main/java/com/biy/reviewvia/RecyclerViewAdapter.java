package com.biy.reviewvia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<Reviewer> mReviewer;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<Reviewer> reviewer, Context context) {
        mReviewer = reviewer;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView author,publishedDate,review;

        public ViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.tv_author);
            publishedDate =itemView.findViewById(R.id.tv_publishedDate);
            review = itemView.findViewById(R.id.tv_reviewer);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Reviewer reviewer = mReviewer.get(position);
        holder.review.setText(reviewer.getReview());
        holder.author.setText(reviewer.getAuthor());
        holder.publishedDate.setText(reviewer.getPublishedDate());
    }

    @Override
    public int getItemCount() {
        return mReviewer.size();
    }

}

