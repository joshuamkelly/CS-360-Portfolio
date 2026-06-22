package com.example.kelly_joshua_inventoryapp_projecttwo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SmsPermissionActivity extends AppCompatActivity {

    private Button allowButton;
    private Button denyButton;
    private TextView smsStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_permission);

        allowButton = findViewById(R.id.buttonAllowSms);
        denyButton = findViewById(R.id.buttonDenySms);
        smsStatusText = findViewById(R.id.textSmsStatus);

        allowButton.setOnClickListener(v ->
                smsStatusText.setText("SMS alerts enabled. You will receive low inventory notifications."));

        denyButton.setOnClickListener(v ->
                smsStatusText.setText("SMS alerts disabled. The app will continue to work without text notifications."));
    }
}