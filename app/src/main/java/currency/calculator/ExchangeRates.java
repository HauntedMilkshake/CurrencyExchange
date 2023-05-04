package currency.calculator;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ExchangeRates {
    @SerializedName("base")
    private String base;

    @SerializedName("rates")
    private Map<String, Double> rates;

    public String getBase() {
        return base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }
}
