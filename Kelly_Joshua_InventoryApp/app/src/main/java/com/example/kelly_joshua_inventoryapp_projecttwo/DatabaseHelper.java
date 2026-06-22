package com.example.kelly_joshua_inventoryapp_projecttwo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper manages the SQLite database for user accounts and inventory items.
 * It supports login/account creation and basic inventory CRUD operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InventoryTracker.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final String TABLE_INVENTORY = "inventory";
    public static final String ITEM_ID = "id";
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_QUANTITY = "quantity";
    public static final String ITEM_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the user and inventory tables when the database is first created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME + " TEXT UNIQUE, " +
                PASSWORD + " TEXT)";

        String createInventoryTable = "CREATE TABLE " + TABLE_INVENTORY + " (" +
                ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_NAME + " TEXT, " +
                ITEM_QUANTITY + " INTEGER, " +
                ITEM_STATUS + " TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createInventoryTable);
    }

    /**
     * Recreates the database tables if the database version changes.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        onCreate(db);
    }

    /**
     * Adds a new user account to the users table.
     */
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    /**
     * Checks whether the entered username and password match a saved account.
     */
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                null,
                USERNAME + "=? AND " + PASSWORD + "=?",
                new String[]{username, password},
                null,
                null,
                null
        );

        boolean userExists = cursor.getCount() > 0;
        cursor.close();

        return userExists;
    }

    /**
     * Adds a new inventory item to the database.
     */
    public boolean addInventoryItem(String itemName, int quantity, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, itemName);
        values.put(ITEM_QUANTITY, quantity);
        values.put(ITEM_STATUS, status);

        long result = db.insert(TABLE_INVENTORY, null, values);
        return result != -1;
    }

    /**
     * Returns all inventory records so they can be displayed on the screen.
     */
    public Cursor getAllInventoryItems() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_INVENTORY,
                null,
                null,
                null,
                null,
                null,
                ITEM_ID + " DESC"
        );
    }

    /**
     * Updates the quantity and status for a specific inventory item.
     */
    public boolean updateInventoryItem(int itemId, int quantity, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_QUANTITY, quantity);
        values.put(ITEM_STATUS, status);

        int rowsUpdated = db.update(
                TABLE_INVENTORY,
                values,
                ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)}
        );

        return rowsUpdated > 0;
    }

    /**
     * Deletes a specific inventory item from the database.
     */
    public boolean deleteInventoryItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsDeleted = db.delete(
                TABLE_INVENTORY,
                ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)}
        );

        return rowsDeleted > 0;
    }
}