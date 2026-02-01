package com.example.myapplication.ui.preferences;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.SessionManager;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class PreferencesActivity extends AppCompatActivity {

    private static final String TAG = "PreferencesActivity";
    private Spinner timeSpinner, durationSpinner, flexibilitySpinner;
    private CheckBox mon, tue, wed, thu, fri, sat, sun;
    private Button btnSave;

    private final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        timeSpinner = findViewById(R.id.spinnerTime);
        durationSpinner = findViewById(R.id.spinnerDuration);
        flexibilitySpinner = findViewById(R.id.spinnerFlexibility);

        mon = findViewById(R.id.chkMon);
        tue = findViewById(R.id.chkTue);
        wed = findViewById(R.id.chkWed);
        thu = findViewById(R.id.chkThu);
        fri = findViewById(R.id.chkFri);
        sat = findViewById(R.id.chkSat);
        sun = findViewById(R.id.chkSun);

        btnSave = findViewById(R.id.btnSavePreferences);
        btnSave.setOnClickListener(v -> savePreferences());
    }

    private void savePreferences() {
        try {
            JSONArray avoided = new JSONArray();
            if (mon.isChecked()) avoided.put("Monday");
            if (tue.isChecked()) avoided.put("Tuesday");
            if (wed.isChecked()) avoided.put("Wednesday");
            if (thu.isChecked()) avoided.put("Thursday");
            if (fri.isChecked()) avoided.put("Friday");
            if (sat.isChecked()) avoided.put("Saturday");
            if (sun.isChecked()) avoided.put("Sunday");

            String durationStr = durationSpinner.getSelectedItem().toString().replaceAll("[^0-9]", "");
            int duration = durationStr.isEmpty() ? 30 : Integer.parseInt(durationStr);

            JSONObject preferences = new JSONObject();
            preferences.put("preferred_time_of_day", timeSpinner.getSelectedItem().toString());
            preferences.put("avoided_days", avoided);
            preferences.put("max_duration", duration);
            preferences.put("flexibility", flexibilitySpinner.getSelectedItem().toString());

            String slackId = SessionManager.getSlackId(this);
            if (slackId == null) {
                Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject payload = new JSONObject();
            payload.put("slack_id", slackId);
            payload.put("preferences", preferences);
            payload.put("availability", new JSONArray());

            RequestBody body = RequestBody.create(payload.toString(), JSON_TYPE);

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:8000/users/profile")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network Error: " + e.getMessage());
                    runOnUiThread(() ->
                            Toast.makeText(PreferencesActivity.this, "Server connection failed", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(PreferencesActivity.this, "Preferences saved to Orchestra AI", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        Log.e(TAG, "Server Error: " + response.code());
                        runOnUiThread(() ->
                                Toast.makeText(PreferencesActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        );
                    }
                    response.close();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
