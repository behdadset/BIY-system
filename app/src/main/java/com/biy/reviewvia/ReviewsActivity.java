package com.biy.reviewvia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReviewsActivity extends AppCompatActivity {

    private String mProductName;
    private String mBarcode;
    private TextView tvProductName;
    private ImageView mImageView;
    private RatingBar mRatingBar;

    List<Reviewer> mList = new ArrayList<>();
    private RecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(mList);
        recyclerView.setAdapter(mAdapter);
        tvProductName = findViewById(R.id.tv_produtname);
        mImageView = findViewById(R.id.image_product);
        mRatingBar = findViewById(R.id.ratingbar);

        String reviewJson = getIntent().getStringExtra("review");
        if (reviewJson != null) {
            Gson gson = new Gson();
            Review review = gson.fromJson(reviewJson, Review.class);
            mList.addAll(review.getReviewsList());
            loadData(review);
        } else {
            mProductName = getIntent().getStringExtra("product_name");
            mBarcode = getIntent().getStringExtra("barcode");
            getWebsite();
        }
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String targetUrl = "https://www.productreview.com.au/search?q=" + Uri.encode(mProductName);

                try {
                    Document doc = Jsoup.connect(targetUrl).get();
                    handleResultList(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                    toastError("Something went wrong");
                }
            }
        }).start();
    }

    private void handleResultList(Document searchResultDoc) {
        final Element firstResult = searchResultDoc.selectFirst("a[href^=/listings/]");
        if (firstResult == null) {
            toastError("No matching data");
        } else {
            String firstItemUrl = firstResult.attr("href");
            handleReviewDetail(firstItemUrl);
        }
    }

    private void handleReviewDetail(String itemUrl) {
        try {
            String targetUrl = "https://www.productreview.com.au" + itemUrl;
            Document doc = Jsoup.connect(targetUrl).get();
            final Element titleElement = doc.selectFirst("meta[name=keywords]");

            if (titleElement != null) {
                final String productTitle = titleElement.attr("content");
                final Elements reviewElements = doc.getElementsByAttributeValue("itemprop", "review");
                handleReviewItems(reviewElements);
                final String avatarUrl = doc.selectFirst("meta[itemprop=image]").attr("content");
                final Element ratingElement = doc.selectFirst("div[itemprop=aggregateRating]").selectFirst("meta[itemprop=ratingValue]");

                final Review review = new Review(
                        mBarcode,
                        mProductName,
                        targetUrl,
                        productTitle,
                        avatarUrl,
                        ratingElement.attr("content"),
                        new ArrayList<>(mList));

                saveToFirebase(review);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadData(review);
                    }
                });
            } else {
                toastError("No matching data");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            toastError("Something went wrong");
        }
    }

    private void loadData(final Review review) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URL url = new URL(review.getProductAvatarUrl());
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvProductName.setText(review.getProductTitle());
                            mAdapter.notifyDataSetChanged();
                            mImageView.setImageBitmap(bmp);
                            mRatingBar.setRating(Float.parseFloat(review.getProductRating()));

                        }
                    });
                } catch (IOException exception) {
                    exception.printStackTrace();
                    toastError("Something went wrong");
                }
            }
        }).start();
    }

    private void handleReviewItems(Elements reviewElements) {
        for (Element element : reviewElements) {
            final Element authorElement = element.selectFirst("div>div:first-child>div:first-child h4");
            String date = element.select("meta").attr("itemprop", "datePublished").get(1).attr("content");
            date = date.split("T")[0];
            final Element reviewElement = element.getElementsByAttributeValue("itemProp", "description").get(0);
            Reviewer reviewer = new Reviewer(authorElement.text(), date, reviewElement.text());
            mList.add(reviewer);
        }

    }

    private void toastError(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ReviewsActivity.this, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFirebase(Review review) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews")
                .document(review.getBarcode())
                .set(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error writing document", e);
                    }
                });
    }


}
