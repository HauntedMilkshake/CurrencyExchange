package currency.calculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "currency_exchange.db";
    private static final int DATABASE_VERSION = 1;

    public static final String CREATE_USER_TABLE = "CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, password Text)";
    public static final String CREATE_CURRENCY_TABLE ="CREATE TABLE currencies(_id INTEGER PRIMARY KEY AUTOINCREMENT, curr_name TEXT"; //table will store currencies for the sake of the tasks
    public static final String CREATE_TRANSACTIONSORCONVERSIONS_TABLE = "CREATE TABLE transactions(_id INTEGER PRIMARY KEY AUTOINCREMENT, curr_from TEXT, curr_to TEXT, result "; //table will store conversions the user has done and will be able to be edited with the button


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public Boolean insertData(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", username);
        values.put("password", password);
        long result = db.insert("users", null, values);
        if(result == -1){
            // vrushta -1 zashtoto ima nqkakuv greshen ili prazen zapis
            return false;
        }else{
            return true;
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
    public Boolean checkUserPasssword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where name=? and password =?", new String[] {username , password});
        if(cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

}
