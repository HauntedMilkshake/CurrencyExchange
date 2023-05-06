package currency.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

/*
 * SignUp класът представлява активността за регистрация на потребителите.
 * Тя съдържа полета за въвеждане на потребителско име, парола и потвърждение на паролата.
 */
public class SignUp extends AppCompatActivity {
    ImageButton back;
    Button signup;
    EditText username, password, confirm_password;

    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        back = findViewById(R.id.backToMain);
        signup = findViewById(R.id.signup);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        db = new MyDatabaseHelper(this);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d("SignUp", "Button clicked");

                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String cp = confirm_password.getText().toString();

                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cp)){
                    Toast.makeText(SignUp.this, "Всички полета са задължителни!", Toast.LENGTH_LONG).show();
                } else if(pass.equals(cp)){
                    Boolean checkUser = db.checkUsername(user);
                    if(checkUser == false){
                        Boolean insert = db.insertUser(user, pass);
                        if(insert == true){
                            Toast.makeText(SignUp.this, "Успешна регистрация", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LogIn.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignUp.this, "Опитайте отново", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUp.this, "Вече съществува потребител със същото име", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUp.this, "Паролите трябва да съвпадат!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
