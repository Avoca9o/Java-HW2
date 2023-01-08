import java.io.File;

public class Main {

    public static void main(String[] args) {
        final File rootDirectory = new File(System.getProperty("user.dir") + "\\root");
        final FilesDB database = new FilesDB(rootDirectory);

        try {
            database.putDependencies();
            database.print();
        } catch (LoopException loopException) {
            System.out.println("There is a cycle!\n");
            database.printCycle(loopException.file);
        } catch (Throwable th) {
            System.out.println(th.getMessage() + "\nsomething went wrong...");
        }
    }
}