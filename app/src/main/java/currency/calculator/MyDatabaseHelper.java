package currency.calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "currency_exchange.db";
    private static final int DATABASE_VERSION = 1;
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE users (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "password TEXT" +
                ")";
    public static final String CREATE_CONVERSION_TABLE =
            "CREATE TABLE conversions (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "conversion_from TEXT, " +
                    "conversion_to TEXT, " +
                    "conversion_amount REAL, " +
                    "FOREIGN KEY(user_id) REFERENCES users(_id)" +
                    ")";
    public static final String CREATE_FAVOURITES_TABLE =
            "CREATE TABLE favourites (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "from_currency_fav TEXT, " +
                    "to_currency_fav TEXT " +
                    ")";


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONVERSION_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_FAVOURITES_TABLE);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS conversions");
        db.execSQL("DROP TABLE IF EXISTS favourites");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    //CRUD USER
    public boolean insertUser(String name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("password", password);
        long result = db.insert("users", null, contentValues);
        return result != -1;
    }

    public Cursor readUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE _id=?", new String[]{String.valueOf(id)});
    }

    public boolean updateUser(int id, String name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("password", password);
        int result = db.update("users", contentValues, "_id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("users", "_id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id FROM users WHERE name=?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        } else {
            return -1;  // return -1 if the user was not found
        }
    }

    public Boolean checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where name =?", new String[] {username});
        if(cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }
    public Boolean checkUserPassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where name=? and password =?", new String[] {username , password});
        if(cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }
    //CRUD CONVERSIONS
    public boolean insertConversion(int userID, String convert_from, String convert_to, double conversion_amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userID);
        contentValues.put("conversion_from", convert_from);
        contentValues.put("conversion_to", convert_to);
        contentValues.put("conversion_amount", conversion_amount);
        long result = db.insert("conversions", null, contentValues);
        return result != -1;
    }

    public Cursor readConversions(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM conversions WHERE _id=?", new String[]{String.valueOf(id)});
    }

    public boolean updateConversions(int userID, String convert_from, String convert_to, double conversion_amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("user_id", userID);
        contentValues.put("conversion_from", convert_from);
        contentValues.put("conversion_to", convert_to);
        contentValues.put("conversion_amount", conversion_amount);
        int result = db.update("conversions", contentValues, "_id=?", new String[]{String.valueOf(userID)});
        return result > 0;
    }

    public boolean deleteConversions(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("conversions", "_id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
    // CRUD FAVOURITES
    public boolean insertFavourites(String from, String to) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("from_currency_fav", from);
        contentValues.put("to_currency_fav", to);
        long result = db.insert("favourites", null, contentValues);
        return result != -1;
    }

    public Cursor readFavourites(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM favourites WHERE _id=?", new String[]{String.valueOf(id)});
    }

    public boolean updateFavourites(int id, String from, String to) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("from_currency_fav", from);
        contentValues.put("to_currency_fav", to);
        int result = db.update("favourites", contentValues, "_id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deleteFavourites(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("favourites", "_id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}
