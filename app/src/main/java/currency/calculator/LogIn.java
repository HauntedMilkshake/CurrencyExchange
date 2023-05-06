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

/**
 * LogIn класа представлява активността за вход в приложението.
 * Потребителят може да въведе потребителско име и парола, за да влезе в приложението.
 */
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

        // Обработва натискането на бутона за вход
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("LogIn", "Button clicked");

                String user = username.getText().toString();
                String pass = password.getText().toString();
                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)){
                    Toast.makeText(LogIn.this, "Всички полета са задължителни", Toast.LENGTH_SHORT).show();
                }else{
                    Boolean checkPassword = db.checkUserPassword(user, pass);
                    if(checkPassword == true){
                        Toast.makeText(LogIn.this, "Успешен вход", Toast.LENGTH_SHORT).show();
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                .putString("username", user)
                                .apply();
                        Intent intent = new Intent(getApplicationContext(), CurrencyCalculator.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LogIn.this, "Неуспешен вход", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Обработва натискането на бутона за връщане към началото
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LogIn.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
