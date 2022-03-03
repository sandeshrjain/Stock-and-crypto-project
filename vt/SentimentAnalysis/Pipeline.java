/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.SentimentAnalysis;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Pipeline {

    private static StanfordCoreNLP stanfordCoreNLP;
    private static Properties properties;
    private static String propertiesName= "tokenize";
    private Pipeline()
    {

    }

    static {
        properties = new Properties();
        properties.setProperty("annotators", propertiesName);


    }
    public static StanfordCoreNLP getPipeline(){

        if(stanfordCoreNLP == null){
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }

        return stanfordCoreNLP;
    }
}
