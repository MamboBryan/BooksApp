package com.mambobryan.booklisting;

public class Book {

    private final String mAuthor;
    private String mTitle;
    private Double mRating;
    private String mLink;
    private String mPublisher;
    private String mPublishedDate;
    private String mPreviewLink;
    private String mImageLink;

    public String getDescription() {
        return mDescription;
    }

    public int getPageCount() {
        return mPageCount;
    }

    private String mDescription;
    private int mPageCount;


    public Book(String author, String title, Double rating, String link, String publishedDate) {
        mAuthor = author;
        mTitle = title;
        mRating = rating;
        mLink = link;
        mPublishedDate = publishedDate;
    }

    public Book(String author, String title, Double rating, String link, String publisher,
                String publishedDate, String previewLink, String imageLink, String description, int pages) {
        mAuthor = author;
        mTitle = title;
        mRating = rating;
        mLink = link;
        mPublisher = publisher;
        mPublishedDate = publishedDate;
        mPreviewLink = previewLink;
        mImageLink = imageLink;
        mDescription = description;
        mPageCount = pages;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getTitle() {
        return mTitle;
    }

    public Double getRating() {
        return mRating;
    }

    public String getLink() {
        return mLink;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

    public String getPreviewLink() {
        return mPreviewLink;
    }

    public String getImageLink() {
        return mImageLink;
    }
}
