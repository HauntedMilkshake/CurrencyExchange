package currency.calculator;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/*
 * Класът ExchangeRates представлява модела за обменните курсове.
 * Включва анотации за сериализация с Gson библиотеката.
 */
public class ExchangeRates {
    @SerializedName("base")
    private String base;

    @SerializedName("rates")
    private Map<String, Double> rates;

    /*
     * Връща основната валута.
     *
     * @return Основната валута като String
     */
    public String getBase() {
        return base;
    }

    /*
     * Връща обменните курсове за всяка валута.
     *
     * @return Map с валутите и техните обменни курсове
     */
    public Map<String, Double> getRates() {
        return rates;
    }
}
