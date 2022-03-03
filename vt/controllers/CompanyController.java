/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.controllers;

import edu.vt.EntityBeans.User;
import edu.vt.controllers.util.JsfUtil;
import edu.vt.controllers.util.JsfUtil.PersistAction;
import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import org.primefaces.shaded.json.JSONObject;
import edu.vt.EntityBeans.Company;
import edu.vt.FacadeBeans.CompanyFacade;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.primefaces.shaded.json.JSONObject;

@Named("companyController")
@SessionScoped
public class CompanyController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */


    // Stock quote data items
    private String latestStockPrice;
    private String latestStockPriceTime;
    private String priceEarningsRatio;
    private String highestPriceIn52Weeks;
    private String lowestPriceIn52Weeks;
    private String analystPrice;
    private String stockSymbol;
    private String companyName;
    private String companyExchange;
    private String companyDescription;
    private String companySector;
    private String companyWebsite;
    private String logo;
    private String revenue_per_share;
    /*
The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
CompanyFacade bean into the instance variable 'companyFacade' after it is instantiated at runtime.
 */
    @EJB
    private CompanyFacade companyFacade;
    @Inject
    private PieChartController pieChartController;

    // 'listOfCompanies' is a List containing the object references of Company objects
    private List<Company> listOfCompanies = null;

    // 'searchItems' is a List containing the object references of Company objects found in the search
    private List<Company> searchItems = null;

    // 'selected' contains the object reference of the selected Company object
    private Company selected;

    public String getLatestStockPrice() {
        return latestStockPrice;
    }

    public void setLatestStockPrice(String latestStockPrice) {
        this.latestStockPrice = latestStockPrice;
    }

    public String getLatestStockPriceTime() {
        return latestStockPriceTime;
    }

    public String getRevenue_per_share() {
        return revenue_per_share;
    }

    public void setRevenue_per_share(String revenue_per_share) {
        this.revenue_per_share = revenue_per_share;
    }

    public void setLatestStockPriceTime(String latestStockPriceTime) {
        this.latestStockPriceTime = latestStockPriceTime;
    }

    public String getPriceEarningsRatio() {
        return priceEarningsRatio;
    }

    public void setPriceEarningsRatio(String priceEarningsRatio) {
        this.priceEarningsRatio = priceEarningsRatio;
    }

    public String getHighestPriceIn52Weeks() {
        return highestPriceIn52Weeks;
    }

    public void setHighestPriceIn52Weeks(String highestPriceIn52Weeks) {
        this.highestPriceIn52Weeks = highestPriceIn52Weeks;
    }

    public String getLowestPriceIn52Weeks() {
        return lowestPriceIn52Weeks;
    }

    public void setLowestPriceIn52Weeks(String lowestPriceIn52Weeks) {
        this.lowestPriceIn52Weeks = lowestPriceIn52Weeks;
    }

    public String getAnalystPrice() {
        return analystPrice;
    }

    public void setAnalystPrice(String analystPrice) {
        this.analystPrice = analystPrice;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyExchange() {
        return companyExchange;
    }

    public void setCompanyExchange(String companyExchange) {
        this.companyExchange = companyExchange;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getCompanySector() {
        return companySector;
    }

    public void setCompanySector(String companySector) {
        this.companySector = companySector;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public CompanyFacade getCompanyFacade() {
        return companyFacade;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setCompanyFacade(CompanyFacade companyFacade) {
        this.companyFacade = companyFacade;
    }

    public void setListOfCompanies(List<Company> listOfCompanies) {
        this.listOfCompanies = listOfCompanies;
    }

    public List<Company> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(List<Company> searchItems) {
        this.searchItems = searchItems;
    }

    public Company getSelected() {
        return selected;
    }

    public void setSelected(Company selected) {
        this.selected = selected;
    }

    /*
     *************************************************************
     *   Setup the Sentiment Analysis for the Selected Company   *
     *************************************************************
     */
    public String pieChart() {
        // Set stockChartController's stockSymbol to the selected company's ticker (stock symbol)
        pieChartController.setQuery(selected.getTicker());

        // Execute stockChartController's setupStockChart() method
        pieChartController.setupPieChart();

        return "/sentiment/Results?faces-redirect=true";
    }
    /*
     ****************************************
     *   Unselect Selected Company Object   *
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
        // Unselect previously selected company object if any
        selected = null;
        return "/company/List?faces-redirect=true";
    }

    /*
     ******************************************
     *   Converter Method for Data Exporter   *
     ******************************************
     PrimeFaces p:dataExporter requires the values to be exported to be of String type.
     This method is called in company/List.xhtml.
     */
    public String convertIntToString(Integer value) {
        return Integer.toString(value);
    }

    /*
     *****************************
     *   Get List of Companies   *
     *****************************
     If 'listOfCompanies' is null, obtain the object references of all of the companies
     in the database, store them in the 'listOfCompanies" List, and return the List.
     */
    public List<Company> getListOfCompanies() {
        if (listOfCompanies == null) {
            // CompanyFacade inherits the findAll() method from the AbstractFacade class
            listOfCompanies = companyFacade.findAll();
        }
        return listOfCompanies;
    }

    public void prepareShare(UserCompanyController u_selected) throws Exception {
        getStockQuote(u_selected.getStockSymbol());
        System.out.print("User YT Created");
    }

    public String sendEmailData(Company c_selected) throws MessagingException {
        Methods.preserveMessages();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        User signedInUser = (User) sessionMap.get("user");
        if (signedInUser == null) {
            Methods.showMessage("Information", "Unable to email!",
                    "To email, a user must have signed in!");
            return "/companyInfo/companyList?faces-redirect=true";
        }
        String emailTo = signedInUser.getEmail();
        String emailsubject = "Hi " + signedInUser.getFirstName() + ", here is your requested stock price";
        String emailBody = "The Stock Price for " + c_selected.getTicker() + "is " + c_selected.getPrice();
        EmailSendController emailSendController = new EmailSendController("None", emailTo, emailBody, emailsubject);

        emailSendController.sendEmail();

        return "/companyInfo/companyList?faces-redirect=true";

    }

    /*
     ***************************************
     *   Prepare to Create a New Company   *
     ***************************************
     */
    public void prepareCreate() {
        /*
        Instantiate a new Company object and store its object reference into
        instance variable 'selected'. The Company class is defined in Company.java
         */
        selected = new Company();

        // Set logoFileUploaded to false
    }

    // The constants CREATE, DELETE and UPDATE are defined in JsfUtil.java

    /*
     ********************************************
     *   CREATE a New Company in the Database   *
     ********************************************
     */
    public void create() {
        Methods.preserveMessages();
        /*
         The object reference of the company to be created is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(PersistAction.CREATE, "Company was Successfully Created!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The CREATE operation is successfully performed.
            selected = null;            // Remove selection
            listOfCompanies = null;     // Invalidate listOfCompanies to trigger re-query
        }
    }

    /*
     ***********************************************
     *   UPDATE Selected Company in the Database   *
     ***********************************************
     */
    public void update() {
        Methods.preserveMessages();
        /*
         The object reference of the company to be updated is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(PersistAction.UPDATE, "Company was Successfully Updated!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The UPDATE operation is successfully performed.
            selected = null;            // Remove selection
            listOfCompanies = null;     // Invalidate listOfCompanies to trigger re-query.
        }
    }

    /*
     ************************************************************************
     *   DELETE Logo File and DELETE the Company Object from the Database   *
     ************************************************************************
     */
    public void destroy() {
        Methods.preserveMessages();

        //----------------------------------------------
        // Delete the Selected Company from the Database
        //----------------------------------------------

        /*
         The object reference of the company to be deleted is stored in the instance variable 'selected'
         See the persist method below.
         */
        persist(PersistAction.DELETE, "Company was Successfully Deleted!");

        if (!JsfUtil.isValidationFailed()) {
            // No JSF validation error. The DELETE operation is successfully performed.
            selected = null;            // Remove selection
            listOfCompanies = null;     // Invalidate listOfCompanies to trigger re-query
        }
    }

    /*
     **********************************************************************************************
     *   Perform CREATE, UPDATE (EDIT), and DELETE (DESTROY, REMOVE) Operations in the Database   *
     **********************************************************************************************
     */

    /**
     * @param persistAction  refers to CREATE, UPDATE (Edit) or DELETE action
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

                     CompanyFacade inherits the edit method from the AbstractFacade class.
                     */
                    companyFacade.edit(selected);
                } else {
                    /*
                     -----------------------------------------
                     Perform DELETE operation in the database.
                     -----------------------------------------
                     The remove(selected) method performs the DELETE operation of the "selected"
                     object in the database.

                     CompanyFacade inherits the remove method from the AbstractFacade class.
                     */
                    companyFacade.remove(selected);
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
                    JsfUtil.addErrorMessage(ex, "A Persistence Error Occured!");
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, "A Persistence Error Occured!");
            }
        }
    }

    /*
     **********************************************************
     *   Obtain Stock Quote Data for the Given Stock Symbol   *
     **********************************************************
     */
    public String getStockQuote(String companyStockSymbol) throws Exception {

        stockSymbol = companyStockSymbol;

        stockSymbol = stockSymbol.toUpperCase();

        String lstockSymbol = stockSymbol.toLowerCase();
        /*
        ================================================
        IEX API Developer Platform: https://iexcloud.io/
        ================================================
        */
        String AlphaVantageUrl = Constants.ALPHA_COMPANY_BASE_URL + stockSymbol + "&apikey=" + Constants.ALPHA_API_KEY;
        //https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=demo
        try {
            // Obtain the JSON file containing the selected company's stock quote data by using the readUrlContent method given below.
// more stuff to go here!

            String jsonData = Methods.readUrlContent(AlphaVantageUrl);


            JSONObject stockData = new JSONObject(jsonData);

            // If data cannot be obtained from the API for the given stock symbol, the catch section below is executed.

            // If data can be obtained form the API, the following shows the structure of the JSON data returned.

            /* Example Returned JSON Data for IBML:
            //https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=demo

            {
                "Symbol": "IBM",
                "AssetType": "Common Stock",
                "Name": "International Business Machines Corporation",
                "Description": "International Business Machines Corporation (IBM) is an American multinational technology company headquartered in Armonk, New York, with operations in over 170 countries. The company began in 1911, founded in Endicott, New York, as the Computing-Tabulating-Recording Company (CTR) and was renamed International Business Machines in 1924. IBM is incorporated in New York. IBM produces and sells computer hardware, middleware and software, and provides hosting and consulting services in areas ranging from mainframe computers to nanotechnology. IBM is also a major research organization, holding the record for most annual U.S. patents generated by a business (as of 2020) for 28 consecutive years. Inventions by IBM include the automated teller machine (ATM), the floppy disk, the hard disk drive, the magnetic stripe card, the relational database, the SQL programming language, the UPC barcode, and dynamic random-access memory (DRAM). The IBM mainframe, exemplified by the System/360, was the dominant computing platform during the 1960s and 1970s.",
                "CIK": "51143",
                "Exchange": "NYSE",
                "Currency": "USD",
                "Country": "USA",
                "Sector": "TECHNOLOGY",
                "Industry": "COMPUTER & OFFICE EQUIPMENT",
                "Address": "1 NEW ORCHARD ROAD, ARMONK, NY, US",
                "FiscalYearEnd": "December",
                "LatestQuarter": "2021-09-30",
                "MarketCapitalization": "106575754000",
                "EBITDA": "15659000000",
                "PERatio": "22.49",
                "PEGRatio": "2.0",
                "BookValue": "24.79",
                "DividendPerShare": "6.54",
                "DividendYield": "0.0559",
                "EPS": "5.28",
                "RevenuePerShareTTM": "83.23",
                "ProfitMargin": "0.064",
                "OperatingMarginTTM": "0.12",
                "ReturnOnAssetsTTM": "0.0374",
                "ReturnOnEquityTTM": "0.214",
                "RevenueTTM": "74461004000",
                "GrossProfitTTM": "35575000000",
                "DilutedEPSTTM": "5.28",
                "QuarterlyEarningsGrowthYOY": "-0.338",
                "QuarterlyRevenueGrowthYOY": "0.003",
                "AnalystTargetPrice": "147.1",
                "TrailingPE": "22.49",
                "ForwardPE": "10.08",
                "PriceToSalesRatioTTM": "1.431",
                "PriceToBookRatio": "4.716",
                "EVToRevenue": "2.115",
                "EVToEBITDA": "12.14",
                "Beta": "1.102",
                "52WeekHigh": "142.43",
                "52WeekLow": "106.73",
                "50DayMovingAverage": "125.43",
                "200DayMovingAverage": "130.89",
                "SharesOutstanding": "896320000",
                "DividendDate": "2021-12-10",
                "ExDividendDate": "2021-11-09"
            }

             */
            // Obtain the only dictionary returned as a JSON object


            /*
            ============
            Stock Symbol
            ============
             */
            // Stock symbols are used as logo filenames in all capital letters
            stockSymbol = stockSymbol.toUpperCase();

            /*
            ============
            Company Name
            ============
             */
            companyName = stockData.optString("Name", "");

            if (companyName.equals("")) {
                companyName = "Unavailable!";
            }

            companyDescription = stockData.optString("Description", "");

            if (companyDescription.equals("")) {
                companyDescription = "Unavailable!";
            }

            revenue_per_share = stockData.optString("RevenuePerShareTTM", "Unavailable");



            /*
            ========
            Exchange
            ========
             */
            companyExchange = stockData.optString("Exchange", "");

            if (companyExchange.equals("")) {
                companyExchange = "Unavailable!";
            }


            companySector = stockData.optString("Sector", "");

            if (companySector.equals("")) {
                companySector = "Unavailable!";
            }


            /*
            ================================
            Percentage Change in Stock Price
            ================================
             */
            analystPrice = stockData.optString("AnalystTargetPrice", "Unavailable");


            /*
            ======================
            Price / Earnings Ratio
            ======================
             */
            priceEarningsRatio = stockData.optString("PERatio", "");


            /*
            ========================================
            Highest Stock Price in the Last 52 Weeks
            ========================================
             */
            highestPriceIn52Weeks = stockData.optString("52WeekHigh", "");


            /*
            =======================================
            Lowest Stock Price in the Last 52 Weeks
            =======================================
             */
            lowestPriceIn52Weeks = stockData.optString("52WeekLow", "");


            /*
                     {
                      "symbol": "AAPL",
                      "companyName": "Apple Inc.",
                      "exchange": "NASDAQ",
                      "industry": "Telecommunications Equipment",
                      "website": "http://www.apple.com",
                      "description": "Apple, Inc. engages in the design, manufacture, and marketing of mobile communication, media devices, personal computers, and portable digital music players. It operates through the following geographical segments: Americas, Europe, Greater China, Japan, and Rest of Asia Pacific. The Americas segment includes North and South America. The Europe segment consists of European countries, as well as India, the Middle East, and Africa. The Greater China segment comprises of China, Hong Kong, and Taiwan. The Rest of Asia Pacific segment includes Australia and Asian countries. The company was founded by Steven Paul Jobs, Ronald Gerald Wayne, and Stephen G. Wozniak on April 1, 1976 and is headquartered in Cupertino, CA.",
                      "CEO": "Timothy Donald Cook",
                      "securityName": "Apple Inc.",
                      "issueType": "cs",
                      "sector": "Electronic Technology",
                      "primarySicCode": 3663,
                      "employees": 132000,
                      "tags": [
                        "Electronic Technology",
                        "Telecommunications Equipment"
                      ],
                      "address": "One Apple Park Way",
                      "address2": null,
                      "state": "CA",
                      "city": "Cupertino",
                      "zip": "95014-2083",
                      "country": "US",
                      "phone": "1.408.974.3123"
                    }
             */

            String iex_url_web = Constants.IEX_COMPANY_BASE_URL + lstockSymbol + "/company?token=" + Constants.IEX_API_KEY;

            String jsonIEXData = Methods.readUrlContent(iex_url_web);

            JSONObject iex_stockData = new JSONObject(jsonIEXData);

            companyWebsite = iex_stockData.optString("website", "");

            if (companyWebsite.equals("")) {
                companyWebsite = "Unavailable!";
            }
                    /* Example Returned JSON Data for Apple AAPL:
            https://cloud.iexapis.com/stable/stock/aapl/quote?token=YOUR-API-KEY

                {                                   <== Stock Data JSON Object (Dictionary with Key-Value pairings)
                    "symbol":"AAPL",
                    "companyName":"Apple Inc.",
                    "primaryExchange":"Nasdaq Global Select",
                    "sector":"Technology",
                    "calculationPrice":"tops",
                    "open":157.8,
                    "openTime":1508160600591,
                    "close":156.99,
                    "closeTime":1507924800139,
                    "latestPrice":159.59,                       <== Latest Stock Price
                    "latestSource":"IEX real time price",
                    "latestTime":"2:29:57 PM",                  <== Latest Stock Price Time
                    "latestUpdate":1508178597216,
                    "latestVolume":17222290,
                    "iexRealtimePrice":159.59,
                    "iexRealtimeSize":100,
                    "iexLastUpdated":1508178597216,
                    "delayedPrice":159.494,
                    "delayedPriceTime":1508177727073,
                    "previousClose":156.99,
                    "change":2.6,                               <== Change in Stock Price
                    "changePercent":0.01656,                    <== Percentage Change in Stock Price
                    "iexMarketPercent":0.00821,
                    "iexVolume":141395,
                    "avgTotalVolume":28517704,
                    "iexBidPrice":159.53,
                    "iexBidSize":100,
                    "iexAskPrice":159.9,
                    "iexAskSize":2000,
                    "marketCap":824318736520,
                    "peRatio":18.14,                           <== P/E Ratio
                    "week52High":164.94,                       <== 52 Weeks High
                    "week52Low":104.08,                        <== 52 Weeks Low
                    "ytdChange":0.35161429186396903
                }
*/
            String iex_url_quote = Constants.IEX_COMPANY_BASE_URL + lstockSymbol + "/quote?token=" + Constants.IEX_API_KEY;
            System.out.println(iex_url_quote);
            String jsonQuote = Methods.readUrlContent(iex_url_quote);
            JSONObject stockQuote = new JSONObject(jsonQuote);
            double latestPrice = stockQuote.optDouble("latestPrice", 0.0);

            if (latestPrice != 0.0) {
                // Format the Double value into a String with 2 decimal places
                latestStockPrice = String.format("$%.2f", latestPrice);
            } else {
                latestStockPrice = "Unavailable!";
            }

            //https://cloud.iexapis.com/stable/stock/aapl/logo?token=YOUR-API-KEY

            String iex_logo = Constants.IEX_COMPANY_BASE_URL + lstockSymbol + "/logo?token=" + Constants.IEX_API_KEY;

            String jsonLogoIEXData = Methods.readUrlContent(iex_logo);

            JSONObject iex_logoData = new JSONObject(jsonLogoIEXData);

            logo = iex_logoData.optString("url", "");

            if (logo.equals("")) {
                logo = "Unavailable!";
            }

        } catch (IOException ex) {
            Methods.showMessage("Fatal Error", "Unknown Stock Symbol",
                    "The API did not return data for the stock symbol entered!");
            return "";
        }
        selected = new Company(companyName, companyExchange, stockSymbol, latestStockPrice, companyDescription, logo, analystPrice, revenue_per_share, companySector, companyWebsite);
        create();


        return "Done!";
    }

    public String populateCompanyDB() throws Exception {
        for (String stock: Constants.COMPANIES){

            getStockQuote(stock);

        }
        return null;
    }



}
