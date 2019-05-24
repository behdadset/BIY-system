package com.biy.reviewvia;


import java.util.List;

public class Review {

    private String barcode;
    private String originalProductName;
    private String reviewWebsiteUrl;
    private String productTitle;
    private String productAvatarUrl;
    private String productRating;
    private List<Reviewer> reviewsList;

    public Review(String barcode, String originalProductName, String reviewWebsiteUrl, String productTitle, String productAvatarUrl, String productRating, List<Reviewer> reviewsList) {
        this.barcode = barcode;
        this.originalProductName = originalProductName;
        this.reviewWebsiteUrl = reviewWebsiteUrl;
        this.productTitle = productTitle;
        this.productAvatarUrl = productAvatarUrl;
        this.productRating = productRating;
        this.reviewsList = reviewsList;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getOriginalProductName() {
        return originalProductName;
    }

    public void setOriginalProductName(String originalProductName) {
        this.originalProductName = originalProductName;
    }

    public String getReviewWebsiteUrl() {
        return reviewWebsiteUrl;
    }

    public void setReviewWebsiteUrl(String reviewWebsiteUrl) {
        this.reviewWebsiteUrl = reviewWebsiteUrl;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductAvatarUrl() {
        return productAvatarUrl;
    }

    public void setProductAvatarUrl(String productAvatarUrl) {
        this.productAvatarUrl = productAvatarUrl;
    }

    public String getProductRating() {
        return productRating;
    }

    public void setProductRating(String productRating) {
        this.productRating = productRating;
    }

    public List<Reviewer> getReviewsList() {
        return reviewsList;
    }

    public void setReviewsList(List<Reviewer> reviewsList) {
        this.reviewsList = reviewsList;
    }
}
