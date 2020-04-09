package com.blog.app.model;

import com.google.gson.annotations.SerializedName;


public class MediaResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("source_url")
    private String source_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }
}
