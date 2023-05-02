package currency.calculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "currency_exchange.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable1 = "CREATE TABLE user_details (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, password TEXT)";
        String createTable2 = "CREATE TABLE table2 (id INTEGER PRIMARY KEY AUTOINCREMENT, address TEXT, phone TEXT)";
        String createTable3 = "CREATE TABLE table3 (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, password TEXT)";
        db.execSQL(createTable1);
        db.execSQL(createTable2);
        db.execSQL(createTable3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade the database tables, if necessary
        db.execSQL("DROP TABLE IF EXISTS mytable");
        onCreate(db);
    }
}
