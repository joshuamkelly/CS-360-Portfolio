package com.example.kelly_joshua_inventoryapp_projecttwo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity handles user login and account creation.
 * New accounts are saved to the SQLite database.
 */
public class MainActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button loginButton;
    private Button createAccountButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        loginButton = findViewById(R.id.buttonLogin);
        createAccountButton = findViewById(R.id.buttonCreateAccount);

        loginButton.setOnClickListener(v -> logInUser());
        createAccountButton.setOnClickListener(v -> createAccount());
    }

    /**
     * Checks the entered username and password against the saved database records.
     */
    private void logInUser() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean loginSuccessful = databaseHelper.checkUser(username, password);

        if (loginSuccessful) {
            Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
            openInventoryScreen();
        } else {
            Toast.makeText(this, "Account not found. Please create an account first.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves a new username and password to the database.
     */
    private void createAccount() {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean accountCreated = databaseHelper.addUser(username, password);

        if (accountCreated) {
            Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show();
            openInventoryScreen();
        } else {
            Toast.makeText(this, "That username already exists.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the inventory dashboard after login or account creation.
     */
    private void openInventoryScreen() {
        Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
        startActivity(intent);
    }
}