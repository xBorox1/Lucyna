package Indexer;

import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileParser {

    public static String getContent(File file) throws TikaException, SAXException, IOException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputStream = new FileInputStream(file);
        parser.parse(inputStream, handler, metadata);
        return handler.toString();
    }

    public static String getName(File file) {
        return file.getName();
    }

    public static String getPath(File file) {
        return file.getPath();
    }

    public static String getLanguage(File file) throws IOException, TikaException, SAXException {
        String text = getContent(file);
        LanguageDetector detector = new OptimaizeLangDetector().loadModels();
        LanguageResult result = detector.detect(text);
        return result.getLanguage();
    }

    public static String getCanonicalPath(String path) {
       Path p = Paths.get(path);
       return p.normalize().toString();
    }

    public static String getCanonicalPath(File file) throws IOException {
        return file.getCanonicalPath();
    }
}
