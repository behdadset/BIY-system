package com.biy.reviewvia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {

    private TextView tvProductName;
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private RatingBar mRatingBar;

    ArrayList<Reviewer> mList = new ArrayList<>();
    private RecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        mRecyclerView = findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);


        mAdapter = new RecyclerViewAdapter(mList,this);
        mRecyclerView.setAdapter(mAdapter);

        tvProductName = findViewById(R.id.tv_produtname);

        mImageView = findViewById(R.id.image_product);
        mRatingBar = findViewById(R.id.ratingbar);

        getWebsite();

    }

    private void getWebsite(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try{
                    Document doc = Jsoup.connect("https://www.productreview.com.au/listings/bose-quietcomfort-35-qc35-ii").get();
                    final Element titleElement = doc.selectFirst("#content > div:nth-child(4) > div.flex-column_36B.d-flex_b9D > div.flex-grow-1_3A8.relative_2e- > div > div.mb-3_2I3.card_134.card-full_3wf.card-full-md_2gh > div > div > div > div.align-items-stretch_26k.align-items-md-start_YBp.flex-column_36B.flex-md-row_2Sy.media_xHn > div.media-body_sNW > div.align-items-start_3CC.flex-column_36B.d-flex_b9D > h1 > span > span");
                    final Elements reviewElements = doc.getElementsByAttributeValue("itemprop", "review");

                    handleReviewItems(reviewElements);



                    final Element imageElment = doc.selectFirst("#content > div:nth-child(4) > div.flex-column_36B.d-flex_b9D > div.flex-grow-1_3A8.relative_2e- > div > div.mb-3_2I3.card_134.card-full_3wf.card-full-md_2gh > div > div > div > div.align-items-stretch_26k.align-items-md-start_YBp.flex-column_36B.flex-md-row_2Sy.media_xHn > div.mr-0_1y6.mr-md-5_Y3v.mb-4_1be.mb-md-0_3-U.cursor--pointer_1MN.card_134.container_2PS > div > img");
                    final Element ratingElement = doc.selectFirst("#content > div:nth-child(4) > div.flex-column_36B.d-flex_b9D > div.flex-grow-1_3A8.relative_2e- > div > div.mb-3_2I3.card_134.card-full_3wf.card-full-md_2gh > div > div > div > div.align-items-stretch_26k.align-items-md-start_YBp.flex-column_36B.flex-md-row_2Sy.media_xHn > div.media-body_sNW > div.mt-4_3rb.align-items-center_nNY.flex-wrap_1Wl.cursor--pointer_1MN.d-inline-flex_2a2 > div.flex-wrap_1Wl.d-flex_b9D > span > span.text-dark_3qN.font-size-lg_3Ta.font-weight-bold_xjV > span");

                    String src = imageElment.attr("src");

                    final URL url = new URL(src);
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    if (titleElement != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvProductName.setText(titleElement.text());
                                mAdapter.notifyDataSetChanged();
                                mImageView.setImageBitmap(bmp);
                                mRatingBar.setRating(Float.parseFloat(ratingElement.text()));

                            }
                        });

                    }
                }catch (IOException e){

                }
            }
        }).start();
    }

    void handleReviewItems(Elements reviewElements) {
        for(Element element : reviewElements) {
            final Element authorElement = element.selectFirst("div>div:first-child>div:first-child h4");
             String date = element.select("meta").attr("itemprop", "datePublished").get(1).attr("content");
            date = date.split("T")[0];
            final Element reviewElement = element.getElementsByAttributeValue("itemProp", "description").get(0);
            Reviewer reviewer = new Reviewer(authorElement.text(),date,reviewElement.text());
            mList.add(reviewer);
            Log.d("reviewer",mList.toString());
           /* Log.d("author",authorElement.text());
            Log.d("date",date);
            Log.d("review",reviewElement.text());*/
        }

    }


}
