package com.biy.reviewvia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

/**
 * @author Yating Zhang
 * Email : nicolezhangyt91@gmail.com
 * Date : 19/5/19
 * Description :
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context mContext;
    private List<Review> mData;

    public HistoryAdapter(Context context, List<Review> data) {
        mContext = context;
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_history_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Review review = mData.get(position);
        viewHolder.productName.setText(review.getOriginalProductName());
        viewHolder.barcode.setText(review.getBarcode());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(mData.get(position));
                Intent intent = new Intent(mContext, ReviewsActivity.class);
                intent.putExtra("review", jsonString);
                ((Activity) mContext).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView barcode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_product_name);
            barcode = itemView.findViewById(R.id.tv_barcode);
        }
    }
}
