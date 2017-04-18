package com.movies.ahmedamir.mymovies.models;

/**
 * Created by Ahmed Amir on 9/5/2016.
 */
public class ReviewsJson {
    private int id;
    private int page;
    private Review[] results;
    private int total_pages;
    private int total_results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getId() {
        return id;

    }

    public void setId(int id) {
        this.id = id;
    }

    public Review[] getResults() {
        return results;
    }

    public void setResults(Review[] results) {
        this.results = results;
    }
}
