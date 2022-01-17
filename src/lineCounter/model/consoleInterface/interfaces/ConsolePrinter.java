package lineCounter.model.consoleInterface.interfaces;

import java.awt.*;

public interface ConsolePrinter {
    public void executeCommand(String[] args);
    public void print(String msg);
    public void printCommandNotFound();
    public void close();
    public void startNextLine();

}
