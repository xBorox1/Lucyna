package Indexer;

import org.apache.lucene.queryparser.classic.ParseException;

import javax.swing.text.Document;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher {

    private FileIndexer indexer;
    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;

    public void registerDirectory(Path path) throws IOException {
        WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, path);

        System.out.println("Directory registered : " + path.toString());
    }

    public void registerDirectoryTree(Path path) throws IOException {
        System.out.println("Registering directory : " + path.toString());

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
                registerDirectory(path);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public FileWatcher(FileIndexer indexer) throws IOException, ParseException {
        this.indexer = indexer;
        watcher = FileSystems.getDefault().newWatchService();
        keys = new HashMap<WatchKey, Path>();

        List<String> paths = indexer.directoriesList();

        for(String path : paths) {
            registerDirectory(Paths.get(path));
        }
    }

    public void processEvents() {
        for(;;) {
            WatchKey key = null;
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Path dir = keys.get(key);
            if (dir == null) {
                continue;
            }

            for(WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path name = ev.context();
                Path child = dir.resolve(name);
                File childFile = child.toFile();

                System.out.println("Event on file : " + child.toString());

                try {
                    if (kind == ENTRY_CREATE) {
                        System.out.println("Create event");
                        if (childFile.isFile() && Files.isRegularFile(child))
                            indexer.indexFile(childFile);
                        else if(childFile.isDirectory())
                            indexer.addDirectory(child.toString(), false);
                    }
                    else if(kind == ENTRY_DELETE) {
                        System.out.println("Delete Event");
                        indexer.deleteFile(childFile);
                    }
                    else if(kind == ENTRY_MODIFY) {
                        System.out.println("Modify event");
                        if (childFile.isFile()) {
                            indexer.deleteFile(childFile);
                            if(Files.isRegularFile(child)) indexer.indexFile(childFile);
                        }
                    }
                }
                catch(Exception e) {
                    System.out.println("Nie udało się obsłużyć zdarzenia : " + e.toString());
                }
            }
            key.reset();
        }
    }
}
