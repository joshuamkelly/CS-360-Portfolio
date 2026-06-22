package com.example.kelly_joshua_inventoryapp_projecttwo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * AddItemActivity allows the user to add a new inventory item to SQLite.
 */
public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private EditText itemQuantityEditText;
    private EditText itemStatusEditText;
    private Button saveItemButton;
    private Button cancelButton;
    private DatabaseHelper databaseHelper;

    private static final int LOW_STOCK_LIMIT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        databaseHelper = new DatabaseHelper(this);

        itemNameEditText = findViewById(R.id.editItemName);
        itemQuantityEditText = findViewById(R.id.editItemQuantity);
        itemStatusEditText = findViewById(R.id.editItemStatus);
        saveItemButton = findViewById(R.id.buttonSaveItem);
        cancelButton = findViewById(R.id.buttonCancelAddItem);

        saveItemButton.setOnClickListener(v -> saveInventoryItem());
        cancelButton.setOnClickListener(v -> finish());
    }

    /**
     * Validates user input and saves the new inventory item to the database.
     */
    private void saveInventoryItem() {
        String itemName = itemNameEditText.getText().toString().trim();
        String quantityText = itemQuantityEditText.getText().toString().trim();
        String status = itemStatusEditText.getText().toString().trim();

        if (itemName.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(this, "Please enter an item name and quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "Quantity must be a whole number.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity < 0) {
            Toast.makeText(this, "Quantity cannot be below zero.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (status.isEmpty()) {
            status = getStatusFromQuantity(quantity);
        }

        boolean itemAdded = databaseHelper.addInventoryItem(itemName, quantity, status);

        if (itemAdded) {
            Toast.makeText(this, "Inventory item saved.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Unable to save item.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Automatically determines a stock status if the user leaves the status field blank.
     */
    private String getStatusFromQuantity(int quantity) {
        if (quantity == 0) {
            return "Out of Stock";
        } else if (quantity <= LOW_STOCK_LIMIT) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
}