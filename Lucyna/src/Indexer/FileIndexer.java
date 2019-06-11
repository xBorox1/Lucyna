package Indexer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static Indexer.IndexerConstants.CONTENT;
import static Indexer.IndexerConstants.DIRECTORY_PATH;
import static Indexer.IndexerConstants.NAME;
import static Indexer.IndexerConstants.FILE_PATH;
import static Indexer.IndexerConstants.PL;
import static Indexer.IndexerConstants.ENG;
import static java.nio.file.Files.walkFileTree;

public class FileIndexer {

    private Directory indexDirectory;

    public FileIndexer(String indexDirectoryPath) throws IOException {
        indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
    }

    public IndexReader reader() throws IOException {
        return DirectoryReader.open(indexDirectory);
    }

    public void purge() throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDirectory, config);
        writer.deleteAll();
        writer.close();
    }

    private void indexFileDirectory(String path) throws IOException {
        System.out.println("Indexing Directory : " + path);

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDirectory, config);

        Document document = new Document();
        document.add(new TextField(DIRECTORY_PATH, path, Field.Store.YES));

        writer.addDocument(document);
        writer.close();
    }

    public void addDirectory(String path, boolean addDirToMemory) throws IOException, TikaException, SAXException {
        path = FileParser.getCanonicalPath(path);
        if(addDirToMemory) indexFileDirectory(path);
        FileVisitor<Path> fileVisitor = IndexerFileVisitors.addFileVisitor(this);
        Path start = Paths.get(path);
        walkFileTree(start, fileVisitor);
    }

    public void deleteFileDirectory(String path) throws IOException {
        path = FileParser.getCanonicalPath(path);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDirectory, config);

        writer.deleteDocuments(new Term(DIRECTORY_PATH, path));
        writer.close();

        System.out.println("Deleting Directory : " + path);
    }

    public void deleteFile(File file) throws IOException {
        System.out.println("Deleting file");

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDirectory, config);

        String fileName = FileParser.getName(file);
        writer.deleteDocuments(new Term(NAME, fileName));
        writer.close();
    }

    public void deleteDirectory(String path) throws IOException {
        deleteFileDirectory(path);
        FileVisitor<Path> fileVisitor = IndexerFileVisitors.deleteFileVisitor(this);
        Path start = Paths.get(path);
        walkFileTree(start, fileVisitor);
    }

    public void reindex() throws IOException, ParseException, TikaException, SAXException {
        List<String> directories = directoriesList();
        for(String dir : directories) {
            deleteDirectory(dir);
            addDirectory(dir, true);
        }
    }

    List<String> directoriesList() throws IOException {
        Query query = new NormsFieldExistsQuery(DIRECTORY_PATH);

        SearchResult result = new IndexerSearcher(this).search(query, Integer.MAX_VALUE);
        List<Document> documents = result.getDocuments();
        ArrayList<String> directories = new ArrayList<>();
        for(Document d : documents) {
            directories.add(d.get(DIRECTORY_PATH));
        }
        return directories;
    }

    private static Document getDocumentFromFile(File file) throws IOException, TikaException, SAXException {
        Document document = new Document();
        document.add(new TextField(CONTENT, FileParser.getContent(file), Field.Store.YES));
        document.add(new TextField(NAME, FileParser.getName(file), Field.Store.YES));
        document.add(new TextField(FILE_PATH, FileParser.getCanonicalPath(file), Field.Store.YES));
        return document;
    }

    private static Analyzer chooseAnalyzer(File file) throws TikaException, IOException, SAXException {
        Analyzer analyzer = null;
        String language = FileParser.getLanguage(file);
        switch (language) {
            case PL: {
                analyzer = new PolishAnalyzer();
                break;
            }
            case ENG: {
                analyzer = new EnglishAnalyzer();
                break;
            }
            default: {
                analyzer = new StandardAnalyzer();
            }
        }
        return analyzer;
    }

    public void indexFile(File file) throws IOException, TikaException, SAXException {
        Document document = getDocumentFromFile(file);
        Analyzer analyzer = chooseAnalyzer(file);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(indexDirectory, config);
        writer.addDocument(document);
        writer.close();

        System.out.println("Indexing file : " + document.get(FILE_PATH));
    }
}
