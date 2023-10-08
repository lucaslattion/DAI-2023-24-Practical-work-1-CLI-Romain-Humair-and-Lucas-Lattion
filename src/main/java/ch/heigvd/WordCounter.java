package ch.heigvd;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Command(name = "WordCounter", description = "Count words in a file and write to output.")
public class WordCounter implements Runnable {

    @Parameters(index = "0", description = "Input file path.")
    private File inputFile;

    @Parameters(index = "1", description = "Output file path.")
    private File outputFile;

    @Option(names = {"-w", "--words"}, description = "List of words to filter (comma-separated).")
    private String wordsFilter;

    @Option(names = {"-e", "--encoding"}, description = "Character encoding (UTF-8 by default).", defaultValue = "UTF-8")
    private String encoding;

    @Option(names = {"-c", "--case-sensitive"}, description = "Enable case-sensitive word comparison.")
    private boolean caseSensitive;

    public void run() {
        try {
            Charset charset = Charset.forName(encoding);
            Map<String, Integer> wordCounts = new HashMap<>();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), charset))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] words = line.split("\\s+");
                    for (String word : words) {
                        if (!caseSensitive) {
                            word = word.toLowerCase(); // Convert to lowercase for case-insensitive comparison
                        }
                        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                    }
                }
            }

            if (wordsFilter != null) {
                Set<String> filterWords = Arrays.stream(wordsFilter.split(",")).collect(Collectors.toSet());

                // Initialize counts for filter words to 0 if not found
                for (String filterWord : filterWords) {
                    if (!caseSensitive) {
                        filterWord = filterWord.toLowerCase();
                    }
                    wordCounts.putIfAbsent(filterWord, 0);
                }

                wordCounts.keySet().retainAll(filterWords);
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charset))) {
                List<String> sortedWords = new ArrayList<>(wordCounts.keySet());
                sortedWords.sort(Comparator.naturalOrder());

                for (String word : sortedWords) {
                    int count = wordCounts.get(word);
                    writer.write(String.format("%s: %d%n", word, count));
                }
            }

            System.out.println("Word counting completed.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());

            // Print Picocli help menu
            CommandLine.usage(this, System.out);
        }
    }

    public static void main(String[] args) {
        CommandLine.run(new WordCounter(), args);
    }
}
