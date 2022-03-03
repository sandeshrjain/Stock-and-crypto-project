/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.SentimentAnalysis;

import com.google.protobuf.Enum;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import static edu.vt.SentimentAnalysis.SentimentAnalyzer.findSentiment;

public class twitterScraper {
    private int num_neutral;
    private int num_negative;
    private int num_realnegative;
    private int num_positive;
    private int num_realpositive;

    public void twitterScraper(){

    }
    public int getNum_neutral() {
        return num_neutral;
    }

    public void setNum_neutral(int num_neutral) {
        this.num_neutral = num_neutral;
    }

    public int getNum_negative() {
        return num_negative;
    }

    public void setNum_negative(int num_negative) {
        this.num_negative = num_negative;
    }

    public int getNum_realnegative() {
        return num_realnegative;
    }

    public void setNum_realnegative(int num_realnegative) {
        this.num_realnegative = num_realnegative;
    }

    public int getNum_positive() {
        return num_positive;
    }

    public void setNum_positive(int num_positive) {
        this.num_positive = num_positive;
    }

    public int getNum_realpositive() {
        return num_realpositive;
    }

    public void setNum_realpositive(int num_realpositive) {
        this.num_realpositive = num_realpositive;
    }


    public void scraper(String givenQuery){
        Integer[] sentimentList = new Integer[5];
        num_negative = 0;
        num_neutral = 0;
        num_positive = 0;
        num_realnegative = 0;
        num_realpositive = 0;
        SentimentAnalyzer.init();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("your val")
                .setOAuthConsumerSecret("your val")
                .setOAuthAccessToken("your val-your val")
                .setOAuthAccessTokenSecret("your val");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        Query query = new Query(givenQuery);
        query.setCount(10);
        try {
            QueryResult result = twitter.search(query);
            //System.out.println(result);
            //The following lines of code initialize variables that represent the number of tweets of each sentiment.

            for(int i=0; i<result.getTweets().size(); i++){
                String tweet = result.getTweets().get(i).getText();
                String sentiment = findSentiment(tweet);
                if (sentiment.equalsIgnoreCase("Neutral")) {
                    num_neutral++;
                } else if (sentiment.equalsIgnoreCase("Negative")) {
                    num_negative++;
                } else if (sentiment.equalsIgnoreCase("Very Negative")) {
                    num_realnegative++;
                } else if (sentiment.equalsIgnoreCase("Very Positive")) {
                    num_realpositive++;
                } else {
                    num_positive++;
                }
            }

            System.out.println(num_neutral);
            System.out.println(num_negative);
            System.out.println(num_realnegative);
            System.out.println(num_realpositive);
            System.out.println(num_positive);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }


}



