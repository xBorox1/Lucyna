package Searcher;

import Indexer.FileIndexer;
import Indexer.IndexerSearcher;
import Indexer.SearchResult;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import java.io.IOException;
import java.util.Scanner;

public class Input {
    private String language = SearcherOptions.EN;
    private boolean details = false;
    private int limit = Integer.MAX_VALUE;
    private boolean color = false;
    private String queryType = SearcherOptions.TERM;

    private String makeSearch(String text, FileIndexer indexer) {
        IndexerSearcher searcher = new IndexerSearcher(indexer);
        SearchResult result = null;

        try {
            switch (queryType) {
                case SearcherOptions.TERM:
                    result = searcher.searchTerm(text, limit);
                    break;
                case SearcherOptions.PHRASE:
                    result = searcher.searchPhrase(text, limit);
                    break;
                case SearcherOptions.FUZZY:
                    result = searcher.searchFuzzy(text, limit);
                    break;
            }
        }
        catch (IOException e) {
            System.out.println("Nie udało się przeprowadzić wyszukania : " + e.toString());
        }

        if (details) {
            try {
                return result.getResultwithContext(color);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidTokenOffsetsException e) {
                e.printStackTrace();
            }
        }
        return result.getResult();

    }

    private void setLanguage(String option) {
        switch(option) {
            case SearcherOptions.PL:
                language = "pl";
                break;
            case SearcherOptions.EN:
                language = "en";
                break;
        }
    }

    private void setDetails(String option) {
        switch(option) {
            case SearcherOptions.ON:
                details = true;
                break;
            case SearcherOptions.OFF:
                details = false;
                break;
        }
    }

    private void setLimit(int num) {
        limit = num;
        if(limit == 0) limit = Integer.MAX_VALUE;
    }

    private void setColor(String option) {
        switch(option) {
            case SearcherOptions.ON:
                color = true;
                break;
            case SearcherOptions.OFF:
                color = false;
                break;
        }
    }

    public String makeCommand(String commandString, FileIndexer indexer) {
        if(commandString.charAt(0) == '%') {
            Scanner scanner = new Scanner(commandString);
            String commandName = scanner.next();
            switch (commandName) {
                case SearcherOptions.LANG:
                    setLanguage(scanner.next());
                    break;
                case SearcherOptions.DETAILS:
                    setDetails(scanner.next());
                    break;
                case SearcherOptions.LIMIT:
                    setLimit(scanner.nextInt());
                    break;
                case SearcherOptions.COLOR:
                    setColor(scanner.next());
                    break;
                case SearcherOptions.TERM:
                    queryType = SearcherOptions.TERM;
                    break;
                case SearcherOptions.PHRASE:
                    queryType = SearcherOptions.PHRASE;
                    break;
                case SearcherOptions.FUZZY:
                    queryType = SearcherOptions.FUZZY;
                    break;
            }
            return "";
        }
        else return makeSearch(commandString, indexer);
    }
}
