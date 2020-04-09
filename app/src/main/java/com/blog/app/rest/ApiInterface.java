package com.blog.app.rest;


import com.blog.app.model.CategoryResponse;
import com.blog.app.model.MediaResponse;
import com.blog.app.model.NotificationRegRequest;
import com.blog.app.model.NotificationRegResponse;
import com.blog.app.model.PostResponse;

import java.util.List;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("/wp-json/wp/v2/categories?per_page=50")
    Call<List<CategoryResponse>> getBlogCategories();

    @GET("/wp-json/wp/v2/posts")
    Call<List<PostResponse>> getBlogPosts();

    @GET("/wp-json/wp/v2/posts")
    Call<List<PostResponse>> getBlogPostsByCategoryIdPageWise(@Query("categories[]") int id,@Query("page") int page_number);

    @GET("/wp-json/wp/v2/posts/{id}")
    Call<PostResponse> getBlogPostById(@Path("id") int id);

    @GET("/wp-json/wp/v2/media/{id}")
    Call<MediaResponse> getImageURLById(@Path("id") int id);

    @GET("/wp-json/wp/v2/posts")
    Call<List<PostResponse>> getBlogPostsPageWise(@Query("page") int page_number);


    @Headers({
            "Content-Type : application/json"
    })
    @POST("/?api-fcm=register")
    Call<NotificationRegResponse> getNotificationRegistrationResponse(@Body NotificationRegRequest notificationRegRequest);




}
