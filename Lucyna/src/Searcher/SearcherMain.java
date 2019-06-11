package Searcher;

import Indexer.FileIndexer;

import java.io.IOException;

public class SearcherMain {

    private static final String pathIndexerDirectory= "index";

    public static void main(String[] args) {

        FileIndexer indexer = null;
        try {
            indexer = new FileIndexer(pathIndexerDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CommandLine commandLine = new CommandLine(indexer);
    }
}
