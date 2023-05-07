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

public class UserFavouritesTable extends AppCompatActivity {
    Button back;

    MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favourites_table);

        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("username", "");

        int userID = myDatabaseHelper.getUserId(loggedInUsername);
        Cursor cursor = myDatabaseHelper.readFavourites(userID);
        FavouritesAdapter favouritesAdapter = new FavouritesAdapter(cursor, this);
        RecyclerView recyclerView = findViewById(R.id.favouritesTable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Add this line
        recyclerView.setAdapter(favouritesAdapter);

        back = findViewById(R.id.backToConverison);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserFavouritesTable.this, CurrencyCalculator.class);
                startActivity(intent);
            }
        });
    }
}