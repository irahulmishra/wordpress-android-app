package com.blog.app.model;

import com.google.gson.annotations.SerializedName;

public class Excerpt {
    @SerializedName("rendered")
    private String rendered;

    public String getRendered() {
        return rendered;
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }
}
