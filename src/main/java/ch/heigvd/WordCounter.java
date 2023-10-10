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

    @Option(names = {"-w", "--words"}, description = "List of words to count/highlight",
            split = ",") // Split the option values by comma
    private List<String> filterWords = new ArrayList<>();

    @Option(names = {"-e", "--encoding"}, description = "Input and output character encoding (default: UTF-8) (priority over -ei and -eo)")
    private String encoding = "UTF-8";
    @Option(names = {"-ei", "--input-encoding"}, description = "Input character encoding (default: UTF-8)")
    private String inputEncoding = "UTF-8";

    @Option(names = {"-eo", "--output-encoding"}, description = "Output character encoding (default: UTF-8)")
    private String outputEncoding = "UTF-8";

    @Option(names = {"-h", "--highlight"}, description = "Highlight words in Markdown format")
    private boolean highlight;

    public static void main(String[] args) {
        CommandLine.run(new WordCounter(), args);
    }

    @Override
    public void run() {

        // Force input and ouput encoding if specified with -e --encoding
        if(!encoding.equals("UTF-8")){
            inputEncoding = encoding;
            outputEncoding = encoding;
        }

        // Convert filterWords to lowercase if case sensitivity is disabled
        if (!caseSensitive) {
            filterWords = filterWords.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }

        // Read input file using the specified input encoding
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), inputEncoding))) {
            List<String> lines = reader.lines().collect(Collectors.toList());

            // Initialize a word frequency map with all words from filterWords set to zero counts
            Map<String, Integer> wordCountMap = new TreeMap<>(caseSensitive ? String::compareTo : String::compareToIgnoreCase);
            if (!filterWords.isEmpty()) {
                // Add all words to the filter with count 0.
                for (String word : filterWords) {
                    wordCountMap.put(word, 0);
                }
            }

            // Process lines and count words
            for (String line : lines) {
                String[] words = line.split("[ .,?!()\"]"); // Split by non-word characters
                for (String word : words) {
                    if (!caseSensitive) {
                        word = word.toLowerCase(); // Convert to lowercase if case sensitivity is disabled
                    }
                    if ((filterWords.isEmpty() || filterWords.contains(word))&&(!word.isEmpty())) {
                        wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                    }
                }
            }

            // Write results to the output file using the specified output encoding
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), outputEncoding))) {
                for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
                    String word = entry.getKey();
                    int count = entry.getValue();
                    writer.write(word + ": " + count + "\n"); // Add a colon between word and count
                }

                writer.flush();
                System.out.println("Success write : " + outputFile);
            } catch (IOException e) {
                System.err.println("Exception in text writer: " + e.getMessage());
                System.exit(1);
            }

            // Determine the output file name with .md extension if highlighting is enabled
            if (highlight) {
                if (!filterWords.isEmpty()) {
                    String outputFileName = outputFile.getName();
                    if (!outputFileName.toLowerCase().endsWith(".md")) {
                        int lastDotIndex = outputFileName.lastIndexOf('.');
                        if (lastDotIndex != -1) {
                            outputFileName = outputFileName.substring(0, lastDotIndex);
                        }
                        outputFileName += ".md";
                    } else {
                        // if the output name for words count is already a .md file
                        outputFileName = outputFileName.substring(0, outputFileName.length()-".md".length());
                        outputFileName += "_highlighted.md";
                    }
                    outputFile = new File(outputFile.getParentFile(), outputFileName);
                    // Create a regex pattern for matching filter words
                    String patternString = "\\b(" + String.join("|", filterWords) + ")\\b";
                    Pattern pattern = Pattern.compile(patternString, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);

                    // Process each line to add Markdown emphasis around filter words
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), outputEncoding))) {
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
                        writer.flush();
                        System.out.println("Success write : " + outputFile);
                    } catch (IOException e) {
                        System.err.println("Exception in markdown writer: " + e.getMessage());
                        System.exit(1);
                    }
                } else{
                    System.out.println("highlight (-h) selected but no filtered word. add word to highlight with -w or --words option. ");
                    System.out.println(".md file not generated");
                }
            }
        } catch (IOException e) {
            System.err.println("Exception in buffered text reader: " + e.getMessage());
            System.exit(1);
        }
    }
}
