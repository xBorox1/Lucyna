package Indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class IndexerMain {

    private static final String pathIndexerDirectory= "index";

    public static void main(String[] args) {
        FileIndexer indexer = null;
        try {
            indexer = new FileIndexer(pathIndexerDirectory);
        } catch (Exception e) {
            System.out.println("Nie udało się wczytać indeksera : " + e.toString());
            return;
        }

        try {
            if (args.length > 0) {
                switch (args[0]) {
                    case "--purge":
                        indexer.purge();
                        return;
                    case "--add":
                        indexer.addDirectory(args[1], true);
                        break;
                    case "--rm":
                        indexer.deleteDirectory(args[1]);
                        break;
                    case "--reindex":
                        indexer.reindex();
                        break;
                    case "--list":
                        List<String> directories = indexer.directoriesList();
                        for(String directory : directories) {
                            System.out.println(directory);
                        }
                        return;
                }
            }
        } catch (Exception e) {
            System.out.println("Nie udało się wykonać polecenia : " + e.toString());
        }

        FileWatcher watcher = null;
        try {
            watcher = new FileWatcher(indexer);
        } catch (Exception e) {
            System.out.println("Nie udało się wczytać indeksera : " + e.toString());
            return;
        }

        watcher.processEvents();
    }
}
