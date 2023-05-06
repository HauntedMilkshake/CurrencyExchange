// Пакетът за калкулатора на валутни курсове
package currency.calculator;

// Импортиране на необходимите библиотеки
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Класът APIClient, който ще използваме за връзка с валутния API
public class APIClient {

    // Основен адрес на API-то, който ще използваме
    private static final String BASE_URL = "https://openexchangerates.org/api/";
    // Статична променлива от тип Retrofit, която ще съдържа инстанция на Retrofit
    private static Retrofit retrofit = null;

    // Метод getClient, който ще връща инстанция на Retrofit
    public static Retrofit getClient() {
        // Ако инстанцията на Retrofit е null, създаваме нова инстанция
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Задаване на основния адрес на API-то
                    .addConverterFactory(GsonConverterFactory.create()) // Добавяне на конвертора за Gson
                    .build(); // Изграждане на инстанцията на Retrofit
        }
        // Връщаме инстанцията на Retrofit
        return retrofit;
    }
}
