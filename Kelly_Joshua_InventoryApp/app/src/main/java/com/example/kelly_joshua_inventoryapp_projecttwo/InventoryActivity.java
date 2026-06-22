package com.example.kelly_joshua_inventoryapp_projecttwo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * InventoryActivity displays saved inventory items from SQLite.
 * Users can add items, update item quantities, delete items,
 * and open the SMS notification settings screen.
 */
public class InventoryActivity extends AppCompatActivity {

    private Button addItemButton;
    private Button smsSettingsButton;
    private LinearLayout inventoryListLayout;
    private TextView emptyInventoryText;
    private DatabaseHelper databaseHelper;

    private static final int LOW_STOCK_LIMIT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        databaseHelper = new DatabaseHelper(this);

        addItemButton = findViewById(R.id.buttonAddItem);
        smsSettingsButton = findViewById(R.id.buttonSmsSettings);
        inventoryListLayout = findViewById(R.id.inventoryListLayout);
        emptyInventoryText = findViewById(R.id.textEmptyInventory);

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        smsSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, SmsPermissionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventoryItems();
    }

    /**
     * Loads inventory records from SQLite and displays them in the grid layout.
     */
    private void loadInventoryItems() {
        inventoryListLayout.removeAllViews();

        Cursor cursor = databaseHelper.getAllInventoryItems();

        if (cursor.getCount() == 0) {
            emptyInventoryText.setVisibility(TextView.VISIBLE);
            cursor.close();
            return;
        }

        emptyInventoryText.setVisibility(TextView.GONE);

        while (cursor.moveToNext()) {
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.ITEM_ID));
            String itemName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ITEM_NAME));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.ITEM_QUANTITY));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.ITEM_STATUS));

            addInventoryRow(itemId, itemName, quantity, status);
        }

        cursor.close();
    }

    /**
     * Creates one visible row in the inventory grid.
     */
    private void addInventoryRow(int itemId, String itemName, int quantity, String status) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setPadding(8, 10, 8, 10);
        rowLayout.setBackgroundColor(0xFFFFFFFF);

        TextView itemNameText = createGridText(itemName, 2);
        TextView quantityText = createGridText(String.valueOf(quantity), 1);
        TextView statusText = createGridText(status, 2);

        LinearLayout actionLayout = new LinearLayout(this);
        actionLayout.setOrientation(LinearLayout.HORIZONTAL);
        actionLayout.setGravity(Gravity.CENTER);
        actionLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2
        ));

        Button decreaseButton = createSmallActionButton("-");
        decreaseButton.setOnClickListener(v -> updateQuantity(itemId, quantity - 1));

        Button increaseButton = createSmallActionButton("+");
        increaseButton.setOnClickListener(v -> updateQuantity(itemId, quantity + 1));

        Button deleteButton = createSmallActionButton("X");
        deleteButton.setOnClickListener(v -> deleteItem(itemId));

        actionLayout.addView(decreaseButton);
        actionLayout.addView(increaseButton);
        actionLayout.addView(deleteButton);

        rowLayout.addView(itemNameText);
        rowLayout.addView(quantityText);
        rowLayout.addView(statusText);
        rowLayout.addView(actionLayout);

        inventoryListLayout.addView(rowLayout);
    }

    /**
     * Creates a TextView for one cell in the inventory grid.
     */
    private TextView createGridText(String text, int weight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(13);
        textView.setTextColor(0xFF222222);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                weight
        ));
        return textView;
    }

    /**
     * Creates a compact action button so update/delete buttons fit in the row.
     */
    private Button createSmallActionButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(10);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setPadding(4, 0, 4, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                44,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(2, 0, 2, 0);
        button.setLayoutParams(params);

        return button;
    }

    /**
     * Updates an item's quantity and automatically updates its stock status.
     */
    private void updateQuantity(int itemId, int newQuantity) {
        if (newQuantity < 0) {
            Toast.makeText(this, "Quantity cannot be below zero.", Toast.LENGTH_SHORT).show();
            return;
        }

        String updatedStatus = getStatusFromQuantity(newQuantity);
        boolean updated = databaseHelper.updateInventoryItem(itemId, newQuantity, updatedStatus);

        if (updated) {
            Toast.makeText(this, "Inventory item updated.", Toast.LENGTH_SHORT).show();
            loadInventoryItems();
        } else {
            Toast.makeText(this, "Unable to update item.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes an inventory item from SQLite.
     */
    private void deleteItem(int itemId) {
        boolean deleted = databaseHelper.deleteInventoryItem(itemId);

        if (deleted) {
            Toast.makeText(this, "Inventory item deleted.", Toast.LENGTH_SHORT).show();
            loadInventoryItems();
        } else {
            Toast.makeText(this, "Unable to delete item.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Determines stock status based on the current quantity.
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