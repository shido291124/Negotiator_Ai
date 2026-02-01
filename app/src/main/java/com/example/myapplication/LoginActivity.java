package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSlackLogin = findViewById(R.id.btnSlackLogin);
        btnSlackLogin.setOnClickListener(v -> {
            // Mocking Slack OAuth for now or redirecting to URL
            // In a real app, you'd trigger the OAuth flow here
            // For demonstration, let's just "log in"
            sessionManager.saveSlackUser("U12345", "Test User");
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });
        
        // Handle OAuth Redirect if applicable
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri data = intent.getData();
            if ("negotiator".equals(data.getScheme()) && "slack-auth".equals(data.getHost())) {
                String code = data.getQueryParameter("code");
                if (code != null) {
                    // Exchange code for token and save user
                    new SessionManager(this).saveSlackUser("U_FROM_OAUTH", "OAuth User");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            }
        }
    }
}
