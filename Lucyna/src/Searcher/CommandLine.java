package Searcher;

import Indexer.FileIndexer;
import org.jline.builtins.Completers;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CommandLine {
    private static Logger logger = LoggerFactory.getLogger(CommandLine.class);
    private Input input;

    public CommandLine(FileIndexer indexer) {
        System.setProperty("org.jline.terminal.dumb", "true");
        logger.info("Warming up...");

        input = new Input();

        try (Terminal terminal = TerminalBuilder.builder()
                .jna(false)
                .jansi(true)
                .build()) {
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new Completers.FileNameCompleter())
                    .build();
            while (true) {
                String line = null;
                try {
                    line = lineReader.readLine("> ");
                    String output = input.makeCommand(line, indexer);
                    if(output != "")
                    terminal.writer().println(new AttributedStringBuilder()
                                    .append(output)
                                    .toAnsi());
                } catch (UserInterruptException e) {
                    break;
                } catch (EndOfFileException e) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("An error has occured", e);
        }
    }
}
