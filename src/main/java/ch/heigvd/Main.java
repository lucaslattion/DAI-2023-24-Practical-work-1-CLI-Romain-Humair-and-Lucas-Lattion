package ch.heigvd;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        //new CommandLine(new HelloWorldCommand()).execute(args);
        //int exitCode = new CommandLine(new HelloNameCommand()).execute(args);
        //int exitCode = new CommandLine(new TextTransformer()).execute(args);
        int exitCode = new CommandLine(new WordCounter()).execute(args);
        //int exitCode = new CommandLine(new WordProcessor()).execute(args);
        System.exit(exitCode);
    }
}