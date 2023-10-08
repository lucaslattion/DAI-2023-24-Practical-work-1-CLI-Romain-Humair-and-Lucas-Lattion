package ch.heigvd;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command(name = "word-counter",
        description = "Count and optionally highlight words in a text file")
public class WordCounter implements Runnable {

    @Parameters(index = "0", description = "Input file path")
    private File inputFile;

    @Parameters(index = "1", description = "Output file path")
    private File outputFile;

    @Option(names = {"-c", "--case-sensitive"}, description = "Enable case sensitivity")
    private boolean caseSensitive;

    @Option(names = {"-w", "--words"}, description = "List of words to filter/count",
            split = ",") // Split the option values by comma
    private List<String> filterWords = new ArrayList<>();

    @Option(names = {"-e", "--encoding"}, description = "Character encoding (default: UTF-8)")
    private String encoding = "UTF-8";

    @Option(names = {"-h", "--highlight"}, description = "Highlight words in Markdown format")
    private boolean highlight;

    public static void main(String[] args) {
        CommandLine.run(new WordCounter(), args);
    }

    @Override
    public void run() {
        try {
            // Read input file using the specified encoding
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), encoding));
            List<String> lines = reader.lines().collect(Collectors.toList());
            reader.close();

            // Initialize a word frequency map
            Map<String, Integer> wordCountMap = new TreeMap<>(caseSensitive ? String::compareTo : String::compareToIgnoreCase);

            // Process lines and count words
            for (String line : lines) {
                String[] words = line.split("\\W+"); // Split by non-word characters
                for (String word : words) {
                    if (!caseSensitive) {
                        word = word.toLowerCase(); // Convert to lowercase if case sensitivity is disabled
                    }
                    if (filterWords.isEmpty() || filterWords.contains(word)) {
                        wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                    }
                }
            }

            // Determine the output file name with .md extension if highlighting is enabled
            if (highlight && !filterWords.isEmpty()) {
                String outputFileName = outputFile.getName();
                if (!outputFileName.toLowerCase().endsWith(".md")) {
                    int lastDotIndex = outputFileName.lastIndexOf('.');
                    if (lastDotIndex != -1) {
                        outputFileName = outputFileName.substring(0, lastDotIndex);
                    }
                    outputFileName += ".md";
                }
                outputFile = new File(outputFile.getParentFile(), outputFileName);
            }

            // Write results to the output file
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), encoding));
            for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
                String word = entry.getKey();
                int count = entry.getValue();
                writer.write(word + ": " + count + "\n"); // Add a colon between word and count
            }

            // Perform highlighting if requested
            if (highlight && !filterWords.isEmpty()) {
                // Create a regex pattern for matching filter words
                String patternString = "\\b(" + String.join("|", filterWords) + ")\\b";
                Pattern pattern = Pattern.compile(patternString, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);

                // Process each line to add Markdown emphasis around filter words
                for (String line : lines) {
                    Matcher matcher = pattern.matcher(line);
                    StringBuffer highlightedLine = new StringBuffer();

                    while (matcher.find()) {
                        // Wrap matched words with Markdown emphasis (e.g., **word**)
                        matcher.appendReplacement(highlightedLine, "**" + matcher.group() + "**");
                    }

                    matcher.appendTail(highlightedLine);
                    writer.write(highlightedLine.toString() + "\n");
                }
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            CommandLine.usage(this, System.out);
        }
    }
}
