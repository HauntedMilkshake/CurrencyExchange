package currency.calculator;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.widget.SimpleAdapter;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//instance of the OpenExchangeRatesApi class and call the `getExchangeRate()` method, passing in the currencies you want to convert and an instance of the `ExchangeRateListener` interface to handle the response.

public class CurrencyCalculator extends AppCompatActivity {

    Spinner convert_from;
    Spinner convert_to;
    Button convert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_calculator);
        convert_from = findViewById(R.id.convert_from);
        convert_to = findViewById(R.id.convert_to);
        convert = findViewById(R.id.convert);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               performConversion();
            }

        });
        fetchExchangeRates();
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
                }
            }

            @Override
            public void onFailure(Call<ExchangeRates> call, Throwable t) {
                Log.e(TAG, "Error fetching exchange rates: " + t.getMessage());
            }
        });


    }
}
