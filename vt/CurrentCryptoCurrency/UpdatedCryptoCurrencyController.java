/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.CurrentCryptoCurrency;

import edu.vt.EntityBeans.CryptoCurrency;
import edu.vt.FacadeBeans.CryptoCurrencyFacade;
import edu.vt.controllers.CryptoCurrencyController;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;
import org.primefaces.util.LangUtils;

@Named("updatedCryptoCurrencyController")
@SessionScoped
public class UpdatedCryptoCurrencyController implements Serializable {
    @Inject
    private CryptoCurrencyController cryptoCurrencyController;

    public String fillDatabase() {
        String get50Latest = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        get50Latest = get50Latest + "?CMC_PRO_API_KEY=" + Constants.COIN_MARKET_CAP_API + "&start=1&limit=50";
        try {
            String get50LatestResults = Methods.readUrlContent(get50Latest);

            JSONObject resultsJsonObject = new JSONObject(get50LatestResults);

            JSONArray jsonArray50Currencies = resultsJsonObject.getJSONArray("data");

            ArrayList<Integer> coinIDs = new ArrayList<Integer>();

            int index = 0;

            while (jsonArray50Currencies.length() > index) {
                JSONObject jsonObject = jsonArray50Currencies.getJSONObject(index);
                Integer coinID = jsonObject.optInt("id");
                coinIDs.add(coinID);
                index++;
            }

            String getMetaData = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/info";
            getMetaData = getMetaData + "?CMC_PRO_API_KEY=" + Constants.COIN_MARKET_CAP_API + "&id=";
            for (Integer coin: coinIDs) {
                getMetaData = getMetaData + coin.toString() + ",";
            }
            getMetaData = getMetaData.substring(0, getMetaData.length() - 1);
            String getSpecificData = Methods.readUrlContent(getMetaData);
            JSONObject resultSpecificData = new JSONObject(getSpecificData);
            JSONObject currency = resultSpecificData.getJSONObject("data");
            index = 0;

            while (jsonArray50Currencies.length() > index) {
                JSONObject jsonObject = jsonArray50Currencies.getJSONObject(index);
                String name = jsonObject.optString("name", "");
                String symbol = jsonObject.optString("symbol", "");
                JSONObject quote = jsonObject.optJSONObject("quote");
                JSONObject usd = quote.getJSONObject("USD");
                Float price = usd.getFloat("price");
                String tags = jsonObject.optString("tags", "");
                String obtainmentMethod = jsonObject.getJSONArray("tags").getString(0);
                Date releaseDate = Date.valueOf(jsonObject.optString("date_added").split("T")[0]);
                Date lastUpdated = Date.valueOf(jsonObject.optString("last_updated").split("T")[0]);
                Integer coinID = jsonObject.optInt("id");
                JSONObject specificCurrency = currency.getJSONObject(coinID.toString());
                String description = specificCurrency.optString("description", "");
                String websiteLink = specificCurrency.getJSONObject("urls").getJSONArray("website").getString(0);
                CryptoCurrency cryptoCurrency = new CryptoCurrency();
                cryptoCurrency.setName(name);
                cryptoCurrency.setSymbol(symbol);
                cryptoCurrency.setObtainmentMethod(obtainmentMethod);
                cryptoCurrency.setWebsiteLink(websiteLink);
                cryptoCurrency.setPrice(price);
                cryptoCurrency.setCoinID(coinID);
                if (description.length() > 1026) {
                    cryptoCurrency.setDescription(description.substring(0, 1025));
                }
                else {
                    cryptoCurrency.setDescription(description);
                }
                cryptoCurrency.setReleaseDate(releaseDate);
                cryptoCurrency.setLastUpdatedDate(lastUpdated);
                cryptoCurrencyController.create(cryptoCurrency);
                index++;
            }
        } catch (Exception ex) {
            Methods.showMessage("Fatal Error", "The Coin Market Cap API Call has failed!",
                    "See: " + ex.getMessage());
            return "";
        }
        return "";
    }
}
