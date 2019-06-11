package Indexer;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class IndexerFileVisitors {

    public static FileVisitor<Path> addFileVisitor(FileIndexer indexer) {
        return  new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                if (Files.isRegularFile(path)) {
                    try {
                        indexer.indexFile(path.toFile());
                    } catch (Exception e) {

                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };
    }

    public static FileVisitor<Path> deleteFileVisitor(FileIndexer indexer) {
        return  new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                if (Files.isRegularFile(path)) {
                    try {
                        indexer.deleteFile(path.toFile());
                    } catch (Exception e) {

                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };
    }
}
