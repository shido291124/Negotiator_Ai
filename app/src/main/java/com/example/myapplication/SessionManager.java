package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("negotiator_prefs", Context.MODE_PRIVATE);
    }

    public void saveSlackUser(String userId, String userName) {
        prefs.edit()
                .putString("SLACK_USER_ID", userId)
                .putString("SLACK_USER_NAME", userName)
                .apply();
    }

    public boolean isLoggedIn() {
        return prefs.contains("SLACK_USER_ID");
    }

    public String getUserId() {
        return prefs.getString("SLACK_USER_ID", null);
    }

    /**
     * Helper method to get the Slack ID from an activity context.
     */
    public static String getSlackId(Context context) {
        return new SessionManager(context).getUserId();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
