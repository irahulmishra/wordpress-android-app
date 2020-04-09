package com.blog.app.model;


import com.google.gson.annotations.SerializedName;

public class CategoryResponse {
    @SerializedName("id")
    private int id;
    @SerializedName("count")
    private int count;
    @SerializedName("name")
    private String name;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
