package com.blog.app.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.blog.app.activity.BlogPostActivity;
import com.blog.app.activity.BlogPostListActivity;
import com.blog.app.config.NotificationConfig;
import com.blog.app.model.NotificationRegRequest;
import com.blog.app.model.NotificationRegResponse;
import com.blog.app.rest.ApiClient;
import com.blog.app.rest.ApiInterface;
import com.blog.app.utils.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKEN", s);

        // Saving reg id to shared preferences
        storeRegIdInPref(s);

        // sending reg id to your server
        sendRegistrationToServer(s);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(NotificationConfig.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", s);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) { // validate nullable

            Map<String, String> data = remoteMessage.getData();
            try {
                String title    = data.get("title");
                String content  = data.get("content");
                String pid = data.get("post_id");

                Log.d(TAG, "Title: " + title);
                Log.d(TAG, "Content: " + content);
                Log.d(TAG, "Post Id: " + pid);

                if(Integer.parseInt(pid) > 0){

                    handleDataMessageWithPostId(Integer.parseInt(pid),title,content);
                }
                else {

                    handleDataMessageWithoutPostId(title,content);
                }

            }
            catch (Exception e){
                Log.e(TAG,e.getMessage());
                String title    = data.get("title");
                String content  = data.get("content");

                Log.e(TAG, "Title: " + title);
                Log.e(TAG, "Content: " + content);

                handleDataMessageWithoutPostId(title,content);

            }

        }


    }


    //handle data message without postid
    private void handleDataMessageWithoutPostId(String title,String message) {

        try {
            Intent resultIntent = new Intent(getApplicationContext(), BlogPostListActivity.class);
            resultIntent.putExtra("page_no", 1);

            showNotificationMessageWithoutPostId(getApplicationContext(), title, message, resultIntent);


        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification without post id
     */
    private void showNotificationMessageWithoutPostId(Context context, String title, String message,Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessageWithoutPostId(title, message,intent);
    }






    //handle data message with postid

    private void handleDataMessageWithPostId(int post_id,String title,String message) {

        try {
            Intent resultIntent = new Intent(getApplicationContext(), BlogPostActivity.class);
            resultIntent.putExtra("message", message);
            resultIntent.putExtra("postid",post_id);
            showNotificationMessageWithPostID(getApplicationContext(), title, message, post_id, resultIntent);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification when the post id is valid and present
     */
    private void showNotificationMessageWithPostID(Context context, String title, String message, int post_id, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessageWithPostId(title, message, post_id, intent);
        
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        NotificationRegRequest objNotificationRegRequest = new NotificationRegRequest();
        objNotificationRegRequest.regid = token;
        objNotificationRegRequest.serial = Build.SERIAL;
        objNotificationRegRequest.device_name = Build.MODEL;
        objNotificationRegRequest.os_version = Build.VERSION.RELEASE;

        //make registration request

        Call<NotificationRegResponse> regResponseCall = apiService.getNotificationRegistrationResponse(objNotificationRegRequest);
        regResponseCall.enqueue(new Callback<NotificationRegResponse>() {
            @Override
            public void onResponse(Call<NotificationRegResponse> call, Response<NotificationRegResponse> response) {
                String status = response.body().getStatus();
                String message = response.body().getMessage();

                Log.d(TAG, "Registration Status: " + status);
                Log.d(TAG, "Registration Messge: " + message);

            }

            @Override
            public void onFailure(Call<NotificationRegResponse> call, Throwable t) {

            }
        });


    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(NotificationConfig.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }




}
