// Пакетът за калкулатора на валутни курсове
package currency.calculator;

// Импортиране на необходимите библиотеки
import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    // Променлива за работа с базата данни
    private MyDatabaseHelper db;

    // Методът onCreate, който се извиква при създаване на активността
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_calculator);
        db = new MyDatabaseHelper(this);
        convert_from = findViewById(R.id.convert_from);
        convert_to = findViewById(R.id.convert_to);
        convert = findViewById(R.id.convert);
        check_for_changes = findViewById(R.id.check_for_changes);
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

        // Добавяне на слушател за кликване на бутона за проверка на промени
        check_for_changes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){ checkForChanges(); }
        });

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
                // to do go to favTable and add favourites

                Intent intent = new Intent(CurrencyCalculator.this, UserTableActivity.class);
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
    }

    // Метод за извличане на конверсиите от базата данни
    public Cursor readConversions(int userId, MyDatabaseHelper database) {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.rawQuery("SELECT * FROM conversions WHERE user_id=? ORDER BY _id DESC LIMIT 1",
                new String[]{String.valueOf(userId)});
    }

    // Метод за проверка на промени във валутните курсове
    public void checkForChanges() {
        // Извличане на потребителското име от локалната база данни
        String username = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("username", "");
        int userId = db.getUserId(username);

        // Извличане на текущите детайли за конверсията
        Cursor cursor = db.readConversions(userId);
        cursor.moveToFirst();
        int convertFromIndex = cursor.getColumnIndex("conversion_from");
        int convertToIndex = cursor.getColumnIndex("conversion_to");
        int amountIndex = cursor.getColumnIndex("conversion_amount");

        String convert_from = convertFromIndex != -1? cursor.getString(convertFromIndex) : null;
        String convert_to = convertToIndex != -1 ? cursor.getString(convertToIndex) : null;
        Double localAmount = amountIndex != -1 ? cursor.getDouble(amountIndex) : null;


        // Получаване на последната конверсия
        Conversion conversion = getLastConversion(db, userId, convert_from, convert_to);
        cursor.close(); // Затваряне на курсора след използване
        if (conversion == null) {
            Toast.makeText(CurrencyCalculator.this, "Няма предишна конверсия от този тип", Toast.LENGTH_SHORT).show();
        } else {

            // Извличане на текущия валутен курс от сървъра
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
                                    .setTitle("Конверсионният курс е същият")
                                    .setMessage("Конверсионният курс за " + convert_from + " към " + convert_to + " е променен от " + localAmount + " на " + serverRate + ". Какво бихте искали да направите?")
                                    .setPositiveButton("Запазете стария курс", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Потребителят избра да запази стария курс, така че не правим нищо.
                                        }
                                    })
                                    .setNeutralButton("Обновете с новия курс", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Потребителят избра да обнови текущия запис с новия курс
                                            db.updateConversions(userId, convert_from, convert_to, serverRate);
                                        }
                                    })
                                    .setNegativeButton("Дублирайте и актуализирайте", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Потребителят избра да дублира записа и да актуализира дубликата с новия курс
                                            db.insertConversion(userId, convert_from, convert_to, serverRate);
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(CurrencyCalculator.this, "Стойностите са еднакви", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onFailure(Call<ExchangeRates> call, Throwable t) {
                    Log.e(TAG, "Грешка при извличане на валутните курсове: " + t.getMessage());
                }
            });
        }
    }
    /*
     * Връща последната конверсия на валута за потребител, базирано на userId, convert_from и convert_to.
     */
    public Conversion getLastConversion(MyDatabaseHelper myDatabaseHelper, int userId, String convert_from, String convert_to) {
        // Отваря четлива връзка с базата данни
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        // Изпълнява заявка към базата данни, за да получи последната конверсия за потребителя
        Cursor cursor = db.rawQuery("SELECT * FROM conversions WHERE user_id=? AND conversion_from=? AND conversion_to=? ORDER BY _id DESC LIMIT 1",
                new String[]{String.valueOf(userId), convert_from, convert_to});

        // Проверява дали курсорът има поне един резултат и се позиционира на първия резултат
        if (cursor != null && cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex("_id");
            int amountColumnIndex = cursor.getColumnIndex("conversion_amount");

            // Ако има данни в колоните, създава нов обект Conversion с получените данни
            if (idColumnIndex != -1 && amountColumnIndex != -1) {
                int id = cursor.getInt(idColumnIndex);
                String amount = cursor.getString(amountColumnIndex);
                cursor.close();
                return new Conversion(id, userId, convert_from, convert_to, amount);
            }
        }

        // Затваря курсора, ако е отворен
        if(cursor != null){
            cursor.close();
        }

        // Връща null, ако не е намерена последна конверсия
        return null;
    }



    /*
     * Извършва конверсия на валута, като използва избраните валути от потребителя и въведената сума.
     */
    private void performConversion() {
        // Извлича избраните валути от спинерите
        String currency1 = convert_from.getSelectedItem().toString();
        String currency2 = convert_to.getSelectedItem().toString();

        // Връзка към полетата за въвеждане на сумите
        EditText toConvert = findViewById(R.id.convert_from_ammount);
        EditText converted = findViewById(R.id.convert_to_ammount);

        // Проверява дали избраните валути са различни
        if (currency1.equals(currency2)) {
            Toast.makeText(this, "Моля, изберете различни валути.", Toast.LENGTH_SHORT).show();
            return;
        }

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

                    double rate1 = rates.get(currency1);
                    double rate2 = rates.get(currency2);
                    double conversionRate = rate2 / rate1;

                    double inputValue;
                    try {
                        inputValue = Double.parseDouble(toConvert.getText().toString());
                    } catch (NumberFormatException e) {
                        Toast.makeText(CurrencyCalculator.this, "Моля, въведете валидно число.", Toast.LENGTH_SHORT).show();
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
