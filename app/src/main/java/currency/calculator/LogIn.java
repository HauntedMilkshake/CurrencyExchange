package currency.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {
    ImageButton back;

    EditText username, password;

    MyDatabaseHelper db;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        back = findViewById(R.id.backToMain);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        db = new MyDatabaseHelper(this);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("LogIn", "Button clicked");

                String user = username.getText().toString();
                String pass = password.getText().toString();
                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)){
                    Toast.makeText(LogIn.this, "All fields required", Toast.LENGTH_SHORT).show();
                }else{
                    Boolean checkPassword = db.checkUserPasssword(user, pass);
                    if(checkPassword == true){
                        Toast.makeText(LogIn.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), CurrencyCalculator.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LogIn.this, "Failed login", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LogIn.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}