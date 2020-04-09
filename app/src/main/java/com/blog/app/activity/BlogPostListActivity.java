package com.blog.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.blog.app.config.ConfigPostListActivity;
import com.blog.app.R;
import com.blog.app.adapter.DividerItemDecoration;
import com.blog.app.adapter.PostsAdapter;
import com.blog.app.adapter.RecyclerTouchListener;
import com.blog.app.model.CategoryResponse;
import com.blog.app.model.MediaResponse;
import com.blog.app.model.PostResponse;
import com.blog.app.rest.ApiClient;
import com.blog.app.rest.ApiInterface;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogPostListActivity extends AppCompatActivity {

    private static final String TAG = BlogPostListActivity.class.getSimpleName();
    private  PostsAdapter postsAdapter;
    private  List<PostResponse> postList = new ArrayList<>();
    private  RecyclerView recyclerView;
    private  ApiInterface apiService ;
    private   Call<List<PostResponse>> callPostsListOriginal ;
    private ProgressBar mProgressBar  ;
    private AdView mAdView;
    private AdRequest adRequest;
    private Intent intentBundle ;
    private boolean boolShowNext = false ;
    private  int CUURENT_PAGE_NUMBER = 1;
    private  int TOTAL_PAGES_NUMBER = 0 ;
    private  int CATEGORY_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post_list);

        //Log.e(TAG,"OnCreate Bundle of Activity");
        apiService = ApiClient.getClient().create(ApiInterface.class);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        mAdView = (AdView) findViewById(R.id.adViewBlogListHome);
        adRequest = new AdRequest.Builder()
                .build();


        if(ConfigPostListActivity.categoryResponseList.isEmpty()){
            getCategoryList();
        }

        intentBundle = new Intent(this,BlogPostActivity.class);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        postsAdapter = new PostsAdapter(postList,R.layout.post_list_row,this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PostResponse post = postList.get(position);
                //Toast.makeText(getBaseContext(), "Loading Post " + post.getTitle().getRendered(), Toast.LENGTH_SHORT).show();
                intentBundle.putExtra("postid",post.getId());
                startActivity(intentBundle);
            }
            @Override
            public void onLongClick(View view, int position) {
            }
        }));
        getPostListPageWise(CATEGORY_ID,CUURENT_PAGE_NUMBER);

    }
    private void getPostListPageWise(int category_id,int page_number) {
        //Log.e(TAG,"get post list of page number :"+page_number);
        //Log.e(TAG,"get post list of category id :"+category_id);
        if(category_id == 0){
            callPostsListOriginal = apiService.getBlogPostsPageWise(page_number);
            callPostsListOriginal.enqueue(new Callback<List<PostResponse>>() {
                @Override
                public void onResponse(Call<List<PostResponse>> call, final Response<List<PostResponse>> response) {
                    int total_records = Integer.parseInt(response.headers().get("X-Wp-Total"));
                    TOTAL_PAGES_NUMBER = Integer.parseInt(response.headers().get("X-Wp-TotalPages"));
                    postList.clear();
                    postList.addAll(response.body());
                    postsAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mAdView.loadAd(adRequest);
                    if(CUURENT_PAGE_NUMBER < TOTAL_PAGES_NUMBER){
                        boolShowNext = true ;
                        invalidateOptionsMenu();
                    }
                    if(CUURENT_PAGE_NUMBER == TOTAL_PAGES_NUMBER){
                        boolShowNext = false ;
                        invalidateOptionsMenu();
                    }
                }
                @Override
                public void onFailure(Call<List<PostResponse>> call, Throwable t) {

                }
            });

        }
        else if(category_id > 0){

            callPostsListOriginal = apiService.getBlogPostsByCategoryIdPageWise(category_id,page_number);
            callPostsListOriginal.enqueue(new Callback<List<PostResponse>>() {
                @Override
                public void onResponse(Call<List<PostResponse>> call, final Response<List<PostResponse>> response) {

                    int total_records = Integer.parseInt(response.headers().get("X-Wp-Total"));
                    TOTAL_PAGES_NUMBER = Integer.parseInt(response.headers().get("X-Wp-TotalPages"));
                    postList.clear();
                    postList.addAll(response.body());
                    postsAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    if(CUURENT_PAGE_NUMBER < TOTAL_PAGES_NUMBER){
                        boolShowNext = true ;
                        invalidateOptionsMenu();
                    }
                    if(CUURENT_PAGE_NUMBER == TOTAL_PAGES_NUMBER){
                        boolShowNext = false ;
                        invalidateOptionsMenu();
                    }
                }
                @Override
                public void onFailure(Call<List<PostResponse>> call, Throwable t) {

                }
            });

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_blog_post_list, menu);
        MenuItem item_left = menu.findItem(R.id.action_left);
        MenuItem item_right = menu.findItem(R.id.action_right);

        if(CUURENT_PAGE_NUMBER == 1){
            item_left.setVisible(false);

        }
        else {
            item_left.setVisible(true);

        }
        if(boolShowNext == true){
            item_right.setVisible(true);

        }
        else {
            item_right.setVisible(false);

        }

        for(CategoryResponse catResponse : ConfigPostListActivity.categoryResponseList){
            menu.add(Menu.NONE,catResponse.getId(), Menu.NONE, Jsoup.parse(catResponse.getName()).text());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        for (CategoryResponse catRes : ConfigPostListActivity.categoryResponseList){

            if(item.getItemId() == catRes.getId()){

//                Toast.makeText(this, catRes.getName()+" Selected", Toast.LENGTH_SHORT)
//                            .show();
                CATEGORY_ID = catRes.getId();
                CUURENT_PAGE_NUMBER = 1 ;
                getPostListPageWise(CATEGORY_ID,CUURENT_PAGE_NUMBER);
                invalidateOptionsMenu();
                recyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                return true;
            }
        }
        switch (item.getItemId()) {
            // action with ID action_right was selected
            case R.id.action_home_page:

                CATEGORY_ID = 0 ;
                CUURENT_PAGE_NUMBER = 1 ;
                getPostListPageWise(CATEGORY_ID,CUURENT_PAGE_NUMBER);
                Toast.makeText(this, "Page "+ CUURENT_PAGE_NUMBER, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                recyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            // action with ID action_right was selected
            case R.id.action_right:
                ++CUURENT_PAGE_NUMBER;
                getPostListPageWise(CATEGORY_ID,CUURENT_PAGE_NUMBER);
                Toast.makeText(this, "Page "+ CUURENT_PAGE_NUMBER, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                recyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                break;
            // action with ID action_left was selected
            case R.id.action_left:
                --CUURENT_PAGE_NUMBER;
                getPostListPageWise(CATEGORY_ID,CUURENT_PAGE_NUMBER);
                Toast.makeText(this, "Page "+ CUURENT_PAGE_NUMBER, Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                recyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                break;
            default:
                break;
        }

        return true;
    }
    // Methods Not Used
    public void getCategoryList() {
        Call<List<CategoryResponse>> callCategories = apiService.getBlogCategories();
        callCategories.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                //Log.d("Ressponse:", response.headers().get("X-Wp-Total"));
                ConfigPostListActivity.categoryResponseList.addAll(response.body());
                invalidateOptionsMenu();
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {

            }
        });

    }

    public void getPostList() {
        Call<List<PostResponse>> callPosts = apiService.getBlogPosts();
        callPosts.enqueue(new Callback<List<PostResponse>>() {
            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response) {
                //Log.d("Ressponse:", response.headers().get("X-Wp-Total"));
                for (PostResponse posres : response.body()) {
                    //Log.d("Title Name :", posres.getTitle().getRendered());
                    //Log.d("Post Content :", posres.getContent().getRendered());
                }

            }
            @Override
            public void onFailure(Call<List<PostResponse>> call, Throwable t) {

            }
        });

    }

    public void getPostListByCategoryIdPageWise(int cat_id,int page_number) {

        Call<List<PostResponse>> callPosts = apiService.getBlogPostsByCategoryIdPageWise(cat_id,page_number);
        callPosts.enqueue(new Callback<List<PostResponse>>() {
            @Override
            public void onResponse(Call<List<PostResponse>> call, Response<List<PostResponse>> response) {
                //Log.d("Ressponse:", response.headers().get("X-Wp-Total"));
                for (PostResponse posres : response.body()) {
                    //Log.d("Title Name :", posres.getTitle().getRendered());
                    //Log.d("Post Content :", posres.getContent().getRendered());
                }

            }

            @Override
            public void onFailure(Call<List<PostResponse>> call, Throwable t) {

            }
        });

    }

    public void getPostById( int id) {

        Call<PostResponse> callPostById = apiService.getBlogPostById(id);
        callPostById.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
//                Log.d("Title Name :", response.body().getTitle().getRendered());
//                Log.d("Post Content :", response.body().getContent().getRendered());
//                Log.d("Post Featured media :", response.body().getFeatured_media() + "");
               // posttitle.setText(response.body().getTitle().getRendered());
               // postcontent.loadData(response.body().getContent().getRendered(), "text/html; charset=UTF-8", null);
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

            }
        });

    }
    public void getPostFeaturedImageUrlById(int id) {
        Call<MediaResponse> callImageUrlBId = apiService.getImageURLById(id);
        callImageUrlBId.enqueue(new Callback<MediaResponse>() {
            @Override
            public void onResponse(Call<MediaResponse> call, Response<MediaResponse> response) {
                //Log.d("Post Url Name :", response.body().getSource_url());

            }
            @Override
            public void onFailure(Call<MediaResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
