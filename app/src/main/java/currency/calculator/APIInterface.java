package currency.calculator;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("latest.json")
    Call<ExchangeRates> getExchangeRates(@Query("app_id") String appId);
}