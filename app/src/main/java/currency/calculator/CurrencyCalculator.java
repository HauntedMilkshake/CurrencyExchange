package currency.calculator;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//instance of the OpenExchangeRatesApi class and call the `getExchangeRate()` method, passing in the currencies you want to convert and an instance of the `ExchangeRateListener` interface to handle the response.

public class CurrencyCalculator extends AppCompatActivity {

    Spinner convert_from;
    Spinner convert_to;
    Button convert, check_for_changes;
    // Get the user ID from the database

    private MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_calculator);
        db = new MyDatabaseHelper(this);
        convert_from = findViewById(R.id.convert_from);
        convert_to = findViewById(R.id.convert_to);
        convert = findViewById(R.id.convert);
        check_for_changes = findViewById(R.id.check_for_changes);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               performConversion();
            }

        });
        fetchExchangeRates();
        check_for_changes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){ checkForChanges(); }
        });
    }


    /*
     String username = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("username", "");
    int userId = db.getUserId(username);
    Conversion conversion = getLastConversion(db, userId, convert_from, convert_to);
    if (conversion == null) {
        Toast.makeText(CurrencyCalculator.this, "No previous conversion of this type", Toast.LENGTH_SHORT).show();
        return
     */
    public Cursor readConversions(int userId, MyDatabaseHelper database) {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT * FROM conversions WHERE user_id=? ORDER BY _id DESC LIMIT 1",
                new String[]{String.valueOf(userId)});
    }

    public void checkForChanges() {
        // Get the item from the local database
        String username = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("username", "");
        int userId = db.getUserId(username);

        // Get current conversion details
        Cursor cursor = db.readConversions(userId);
        cursor.moveToFirst();
        int convertFromIndex = cursor.getColumnIndex("conversion_from");
        int convertToIndex = cursor.getColumnIndex("conversion_to");
        int amountIndex = cursor.getColumnIndex("conversion_amount");

        String convert_from = convertFromIndex != -1 ? cursor.getString(convertFromIndex) : null;
        String convert_to = convertToIndex != -1 ? cursor.getString(convertToIndex) : null;
        Double localAmount = amountIndex != -1 ? cursor.getDouble(amountIndex) : null;


        Conversion conversion = getLastConversion(db, userId, convert_from, convert_to);
        cursor.close(); // Closing the cursor after use
        if (conversion == null) {
            Toast.makeText(CurrencyCalculator.this, "No previous conversion of this type", Toast.LENGTH_SHORT).show();
        } else {

        // Fetch the current conversion rate from the server
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ExchangeRates> call = apiService.getExchangeRates("3086526c54cd4b09927cb43dcff066fe");

        call.enqueue(new Callback<ExchangeRates>() {
            @Override
            public void onResponse(Call<ExchangeRates> call, Response<ExchangeRates> response) {
                if (response.isSuccessful()) {
                    ExchangeRates exchangeRates = response.body();
                    Map<String, Double> rates = exchangeRates.getRates();

                    double serverRate = rates.get(convert_to) / rates.get(convert_from);
                    //!
                    if (serverRate != localAmount) {
                        new AlertDialog.Builder(CurrencyCalculator.this)
                                .setTitle("Conversion rate is the same")
                                .setMessage("The conversion rate for " + convert_from + " to " + convert_to + " has changed from " + localAmount + " to " + serverRate + ". What would you like to do?")
                                .setPositiveButton("Keep old rate", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User chose to keep the old rate, so do nothing.
                                    }
                                })
                                .setNeutralButton("Update with new rate", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User chose to update the current record with the new rate
                                        db.updateConversions(userId, convert_from, convert_to, serverRate);
                                    }
                                })
                                .setNegativeButton("Duplicate and update", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User chose to duplicate the record and update the duplicate with the new rate
                                        db.insertConversion(userId, convert_from, convert_to, serverRate);
                                    }
                                })
                                .show();
                    } else {
                        Toast.makeText(CurrencyCalculator.this, "Values are different", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ExchangeRates> call, Throwable t) {
                Log.e(TAG, "Error fetching exchange rates: " + t.getMessage());
            }
        });
        }
    }
    public Conversion getLastConversion(MyDatabaseHelper myDatabaseHelper, int userId, String convert_from, String convert_to) {
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM conversions WHERE user_id=? AND conversion_from=? AND conversion_to=? ORDER BY _id DESC LIMIT 1",
                new String[]{String.valueOf(userId), convert_from, convert_to});
        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex("_id");
            int amountColumnIndex = cursor.getColumnIndex("conversion_amount");
            if (idColumnIndex != -1 && amountColumnIndex != -1) {
                int id = cursor.getInt(idColumnIndex);
                String amount = cursor.getString(amountColumnIndex);
                cursor.close();
                return new Conversion(id, userId, convert_from, convert_to, amount);
            }
        }
        if(cursor != null){
            cursor.close();
        }
        return null;
    }

    private void performConversion() {
        String currency1 = convert_from.getSelectedItem().toString();
        String currency2 = convert_to.getSelectedItem().toString();
        EditText toConvert = findViewById(R.id.convert_from_ammount);
        EditText converted = findViewById(R.id.convert_to_ammount);
        if (currency1.equals(currency2)) {
            Toast.makeText(this, "Please select different currencies.", Toast.LENGTH_SHORT).show();
            return;
        }

        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ExchangeRates> call = apiService.getExchangeRates("3086526c54cd4b09927cb43dcff066fe");

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
                        inputValue = Double.parseDouble(toConvert.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(CurrencyCalculator.this, "Please enter a valid number.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double convertedValue = inputValue * conversionRate;
                    converted.setText(String.format(Locale.getDefault(), "%.2f", convertedValue));

                    String username = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("username", "");
                    int userId = db.getUserId(username);

                    boolean insertResult = db.insertConversion(userId, currency1, currency2, convertedValue);

                }
            }

            @Override
            public void onFailure(Call<ExchangeRates> call, Throwable t) {
                Log.e(TAG, "Error fetching exchange rates: " + t.getMessage());
            }
        });



    }
    private void fetchExchangeRates() {
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ExchangeRates> call = apiService.getExchangeRates("3086526c54cd4b09927cb43dcff066fe");

        call.enqueue(new Callback<ExchangeRates>() {
            @Override
            public void onResponse(Call<ExchangeRates> call, Response<ExchangeRates> response) {
                if (response.isSuccessful()) {
                    ExchangeRates exchangeRates = response.body();
                    Map<String, Double> rates = exchangeRates.getRates();
                    populateSpinners(rates);
                }
            }

            @Override
            public void onFailure(Call<ExchangeRates> call, Throwable t) {
                Log.e(TAG, "Error fetching exchange rates: " + t.getMessage());
            }
        });
    }
    private void populateSpinners(Map<String, Double> rates) {
        List<String> currencies = new ArrayList<>(rates.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        convert_from.setAdapter(adapter);
        convert_to.setAdapter(adapter);
    }
}
