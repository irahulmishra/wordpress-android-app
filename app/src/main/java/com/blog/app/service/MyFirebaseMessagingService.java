package com.blog.app.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.blog.app.activity.BlogPostActivity;
import com.blog.app.activity.BlogPostListActivity;
import com.blog.app.utils.NotificationUtils;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getData().size() > 0) { // validate nullable

            Map<String, String> data = remoteMessage.getData();
            try {
                String title    = data.get("title");
                String content  = data.get("content");
                String pid = data.get("post_id");

                Log.e(TAG, "Title: " + title);
                Log.e(TAG, "Content: " + content);
                Log.e(TAG, "Post Id: " + pid);

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





}
