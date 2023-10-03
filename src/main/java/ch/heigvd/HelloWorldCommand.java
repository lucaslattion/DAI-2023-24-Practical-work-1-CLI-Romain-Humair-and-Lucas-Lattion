package ch.heigvd;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
        name = "HelloWorld",
        description = "It prints Hello Word"
)
public class HelloWorldCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Hello Word with PicoCLI");
    }
}
