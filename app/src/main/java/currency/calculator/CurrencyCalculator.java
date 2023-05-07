// Пакетът за калкулатора на валутни курсове
package currency.calculator;

// Импортиране на необходимите библиотеки
import static android.content.ContentValues.TAG;

import static java.lang.Double.parseDouble;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Класът CurrencyCalculator, който наследява AppCompatActivity
public class CurrencyCalculator extends AppCompatActivity {

    // Дефиниране на променливи за интерфейса на приложението
    Spinner convert_from;
    Spinner convert_to;
    Button convert, check_for_changes, userTable, favTable, conversionTable;
    ImageButton favourite;
    boolean isClicked = false;

    // Променлива за работа с базата данни
    private MyDatabaseHelper db;

    // Методът onCreate, който се извиква при създаване на активността
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_calculator);
        favourite = findViewById(R.id.favourite);
        db = new MyDatabaseHelper(this);
        convert_from = findViewById(R.id.convert_from);
        convert_to = findViewById(R.id.convert_to);
        convert = findViewById(R.id.convert);
        userTable = findViewById(R.id.goToUserPage);
        favTable = findViewById(R.id.goToFavouritesPage);
        conversionTable = findViewById(R.id.goToConversionPage);
        // Добавяне на слушател за кликване на бутона за конвертиране
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }

        });

        // Извличане на валутните курсове
        fetchExchangeRates();

        userTable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(CurrencyCalculator.this, UserTableActivity.class);
                startActivity(intent);
            }
        });
        favTable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(CurrencyCalculator.this, UserFavouritesTable.class);
                startActivity(intent);
            }
        });
        conversionTable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //to do go to conversion table
                Intent intent = new Intent(CurrencyCalculator.this, UserConversionTable.class);
                startActivity(intent);
            }
        });
        favourite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                setFavourite();
            }
        });
    }
    private void setFavourite(){
        String conversionFrom = convert_from.getSelectedItem().toString();
        String conversionTo = convert_to.getSelectedItem().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String loggedInUsername = sharedPreferences.getString("username", "");
        int userID = db.getUserId(loggedInUsername);
        if(!isClicked){
            if(!conversionFrom.equals(conversionTo)) {
                boolean success = db.insertFavourites(userID, conversionFrom, conversionTo);

                if (success) {
                    Toast.makeText(CurrencyCalculator.this, "Conversion added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CurrencyCalculator.this, "Failed to add conversion to favorites", Toast.LENGTH_SHORT).show();
                }
                favourite.setImageResource(android.R.drawable.btn_star_big_on);
                isClicked = true;
            }else{
                Toast.makeText(CurrencyCalculator.this, "Chose different conversion to favourite :)", Toast.LENGTH_SHORT).show();

            }
        }else{
            int favID = db.getFavouritesID(conversionFrom);
            boolean success = db.deleteFavourites(favID);
            if (success) {
                Toast.makeText(CurrencyCalculator.this, "Conversion deleted from favorites", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CurrencyCalculator.this, "Failed to delete conversion from favorites", Toast.LENGTH_SHORT).show();
            }
            favourite.setImageResource(android.R.drawable.btn_star_big_off);
            isClicked = false;
        }
    }



    /*
     * Извършва конверсия на валута, като използва избраните валути от потребителя и въведената сума.
     */
    private void performConversion() {
        // Extract the selected currencies from the spinners
        String currency1 = convert_from.getSelectedItem().toString();
        String currency2 = convert_to.getSelectedItem().toString();

        // Reference the input fields for the amounts
        EditText toConvert = findViewById(R.id.convert_from_ammount);
        EditText converted = findViewById(R.id.convert_to_ammount);

        // Check if the selected currencies are different
        if (currency1.equals(currency2)) {
            Toast.makeText(this, "Моля, изберете различни валути.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an API service to fetch the exchange rates
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ExchangeRates> call = apiService.getExchangeRates("3086526c54cd4b09927cb43dcff066fe");

        // Send a request to the API to get the rates
        call.enqueue(new Callback<ExchangeRates>() {
            @Override
            public void onResponse(Call<ExchangeRates> call, Response<ExchangeRates> response) {
                if (response.isSuccessful()) {
                    ExchangeRates exchangeRates = response.body();
                    Map<String, Double> rates = exchangeRates.getRates();

                    double rate1 = rates.get(currency1);
                    double rate2 = rates.get(currency2);
                    double conversionRate = rate2 / rate1;

                    double inputValue;
                    try {
                        inputValue = parseDouble(toConvert.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(CurrencyCalculator.this, "Моля, въведете валидно число.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double convertedValue = inputValue * conversionRate;
                    converted.setText(String.format(Locale.getDefault(), "%.2f", convertedValue));

                    String username = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("username", "");
                    int userId = db.getUserId(username);

                    // Insert the initial amount and the conversion rate into the database
                    //formatting

                    boolean insertResult = db.insertConversion(userId, Double.parseDouble(String.format("%.2f", inputValue)), Double.parseDouble(String.format("%.2f", conversionRate)), currency1, currency2, Double.parseDouble(String.format("%.2f", convertedValue)));
                }
            }

            @Override
            public void onFailure(Call<ExchangeRates> call, Throwable t) {
                Log.e(TAG, "Грешка при извличане на валутните курсове: " + t.getMessage());
            }
        });
    }


    /*
     * Извлича валутните курсове от API и попълва спинерите с валутите.
     */
    private void fetchExchangeRates() {
        // Създава API услуга за извличане на валутните курсове
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ExchangeRates> call = apiService.getExchangeRates("3086526c54cd4b09927cb43dcff066fe");

        // Изпраща заявка към API за получаване на курсовете
        call.enqueue(new Callback<ExchangeRates>() {
            @Override
            public void onResponse(Call<ExchangeRates> call, Response<ExchangeRates> response) {
                if (response.isSuccessful()) {
                    ExchangeRates exchangeRates = response.body();
                    Map<String, Double> rates = exchangeRates.getRates();
                    // Попълва спинерите с валутите
                    populateSpinners(rates);
                }
            }

            @Override
            public void onFailure(Call<ExchangeRates> call, Throwable t) {
                Log.e(TAG, "Грешка при извличане на валутните курсове: " + t.getMessage());
            }
        });
    }

    /*
     * Попълва спинерите с валутите, получени от предоставения Map с валутни курсове.
     *
     * @param rates Map с валутни курсове
     */
    private void populateSpinners(Map<String, Double> rates) {
        // Създава списък с валутите от предоставените валутни курсове
        List<String> currencies = new ArrayList<>(rates.keySet());

        // Създава адаптер за спинерите със списъка с валутите
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Задава адаптерите на спинерите
        convert_from.setAdapter(adapter);
        convert_to.setAdapter(adapter);
    }

}
