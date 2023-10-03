package ch.heigvd;
import picocli.CommandLine.Command;

@Command(
        name = "HelloName",
        description = "It prints Hello [Name]"
)
public class HelloNameCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("Hello Name with PicoCLI");
    }
}
