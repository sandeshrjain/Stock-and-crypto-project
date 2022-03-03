/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.SentimentAnalysis;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.List;

public class TokenizeExample {

public static void main(String[] args){
StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
String text = "Hello, this is Sandesh Jain!";
    CoreDocument coreDocument = new CoreDocument(text);
    stanfordCoreNLP.annotate(coreDocument);
    List<CoreLabel> coreLabelList = coreDocument.tokens();

    for(CoreLabel coreLabel: coreLabelList){
        System.out.println(coreLabel.originalText());
    }



}

}
