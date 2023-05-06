// Пакетът за калкулатора на валутни курсове
package currency.calculator;

// Импортиране на необходимите библиотеки
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Интерфейсът APIInterface, който ще дефинира методите за връзка с валутния API
public interface APIInterface {

    // HTTP GET заявка, която извлича най-новите валутни курсове
    @GET("latest.json")
    // Методът getExchangeRates приема един параметър - app_id, който представлява идентификатора на приложението
    // и връща обект от тип ExchangeRates
    Call<ExchangeRates> getExchangeRates(@Query("app_id") String appId);
}
