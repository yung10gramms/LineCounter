package lineCounter.model.consoleInterface.interfaces;

public interface OutputTerminal {
    void executeCommand(String[] args);
    void print(String msg);
    void printCommandNotFound();
    void close();
    void startNextLine();

    String output();
}
