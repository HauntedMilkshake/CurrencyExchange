package currency.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SignUp extends AppCompatActivity {
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        back = findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Gets the data repository in write mode
        SQLiteDatabase db = MyDatabaseHelper;
                //.getWritableDatabase();

        ContentValues values = new ContentValues();
        //
        //values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        //values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        //long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
    }
}