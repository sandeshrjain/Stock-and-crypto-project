/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.CryptoCurrency;
import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserCryptoCurrency;
import edu.vt.FacadeBeans.UserCryptoCurrencyFacade;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.FacadeBeans.CryptoCurrencyFacade;
import edu.vt.globals.Methods;
import edu.vt.globals.Constants;
import org.primefaces.shaded.json.JSONObject;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

/*
---------------------------------------------------------------------------
The @Named (javax.inject.Named) annotation indicates that the objects
instantiated from this class will be managed by the Contexts and Dependency
Injection (CDI) container. The name "cryptoCurrencyController" is used within
Expression Language (EL) expressions in JSF (XHTML) facelets pages to
access the properties and invoke methods of this class.
---------------------------------------------------------------------------
 */
@Named("userCryptoCurrencyController")

/*
The @SessionScoped annotation preserves the values of the CryptoCurrencyController
object's instance variables across multiple HTTP request-response cycles
as long as the user's established HTTP session is alive.
 */
@SessionScoped

/*
-----------------------------------------------------------------------------
Marking the CryptoCurrencyController class as "implements Serializable" implies that
instances of the class can be automatically serialized and deserialized.

Serialization is the process of converting a class instance (object)
from memory into a suitable format for storage in a file or memory buffer,
or for transmission across a network connection link.

Deserialization is the process of recreating a class instance (object)
in memory from the format under which it was stored.
-----------------------------------------------------------------------------
 */
public class UserCryptoCurrencyController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
    */

    /*
    The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
    CryptoCurrencyFacade bean into the instance variable 'cryptoCurrencyFacade' after it is instantiated at runtime.
     */
    @EJB
    private UserCryptoCurrencyFacade userCryptoCurrencyFacade;

    @EJB
    private UserFacade userFacade;

    @Inject
    private CryptoCurrencyController cryptoCurrencyController;

    @Inject
    private PieChartController pieChartController;

    // List of object references of CryptoCurrency objects
    private List<UserCryptoCurrency> listOfUserCryptoCurrencies = null;

    // selected = object reference of a selected CryptoCurrency object
    private UserCryptoCurrency selected;

    // Flag indicating if cryptoCurrency data changed or not
    private Boolean cryptoCurrencyDataChanged;

    // searchItems = List of object references of CryptoCurrency objects found in the search
    private List<UserCryptoCurrency> searchItems = null;

    // searchField refers to searching cryptoCurrency name, engine type, or drive type individually or search in each
    private String searchField;

    // searchString contains the character string the user entered for searching the selected searchField
    private String searchString;


    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public List<UserCryptoCurrency> getListOfUserCryptoCurrencies() {
        if (listOfUserCryptoCurrencies == null) {
            /*
            'user', the object reference of the signed-in user, was put into the SessionMap
            in the initializeSessionMap() method in LoginManager upon user's sign in.
             */
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            User signedInUser = (User) sessionMap.get("user");

            // Obtain the database primary key of the signedInUser object
            Integer primaryKey = signedInUser.getId();

            listOfUserCryptoCurrencies = userCryptoCurrencyFacade.findUserCryptoCurrenciesByUserPrimaryKey(primaryKey);
        }
        return listOfUserCryptoCurrencies;
    }

    public UserCryptoCurrency getSelected() {
        return selected;
    }

    public void setSelected(UserCryptoCurrency selected) {
        this.selected = selected;
    }

    public Boolean getCryptoCurrencyDataChanged() {
        return cryptoCurrencyDataChanged;
    }

    public void setCryptoCurrencyDataChanged(Boolean cryptoCurrencyDataChanged) {
        this.cryptoCurrencyDataChanged = cryptoCurrencyDataChanged;
    }

    // getSearchItems() is given at the bottom

    /*
     ****************************************
     *   Unselect Selected CryptoCurrency Object   *
     ****************************************
     */
    public void unselect() {
        selected = null;
    }

    /*
     *************************************
     *   Cancel and Display List.xhtml   *
     *************************************
     */
    public String cancel() {
        // Unselect previously selected cryptoCurrency object if any
        selected = null;
        return "/userCryptoCurrency/cryptoList?faces-redirect=true";
    }

    /*
     ***************************************
     *   Prepare to Create a New CryptoCurrency   *
     ***************************************
     */
    public void prepareCreate() {
        /*
        Instantiate a new CryptoCurrency object and store its object reference into
        instance variable 'selected'. The CryptoCurrency class is defined in CryptoCurrency.java
         */
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        User signedInUser = (User) sessionMap.get("user");
        selected = new UserCryptoCurrency(signedInUser);
    }

    /*
     ********************************************
     *   CREATE a New CryptoCurrency in the Database   *
     ********************************************
     */
    public void create() {
        Methods.preserveMessages();

        /*
        An enum is a special Java type used to define a group of constants.
        The constants CREATE, DELETE and UPDATE are defined as follows in JsfUtil.java

                public enum PersistAction {
                    CREATE,
                    DELETE,
                    UPDATE
                }
         */

        /*
         The object reference of the cryptoCurrency to be created is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(PersistAction.CREATE, "CryptoCurrency was Successfully Created!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;            // Remove selection
            listOfUserCryptoCurrencies = null;     // Invalidate listOfCryptoCurrencies to trigger re-query.
            cryptoCurrencyDataChanged = true;
        }
    }

    /*
     *************************************************************
     *   Setup the Sentiment Analysis for the Selected Company   *
     *************************************************************
     */
    public String pieChart() {
        // Set stockChartController's stockSymbol to the selected company's ticker (stock symbol)
        pieChartController.setQuery(selected.getSymbol());

        // Execute stockChartController's setupStockChart() method
        pieChartController.setupPieChart();

        return "/sentiment/Results?faces-redirect=true";
    }

    public void checkCreate() {
        Methods.preserveMessages();
        String getLatestInfo = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
        getLatestInfo = getLatestInfo + "?CMC_PRO_API_KEY=" + Constants.COIN_MARKET_CAP_API + "&symbol=" + selected.getSymbol();
        try {
            String getLatestInfoResults = Methods.readUrlContent(getLatestInfo);
            JSONObject resultsJsonObject = new JSONObject(getLatestInfoResults);
            JSONObject update = resultsJsonObject.getJSONObject("data");
            JSONObject currency = update.getJSONObject(selected.getSymbol());
            Integer coinID = currency.getInt("id");
            selected.setCoinID(coinID);
            create();
        } catch (Exception ex) {
            Methods.showMessage("Fatal Error", "The Coin Market Cap API Call could not find a currency with that symbol!",
                    "See: " + ex.getMessage());
        }
    }

    public void checkEdit() {
        Methods.preserveMessages();
        String getLatestInfo = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
        getLatestInfo = getLatestInfo + "?CMC_PRO_API_KEY=" + Constants.COIN_MARKET_CAP_API + "&symbol=" + selected.getSymbol();
        try {
            String getLatestInfoResults = Methods.readUrlContent(getLatestInfo);
            JSONObject resultsJsonObject = new JSONObject(getLatestInfoResults);
            JSONObject update = resultsJsonObject.getJSONObject("data");
            JSONObject currency = update.getJSONObject(selected.getSymbol());
            Integer coinID = currency.getInt("id");
            selected.setCoinID(coinID);
            update();
        } catch (Exception ex) {
            Methods.showMessage("Fatal Error", "The Coin Market Cap API Call could not find a currency with that symbol!",
                    "See: " + ex.getMessage());
        }
    }

    public void create(UserCryptoCurrency userCryptoCurrency) {
        this.selected = userCryptoCurrency;
        create();
    }

    public String share() {
        try {
            CryptoCurrency cryptoCurrency = new CryptoCurrency();
            cryptoCurrency.setName(selected.getName());
            cryptoCurrency.setSymbol(selected.getSymbol());
            cryptoCurrency.setObtainmentMethod(selected.getObtainmentMethod());
            cryptoCurrency.setWebsiteLink(selected.getWebsiteLink());
            cryptoCurrency.setPrice(selected.getPrice());
            cryptoCurrency.setCoinID(selected.getCoinID());
            cryptoCurrency.setDescription(selected.getDescription());
            cryptoCurrency.setReleaseDate(selected.getReleaseDate());
            cryptoCurrency.setLastUpdatedDate(selected.getLastUpdatedDate());
            cryptoCurrencyController.create(cryptoCurrency);
        } catch (EJBException ex) {
            String msg = "";
            Throwable cause = ex.getCause();
            if (cause != null) {
                msg = cause.getLocalizedMessage();
            }
            if (msg.length() > 0) {
                JsfUtil.addErrorMessage(msg);
            } else {
                JsfUtil.addErrorMessage(ex, "A persistence error occurred.");
            }
        }
        return "/userCryptoCurrency/cryptoList?faces-redirect=true";
    }

    public void updateInfo() {
        Methods.preserveMessages();
        String getLatestInfo = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
        getLatestInfo = getLatestInfo + "?CMC_PRO_API_KEY=" + Constants.COIN_MARKET_CAP_API + "&id=" + selected.getCoinID().toString();
        try {
            String getLatestInfoResults = Methods.readUrlContent(getLatestInfo);
            JSONObject resultsJsonObject = new JSONObject(getLatestInfoResults);
            JSONObject update = resultsJsonObject.getJSONObject("data");
            JSONObject specificCurrency = update.getJSONObject(selected.getCoinID().toString());
            JSONObject quote = specificCurrency.getJSONObject("quote");
            JSONObject usd = quote.getJSONObject("USD");
            Float price = usd.getFloat("price");
            java.sql.Date lastUpdated = java.sql.Date.valueOf(specificCurrency.getString("last_updated").split("T")[0]);
            selected.setLastUpdatedDate(lastUpdated);
            selected.setPrice(price);
            update();
        } catch (Exception ex) {
            Methods.showMessage("Fatal Error", "The Coin Market Cap API Call could not find that currency!",
                    "See: " + ex.getMessage());
        }
    }

    /*
     ***********************************************
     *   UPDATE Selected CryptoCurrency in the Database   *
     ***********************************************
     */
    public void update() {
        Methods.preserveMessages();
        /*
         The object reference of the cryptoCurrency to be updated is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(PersistAction.UPDATE, "CryptoCurrency was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;        // Remove selection
            listOfUserCryptoCurrencies = null; // Invalidate listOfCryptoCurrencys to trigger re-query.
            cryptoCurrencyDataChanged = true;
        }
    }

    /*
     *************************************************
     *   DELETE Selected CryptoCurrency from the Database   *
     *************************************************
     */
    public void destroy() {
        Methods.preserveMessages();
        /*
         The object reference of the cryptoCurrency to be deleted is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(PersistAction.DELETE, "CryptoCurrency was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;            // Remove selection
            listOfUserCryptoCurrencies = null;     // Invalidate list of cryptoCurrencies to trigger re-query.
            cryptoCurrencyDataChanged = true;
        }
    }

    /*
     **********************************************************************************************
     *   Perform CREATE, UPDATE (EDIT), and DELETE (DESTROY, REMOVE) Operations in the Database   *
     **********************************************************************************************
     */
    /**
     * @param persistAction refers to CREATE, UPDATE (Edit) or DELETE action
     * @param successMessage displayed to inform the user about the result
     */
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    /*
                     -------------------------------------------------
                     Perform CREATE or EDIT operation in the database.
                     -------------------------------------------------
                     The edit(selected) method performs the SAVE (STORE) operation of the "selected"
                     object in the database regardless of whether the object is a newly
                     created object (CREATE) or an edited (updated) object (EDIT or UPDATE).

                     CryptoCurrencyFacade inherits the edit(selected) method from the AbstractFacade class.
                     */
                    userCryptoCurrencyFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove(selected) method performs the DELETE operation of the "selected"
                     object in the database.

                     CryptoCurrencyFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    userCryptoCurrencyFacade.remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, "A persistence error occurred!");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "A persistence error occurred");
            }
        }
    }
}
