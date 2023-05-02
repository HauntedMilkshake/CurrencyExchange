package currency.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import java.security.Signature;

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
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
            }
        });


    }
//    public void signup(){
//        Intent i = new Intent(MainActivity.this, SignUp.class);
//        startActivity(i);
//    }
//
//    public void login(){
//        Intent i = new Intent(MainActivity.this, LogIn.class);
//        startActivity(i);
//    }
}