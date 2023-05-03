package currency.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;
//instance of the OpenExchangeRatesApi class and call the `getExchangeRate()` method, passing in the currencies you want to convert and an instance of the `ExchangeRateListener` interface to handle the response.

public class CurrencyCalculator extends AppCompatActivity {
    Spinner convert_from = findViewById(R.id.convert_from);
    Spinner convert_to = findViewById(R.id.convert_to);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_calculator);
    }
}