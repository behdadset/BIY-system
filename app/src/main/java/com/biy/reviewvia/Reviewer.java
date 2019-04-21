package com.biy.reviewvia;

/**
 * @author Nicole Zhang
 * Description :
 */
public class Reviewer {
    private String author;
    private String publishedDate;
    private String review;

    public Reviewer(){

    }

    public Reviewer(String author, String publishedDate, String review) {
        this.author = author;
        this.publishedDate = publishedDate;
        this.review = review;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
