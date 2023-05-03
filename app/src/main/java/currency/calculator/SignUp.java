package currency.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
                    Toast.makeText(SignUp.this, "All fields required!", Toast.LENGTH_LONG).show();
                }else if(pass.equals(cp)){
                    Boolean checkuser = db.checkUsername(user);
                    if(checkuser == false){
                        Boolean insert = db.insertData(user, pass);
                        if(insert == true){
                            Toast.makeText(SignUp.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LogIn.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(SignUp.this, "Try again", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SignUp.this, "There is a user with the same name", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignUp.this, "passwords must match!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Gets the data repository in write mode
        //SQLiteDatabase db = MyDatabaseHelper;
                //.getWritableDatabase();

        ContentValues values = new ContentValues();
        //
        //values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        //values.put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        //long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
    }
}