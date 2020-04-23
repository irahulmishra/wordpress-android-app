package com.blog.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blog.app.R;
import com.blog.app.config.ConfigPostActivity;
import com.blog.app.config.NotificationConfig;
import com.blog.app.model.PostResponse;
import com.blog.app.rest.ApiClient;
import com.blog.app.rest.ApiInterface;
import com.blog.app.utils.NotificationUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.support.design.widget.FloatingActionButton;

public class BlogPostActivity extends AppCompatActivity {
    private ProgressBar mProgressBar ;
    private WebView postcontent  ;
    private FloatingActionButton floatingShareButton ;
    private ApiInterface apiService ;
    private static final String TAG = BlogPostActivity.class.getSimpleName();
    private Call<PostResponse> callPostById ;
    private int post_id = 0 ;
    private String strLinkToShare ;


    Callback<PostResponse> callPostByIdCallback = new Callback<PostResponse>() {
        @Override
        public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

//            Log.e("Title Name :", response.body().getTitle().getRendered());
            //          Log.d("Post Content :", response.body().getContent().getRendered());
//            Log.e("Post Featured media :", response.body().getFeatured_media() + "");
//            Log.e("Post Link",response.body().getLink());
            strLinkToShare = response.body().getLink();

            //postcontent.loadUrl(strLinkToShare);

            postcontent.loadData(ConfigPostActivity.strHtmlStart + response.body().getTitle().getRendered() + ConfigPostActivity.strAfterHeadTag + response.body().getContent().getRendered() + ConfigPostActivity.getStrHtmlEndTag, "text/html; charset=UTF-8", "UTF-8");

            // mAdView.loadAd(adRequest);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Call<PostResponse> call, Throwable t) {
            //Log.e(TAG,t.getMessage());

        }
    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_blog_post, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT)
                        .show();
                break;

            default:
                break;
        }

        return true;
    }
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(NotificationConfig.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        //Log.e(TAG, "FireBase Reg Id: " + regId);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Log.e(TAG,"Activity On Resume");
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }
    @Override
    protected void onPause() {
        //Log.e(TAG,"Activity onn Pause");
        super.onPause();
    }
    public void getPostById(int id) {
        callPostById = apiService.getBlogPostById(id);
        callPostById.enqueue(callPostByIdCallback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post);
        strLinkToShare = "";
        apiService = ApiClient.getClient().create(ApiInterface.class);
        post_id = this.getIntent().getExtras().getInt("postid");
        mProgressBar = findViewById(R.id.progress_bar_post);
        postcontent = findViewById(R.id.blogpostcontent);
        floatingShareButton = findViewById(R.id.floatingShareButton);

        //mAdView = findViewById(R.id.adViewBlogPostHome);
        //adRequest = new AdRequest.Builder()
        //        .build();

        //postcontent.requestFocus();
        postcontent.getSettings().setJavaScriptEnabled(true);
        //postcontent.getSettings().setGeolocationEnabled(true);
        postcontent.setSoundEffectsEnabled(true);


        if (post_id > 0) {
            getPostById(post_id);
        }
        floatingShareButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, strLinkToShare);
                startActivity(Intent.createChooser(shareIntent, "Share Link "));

            }
        });

    }

}
