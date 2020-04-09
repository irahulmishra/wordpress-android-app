package com.blog.app.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.blog.app.config.NotificationConfig;
import com.blog.app.model.NotificationRegRequest;
import com.blog.app.model.NotificationRegResponse;
import com.blog.app.rest.ApiClient;
import com.blog.app.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    private ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(NotificationConfig.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        NotificationRegRequest objNotificationRegRequest = new NotificationRegRequest();
        objNotificationRegRequest.regid = token ;
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

                Log.e(TAG, "Registration Status: " + status);
                Log.e(TAG, "Registration Messge: " + message);

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

