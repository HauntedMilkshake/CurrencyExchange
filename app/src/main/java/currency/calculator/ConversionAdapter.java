package currency.calculator;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Дефиниране на клас, който разширява RecyclerView.Adapter за управление на списък от конверсии
public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ViewHolder> {
    // Частни променливи за Cursor, ImageButton компонентите и контекста на приложението
    private Cursor cursor;
    private ImageButton delete, itemView, checkForChanges;
    private Context context;
    private MyDatabaseHelper db;

    // Конструктор, който получава контекста и Cursor
    public ConversionAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.db = new MyDatabaseHelper(context);
    }

    // Създаване на нов обект ViewHolder чрез надуване на макета на изгледа
    @NonNull
    @Override
    public ConversionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversion_table, parent, false);
        return new ConversionAdapter.ViewHolder(view);
    }

    // Поддържане на изгледа на RecyclerView като зарежда данните в обекта ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ConversionAdapter.ViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            // Извличане на данните за конверсията от Cursor
            String initialAmount = cursor.getString(cursor.getColumnIndex("initial_amount"));
            String conversionRate = cursor.getString(cursor.getColumnIndex("conversion_rate"));
            String conversionFrom = cursor.getString(cursor.getColumnIndex("conversion_from"));
            String conversionTo = cursor.getString(cursor.getColumnIndex("conversion_to"));
            String conversionAmount = cursor.getString(cursor.getColumnIndex("conversion_amount"));

            // Задаване на текст в отделните изгледи на ViewHolder-а
            holder.initialAmountView.setText(initialAmount);
            holder.conversionRateView.setText(conversionRate);
            holder.conversionFromView.setText(conversionFrom);
            holder.conversionToView.setText(conversionTo);
            holder.conversionAmountView.setText(conversionAmount);

            // Намиране на ImageButton компонентите на изгледа чрез findViewById
            delete = holder.itemView.findViewById(R.id.delete_conversion);
            itemView = holder.itemView.findViewById(R.id.editButton);
            checkForChanges = holder.itemView.findViewById(R.id.checkForChangesButton);

            // Създаване на слушател за натискане на бутона "delete"
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Извличане на ID на записа, който трябва да се изтрие от базата данни, чрез "cursor"
                    int conversionId = cursor.getInt(cursor.getColumnIndex("_id"));
                    // Създаване на инстанция на помощник на базата данни
                    MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);

                    // Изтриване на записа от базата данни и запазване на резултата в "success"
                    boolean success = myDatabaseHelper.deleteConversions(conversionId);

                    if (success) {
                        // Показване на "Toast" със съобщение, че записът е изтрит успешно
                        Toast.makeText(context, "Конверсия изтрита", Toast.LENGTH_SHORT).show();

                        // Извличане на името на влезнал потребител от споделени предпочитания
                        SharedPreferences sharedPreferences = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                        String loggedInUsername = sharedPreferences.getString("username", "");

                        // Извличане на ID на потребителя от базата данни
                        int userID = myDatabaseHelper.getUserId(loggedInUsername);

                        // Извличане на всички записи на потребителя от базата данни и задаване на новия "Cursor"
                        Cursor newCursor = myDatabaseHelper.getAllUserConversions(userID);
                        swapCursor(newCursor);
                        notifyDataSetChanged();
                    } else {
                        // Показване на "Toast" със съобщение, че записът не може да бъде изтрит
                        Toast.makeText(context, "Не успяхте да изтриете конверсия", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int conversionId = cursor.getInt(cursor.getColumnIndex("_id"));

                    // Разширяване на прозореца за редактиране на конверсията и настройка на неговите елементи
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View editDialogView = inflater.inflate(R.layout.edit_conversions, null);
                    EditText editInitialAmount = editDialogView.findViewById(R.id.edit_initial_amount);
                    EditText editConversionRate = editDialogView.findViewById(R.id.edit_conversion_rate);
                    EditText editConversionFrom = editDialogView.findViewById(R.id.edit_conversion_from);
                    EditText editConversionTo = editDialogView.findViewById(R.id.edit_conversion_to);
                    EditText editConversionAmount = editDialogView.findViewById(R.id.edit_conversion_amount);

                    String initial_amount = cursor.getString(cursor.getColumnIndex("initial_amount"));
                    String conversion_rate = cursor.getString(cursor.getColumnIndex("conversion_rate"));
                    String conversion_From = cursor.getString(cursor.getColumnIndex("conversion_from"));
                    String conversion_To = cursor.getString(cursor.getColumnIndex("conversion_to"));
                    String conversion_Amount = cursor.getString(cursor.getColumnIndex("conversion_amount"));

                    editInitialAmount.setText(initial_amount);
                    editConversionRate.setText(conversion_rate);
                    editConversionFrom.setText(conversion_From);
                    editConversionTo.setText(conversion_To);
                    editConversionAmount.setText(conversion_Amount);

                    // Показване на прозореца за редактиране на конверсията
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(editDialogView);
                    builder.setTitle("Редактирай конверсия");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Запази", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String updatedInitialAmount = editInitialAmount.getText().toString();
                            String updatedConversionRate = editConversionRate.getText().toString();
                            String updatedConversionFrom = editConversionFrom.getText().toString();
                            String updatedConversionTo = editConversionTo.getText().toString();
                            String updatedConversionAmount = editConversionAmount.getText().toString();

                            MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
                            SharedPreferences sharedPreferences = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE);
                            String loggedInUsername = sharedPreferences.getString("username", "");
                            int userId = myDatabaseHelper.getUserId(loggedInUsername);

                            boolean success = myDatabaseHelper.updateConversions(conversionId,
                                    Double.parseDouble(String.format("%.2f", Double.parseDouble(updatedInitialAmount))),
                                    Double.parseDouble(updatedConversionRate),
                                    userId, updatedConversionFrom, updatedConversionTo,
                                    Double.parseDouble(String.format("%.2f", Double.parseDouble(updatedConversionAmount))));

                            if (success) {
                                Toast.makeText(context, "Конверсията е актуализирана", Toast.LENGTH_SHORT).show();
                                // Обновяване на списъка
                                Cursor newCursor = myDatabaseHelper.readConversions(userId);
                                swapCursor(newCursor);
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "Актуализирането на конверсията не успя", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Отказ", null);
                    builder.create().show();
                }
            });


            checkForChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkForChanges(context, ConversionAdapter.this);
                }
            });
        }
    }

    // Метод за проверка на промени във валутните курсове
    public void checkForChanges(Context context, ConversionAdapter adapter) {
        // Извличане на потребителското име от локалната база данни
        String username = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("username", "");
        int userId = db.getUserId(username);

        // Извличане на текущите детайли за конверсията
        Cursor cursor = db.readConversions(userId);
        cursor.moveToFirst();
        int initialAmountIndex = cursor.getColumnIndex("initial_amount");
        int convertRateIndex = cursor.getColumnIndex("conversion_rate");
        int convertFromIndex = cursor.getColumnIndex("conversion_from");
        int convertToIndex = cursor.getColumnIndex("conversion_to");
        int amountIndex = cursor.getColumnIndex("conversion_amount");
        int convertID = cursor.getColumnIndex("_id");

        Double initial_amount = initialAmountIndex != -1 ? cursor.getDouble(initialAmountIndex) : null;
        Double convert_rate = convertRateIndex != -1 ? cursor.getDouble(convertRateIndex) : null;
        String convert_from = convertFromIndex != -1 ? cursor.getString(convertFromIndex) : null;
        String convert_to = convertToIndex != -1 ? cursor.getString(convertToIndex) : null;
        Double convert_amount = amountIndex !=-1 ? cursor.getDouble(amountIndex) : null;
        Double localRate = amountIndex != -1 ? cursor.getDouble(convertRateIndex) : null;

        final Double final_initial_amount = Double.parseDouble(String.format("%.2f", initial_amount));
        convert_rate = Double.parseDouble(String.format("%.2f", convert_rate));
        convert_amount = Double.parseDouble(String.format("%.2f", convert_amount));

        // Получаване на последната конверсия
        Conversion conversion = getLastConversion(db, userId, convert_from, convert_to);
        cursor.close(); // Затваряне на курсора след използване
        if (conversion == null) {
            Toast.makeText(context, "Няма предишна конверсия от този тип", Toast.LENGTH_SHORT).show();
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
                        serverRate = Double.parseDouble(String.format("%.2f", serverRate));
                        //!= така трябва да са по задание, за да са различни
                        //== така трябва да са ако искате да ги покажете

                        if (serverRate != localRate) {
                            final Double final_serverRate = serverRate;
                            new AlertDialog.Builder(context)
                                    .setTitle("Конверсионният курс не е същият")
                                    .setMessage("Конверсионният курс за " + convert_from + " към " + convert_to + " е променен от " + localRate + " на " + serverRate + ". Какво бихте искали да направите?")
                                    .setPositiveButton("Запазете стария курс", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Потребителят избра да запази стария курс, така че не правим нищо.
                                            ((UserConversionTable) context).recreate();                                        }
                                    })
                                    .setNeutralButton("Обновете с новия курс", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Потребителят избра да обнови текущия запис с новия курс
                                            //db.updateConversions(userId, convert_from, convert_to, serverRate);\
                                            double newRate = Double.parseDouble(String.format("%.2f", final_initial_amount*final_serverRate));
                                            db.updateConversions(convertID, final_initial_amount, final_serverRate, userId, convert_from, convert_to, newRate);
                                            ((UserConversionTable) context).recreate();                                        }
                                    })
                                    .setNegativeButton("Дублирайте и актуализирайте", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Потребителят избра да дублира записа и да актуализира дубликата с новия курс
                                            double newRate = Double.parseDouble(String.format("%.2f", final_initial_amount*final_serverRate));
                                            db.insertConversion(userId, final_initial_amount, final_serverRate, convert_from, convert_to, newRate);
                                            ((UserConversionTable) context).recreate();                                        }
                                    })
                                    .show();


                        } else {
                            Toast.makeText(context, "Стойностите са еднакви", Toast.LENGTH_SHORT).show();

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
        if (cursor != null) {
            cursor.close();
        }

        // Връща null, ако не е намерена последна конверсия
        return null;
    }


    // Метод, който връща броя на елементите в списъка
// Ако Cursor е null, връща 0, в противен случай връща броя на редовете в Cursor
    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    // Метод, който замества текущия Cursor с нов Cursor и известява адаптера за промяната на данните
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    // Клас, който представлява ViewHolder и съдържа отделните изгледи на RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView initialAmountView, conversionRateView, conversionFromView, conversionToView, conversionAmountView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initialAmountView = itemView.findViewById(R.id.initial_amount);
            conversionRateView = itemView.findViewById(R.id.conversion_rate);
            conversionFromView = itemView.findViewById(R.id.conversion_from);
            conversionToView = itemView.findViewById(R.id.conversion_to);
            conversionAmountView = itemView.findViewById(R.id.conversion_amount);
        }
    }
}
