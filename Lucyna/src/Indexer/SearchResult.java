package Indexer;

import opennlp.tools.tokenize.TokenSampleStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchResult {

    private List<Document> documents;
    private Query query = null;

    private static String bold(String s) {
        return "\u001b[30;1m" +s + "\u001b[0m";
    }

    private static String color(String s) {
        return "\033[31m" + s + "\u001b[0m";
    }

    public SearchResult(List<Document> documents, Query query) {
        this.documents = documents;
        this.query = query;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public String getResult() {
        String result = "File count: " + bold(new Integer(documents.size()).toString()) + "\n";
        for(Document doc : documents) {
            result += bold(doc.get(IndexerConstants.FILE_PATH)) + "\n";
        }
        return result;
    }

    public String getResultwithContext(boolean color) throws IOException, InvalidTokenOffsetsException {
        Formatter formatter = new Formatter() {
            @Override
            public String highlightTerm(String s, TokenGroup tokenGroup) {
                if(tokenGroup.getTotalScore() == 0) return s;
                if(color) return bold(color(s));
                else return bold(s);
            }
        };

        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, scorer);
        Fragmenter fragmenter = new SimpleFragmenter();
        highlighter.setTextFragmenter(fragmenter);

        String result = "File count: " + bold(new Integer(documents.size()).toString()) + "\n";
        for(Document doc : documents) {
            result += doc.get(IndexerConstants.FILE_PATH) + "\n";

            String text = doc.get(IndexerConstants.CONTENT);
            Analyzer analyzer = new StandardAnalyzer();
            TokenStream stream =
                    TokenSources.getTokenStream("c", null, text, analyzer, highlighter.getMaxDocCharsToAnalyze());
            String[] frags = highlighter.getBestFragments(stream, text, highlighter.getMaxDocCharsToAnalyze());

            for(String frag : frags) {
                result += frag + "\n";
            }
        }
        return result;
    }
}
