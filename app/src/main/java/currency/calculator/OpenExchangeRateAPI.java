package currency.calculator;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class OpenExchangeRateAPI {
    private static final String BASE_URL = "https://openexchangerates.org/api/";
    private static final String APP_ID = "3086526c54cd4b09927cb43dcff066fe";

    public static void getExchangeRate(final String fromCurrency, final String toCurrency, final ExchangeRateListener listener) {
        String url = BASE_URL + "latest.json?app_id=" + APP_ID + "&symbols=" + fromCurrency + "," + toCurrency;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject ratesObject = jsonObject.getJSONObject("rates");
                    double fromRate = ratesObject.getDouble(fromCurrency);
                    double toRate = ratesObject.getDouble(toCurrency);
                    double exchangeRate = toRate / fromRate;
                    listener.onExchangeRateReceived(exchangeRate);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onExchangeRateError(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onExchangeRateError(error.getMessage());
            }
        });

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public interface ExchangeRateListener {
        void onExchangeRateReceived(double exchangeRate);
        void onExchangeRateError(String errorMessage);
    }
 }

