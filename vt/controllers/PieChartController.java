/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.controllers;

import edu.vt.globals.Constants;
import edu.vt.globals.Methods;
import org.primefaces.model.chart.*;
import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import edu.vt.SentimentAnalysis.twitterScraper;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

@Named("pieChartController")
@SessionScoped
public class PieChartController implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private PieChartModel pieChart;
    private String query = "MSFT stock";
    //private String companyName = "Apple Inc.";
    // private String companyUrl = "https://www.apple.com";
    int numberOfDays;
    private String[] stockPriceDays;
    private Double[] stockPrices;
    //Fields for pie chart sentiment analysis values
    private int veryPositive;
    private int positive;
    private int neutral;
    private int negative;
    private int veryNegative;
    /*
     ---------------------------------------------------------------------------------------------------------
     "The PostConstruct annotation is used on a method that needs to be executed after dependency injection
     is done to perform any initialization. This method MUST be invoked before the class is put into service."
     See for further info: https://docs.oracle.com/javaee/7/api/javax/annotation/PostConstruct.html
     ---------------------------------------------------------------------------------------------------------
     */
    @PostConstruct
    public void init() {
        // Display default stock chart
        obtainTweetData();
        createPieModel();
    }
    /*
   =========================
   Getter and Setter Methods
   =========================
    */
    public PieChartModel getPieChart() {
        return pieChart;
    }

    public void setPieChart(PieChartModel pieChart) {
        this.pieChart = pieChart;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        // Force stock symbol to be in uppercase
        this.query = query;
    }
//
//    public String getCompanyName() {
//        return companyName;
//    }
//
//    public void setCompanyName(String companyName) {
//        this.companyName = companyName;
//    }
//
//    public String getCompanyUrl() {
//        return companyUrl;
//    }
//
//    public void setCompanyUrl(String companyUrl) {
//        this.companyUrl = companyUrl;
//    }

    /*
    ================
    Instance Methods
    ================
     */
    public String displayPieChart() {
        obtainTweetData();
        createPieModel();
        return "/sentiment/Results?faces-redirect=true";
    }

    public void clear() {
        query = "";
    }

    public void setupPieChart() {
        obtainTweetData();
        createPieModel();
    }

    private void createPieModel(){
        pieChart = new PieChartModel();
        pieChart.set("Very Positive", veryPositive);
        pieChart.set("Positive", positive);
        pieChart.set("Neutral", neutral);
        pieChart.set("Negative", negative);
        pieChart.set("Very Negative", veryNegative);
        pieChart.setTitle("Sentiment Analysis of " + query);
        pieChart.setLegendPosition("w");
        pieChart.setShadow(false);
    }
    private void obtainTweetData(){
        twitterScraper scraper = new twitterScraper();
        scraper.scraper(query);
        //use the getters to get each value
        veryPositive = scraper.getNum_realpositive();
        positive = scraper.getNum_positive();
        neutral = scraper.getNum_neutral();
        negative = scraper.getNum_negative();
        veryNegative = scraper.getNum_realnegative();
    }
}