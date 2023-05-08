package currency.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserConversionTable extends AppCompatActivity {
    Button back;

    MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_conversion_table);

        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("username", "");

        int userID = myDatabaseHelper.getUserId(loggedInUsername);
        Cursor cursor = myDatabaseHelper.getAllUserConversions(userID);
        ConversionAdapter conversionAdapter = new ConversionAdapter(this, cursor, this);
        RecyclerView recyclerView = findViewById(R.id.conversionTable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Add this line
        recyclerView.setAdapter(conversionAdapter);

        back = findViewById(R.id.backToConverison);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserConversionTable.this, CurrencyCalculator.class);
                startActivity(intent);
            }
        });
    }
}