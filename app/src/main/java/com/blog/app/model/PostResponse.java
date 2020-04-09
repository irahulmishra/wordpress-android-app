package com.blog.app.model;

import com.google.gson.annotations.SerializedName;

public class PostResponse {
    @SerializedName("id")
    private int id;



    @SerializedName("date_gmt")
    private String date_gmt;

    @SerializedName("title")
    private Title title;


    @SerializedName("content")
    private Content content;

    @SerializedName("excerpt")
    private Excerpt excerpt ;


    @SerializedName("featured_media")
    private int featured_media;


    @SerializedName("link")
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getFeatured_media() {
        return featured_media;
    }

    public void setFeatured_media(int featured_media) {
        this.featured_media = featured_media;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Excerpt getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(Excerpt excerpt) {
        this.excerpt = excerpt;
    }

    public String getDate_gmt() {
        return date_gmt;
    }

    public void setDate_gmt(String date_gmt) {
        this.date_gmt = date_gmt;
    }
}
