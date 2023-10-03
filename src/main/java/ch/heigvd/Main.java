package ch.heigvd;
import picocli.CommandLine;
import picocli.CommandLine.Command;

public class Main {
    public static void main(String[] args) {
        new CommandLine(new HelloWorldCommand()).execute(args);
        //System.out.println("Hello world!");
    }
}