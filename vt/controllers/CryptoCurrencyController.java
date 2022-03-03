/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.Company;
import edu.vt.EntityBeans.CryptoCurrency;
import edu.vt.EntityBeans.User;
import edu.vt.EntityBeans.UserCryptoCurrency;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.FacadeBeans.CryptoCurrencyFacade;
import edu.vt.globals.Methods;
import edu.vt.globals.Constants;
import org.primefaces.shaded.json.JSONObject;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.MemoryHandler;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.mail.MessagingException;

/*
---------------------------------------------------------------------------
The @Named (javax.inject.Named) annotation indicates that the objects
instantiated from this class will be managed by the Contexts and Dependency
Injection (CDI) container. The name "cryptoCurrencyController" is used within
Expression Language (EL) expressions in JSF (XHTML) facelets pages to
access the properties and invoke methods of this class.
---------------------------------------------------------------------------
 */
@Named("cryptoCurrencyController")

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
public class CryptoCurrencyController implements Serializable {
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
    private CryptoCurrencyFacade cryptoCurrencyFacade;

    @EJB
    private UserFacade userFacade;

    @Inject
    private UserCryptoCurrencyController userCryptoCurrencyController;

    @Inject
    private PieChartController pieChartController;

    // List of object references of CryptoCurrency objects
    private List<CryptoCurrency> listOfCryptoCurrencies = null;

    // selected = object reference of a selected CryptoCurrency object
    private CryptoCurrency selected;

    // Flag indicating if cryptoCurrency data changed or not
    private Boolean cryptoCurrencyDataChanged;

    // searchItems = List of object references of CryptoCurrency objects found in the search
    private List<CryptoCurrency> searchItems = null;

    // searchField refers to searching cryptoCurrency name, engine type, or drive type individually or search in each
    private String searchField;

    // searchString contains the character string the user entered for searching the selected searchField
    private String searchString;
    private Integer searchType;
    private String coinDescriptionQ;
    private String obtainmentMethodQ;
    private Float fromPriceQ;
    private Float toPriceQ;
    private Date fromReleaseDateQ;
    private Date toReleaseDateQ;


    /*
    =========================
    Getter and Setter Methods
    =========================
     */

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public String getCoinDescriptionQ() {
        return coinDescriptionQ;
    }

    public void setCoinDescriptionQ(String coinDescriptionQ) {
        this.coinDescriptionQ = coinDescriptionQ;
    }

    public String getObtainmentMethodQ() {
        return obtainmentMethodQ;
    }

    public void setObtainmentMethodQ(String obtainmentMethodQ) {
        this.obtainmentMethodQ = obtainmentMethodQ;
    }

    public Float getFromPriceQ() {
        return fromPriceQ;
    }

    public void setFromPriceQ(Float fromPriceQ) {
        this.fromPriceQ = fromPriceQ;
    }

    public Float getToPriceQ() {
        return toPriceQ;
    }

    public void setToPriceQ(Float toPriceQ) {
        this.toPriceQ = toPriceQ;
    }

    public Date getFromReleaseDateQ() {
        return fromReleaseDateQ;
    }

    public void setFromReleaseDateQ(Date fromReleaseDateQ) {
        this.fromReleaseDateQ = fromReleaseDateQ;
    }

    public Date getToReleaseDateQ() {
        return toReleaseDateQ;
    }

    public void setToReleaseDateQ(Date toReleaseDateQ) {
        this.toReleaseDateQ = toReleaseDateQ;
    }

    public List<CryptoCurrency> getListOfCryptoCurrencies() {
        if (listOfCryptoCurrencies == null) {
            listOfCryptoCurrencies = cryptoCurrencyFacade.findAll();
        }
        return listOfCryptoCurrencies;
    }

    public CryptoCurrency getSelected() {
        return selected;
    }

    public void setSelected(CryptoCurrency selected) {
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
        return "/cryptoCurrency/cryptoList?faces-redirect=true";
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
        selected = new CryptoCurrency();
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
            listOfCryptoCurrencies = null;     // Invalidate listOfCryptoCurrencies to trigger re-query.
            cryptoCurrencyDataChanged = true;
        }
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

    public void create(CryptoCurrency cryptoCurrency) {
        this.selected = cryptoCurrency;
        create();
    }

    public String share() {
        Methods.preserveMessages();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        User signedInUser = (User) sessionMap.get("user");
        if (signedInUser == null) {
            Methods.showMessage("Information", "Unable to Share!",
                    "To share a crypto, a user must have signed in!");
            return "/cryptoCurrency/cryptoList?faces-redirect=true";
        }
        try {
            UserCryptoCurrency userCryptoCurrency = new UserCryptoCurrency();
            userCryptoCurrency.setUserID(signedInUser);
            userCryptoCurrency.setName(selected.getName());
            userCryptoCurrency.setSymbol(selected.getSymbol());
            userCryptoCurrency.setObtainmentMethod(selected.getObtainmentMethod());
            userCryptoCurrency.setWebsiteLink(selected.getWebsiteLink());
            userCryptoCurrency.setPrice(selected.getPrice());
            userCryptoCurrency.setCoinID(selected.getCoinID());
            userCryptoCurrency.setDescription(selected.getDescription());
            userCryptoCurrency.setReleaseDate(selected.getReleaseDate());
            userCryptoCurrency.setLastUpdatedDate(selected.getLastUpdatedDate());
            userCryptoCurrencyController.create(userCryptoCurrency);
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

        return "/cryptoCurrency/cryptoList?faces-redirect=true";
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
            listOfCryptoCurrencies = null; // Invalidate listOfCryptoCurrencys to trigger re-query.
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
            listOfCryptoCurrencies = null;     // Invalidate list of cryptoCurrencies to trigger re-query.
            cryptoCurrencyDataChanged = true;
        }
    }

    public String sendEmailData(CryptoCurrency c_selected) throws MessagingException {
        Methods.preserveMessages();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        User signedInUser = (User) sessionMap.get("user");
        if (signedInUser == null) {
            Methods.showMessage("Information", "Unable to email!",
                    "To email, a user must have signed in!");
            return "/cryptoCurrency/cryptoList?faces-redirect=true";
        }
        String emailTo = signedInUser.getEmail();
        String emailsubject = "Hi " + signedInUser.getFirstName() + ", here is your requested stock price";
        String emailBody = "The Price for " + c_selected.getSymbol() + "is " + c_selected.getPrice();
        EmailSendController emailSendController = new EmailSendController("None", emailTo, emailBody, emailsubject);

        emailSendController.sendEmail();
        return "/cryptoCurrency/cryptoList?faces-redirect=true";

    }

    public String search(Integer type) {
        searchType = type;
        selected = null;
        searchItems = null;

        return "/databaseSearch/SearchResults?faces-redirect=true";
    }

    public List<CryptoCurrency> getSearchItems() {
        if (searchItems == null) {
            switch (searchType) {
                case 1:
                    switch (searchField) {
                        case "Name":
                            searchItems = cryptoCurrencyFacade.nameQuery(searchString);
                            break;
                        case "Symbol":
                            searchItems = cryptoCurrencyFacade.symbolQuery(searchString);
                            break;
                        case "Description":
                            searchItems = cryptoCurrencyFacade.descriptionQuery(searchString);
                            break;
                        default:
                            searchItems = cryptoCurrencyFacade.allQuery(searchString);
                    }
                    break;
                case 2:
                    searchItems = cryptoCurrencyFacade.type2SearchQuery(coinDescriptionQ, obtainmentMethodQ);
                    break;
                case 3:
                    searchItems = cryptoCurrencyFacade.type3SearchQuery(fromPriceQ, toPriceQ);
                    break;
                case 4:
                    searchItems = cryptoCurrencyFacade.type4SearchQuery(fromReleaseDateQ, toReleaseDateQ);
                    break;
                default:
                    Methods.showMessage("Fatal Error", "Search Type is Out of Range!",
                            "");
            }
        }
        return searchItems;
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
                    cryptoCurrencyFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove(selected) method performs the DELETE operation of the "selected"
                     object in the database.

                     CryptoCurrencyFacade inherits the remove(selected) method from the AbstractFacade class.
                     */
                    cryptoCurrencyFacade.remove(selected);
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
