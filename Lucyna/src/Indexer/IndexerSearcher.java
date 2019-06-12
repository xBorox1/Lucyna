package Indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static Indexer.IndexerConstants.CONTENT;

public class IndexerSearcher {
    private FileIndexer indexer;

    public IndexerSearcher(FileIndexer indexer) {
        this.indexer = indexer;
    }

    protected SearchResult search(Query query, int limit) throws IOException {
        IndexReader reader = indexer.reader();
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, limit);

        List<Document> documents = new ArrayList<>();
        for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        reader.close();
        return new SearchResult(documents, query);
    }

    public SearchResult searchTerm(String text, int limit) throws IOException {
        Term term = new Term(CONTENT, text);
        TermQuery query = new TermQuery(term);
        return search(query, limit);
    }

    public SearchResult searchPhrase(String text, int limit) throws IOException {
        PhraseQuery.Builder builder = new PhraseQuery.Builder();

        Scanner scanner = new Scanner(text);
        while(scanner.hasNext()) builder.add(new Term(CONTENT, scanner.next()));
        PhraseQuery query = builder.build();

        return search(query, limit);
    }

    public SearchResult searchFuzzy(String text, int limit) throws IOException {
        Term term = new Term(CONTENT, text);
        FuzzyQuery query = new FuzzyQuery(term);
        return search(query, limit);
    }
}
