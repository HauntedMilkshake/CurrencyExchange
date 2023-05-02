package currency.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {

    Button signup, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                signup();
            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                login();
            }
        });

    }
    public void signup(){
        Intent i = new Intent(this, signup.getClass());
        startActivity(i);
    }

    public void login(){
        Intent i = new Intent(this, login.getClass());
        startActivity(i);
    }
}