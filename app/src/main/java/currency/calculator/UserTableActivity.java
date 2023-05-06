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

public class UserTableActivity extends AppCompatActivity {
    Button back;
    MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_database);

        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("username", "");

        back = findViewById(R.id.backToConverison);

        Cursor cursor = myDatabaseHelper.getUserByUsername(loggedInUsername);
        UserAdapter userAdapter = new UserAdapter(cursor);
        RecyclerView recyclerView = findViewById(R.id.viewUserTable);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Add this line
        recyclerView.setAdapter(userAdapter);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(UserTableActivity.this, CurrencyCalculator.class);
                startActivity(intent);
            }
        });
    }

}