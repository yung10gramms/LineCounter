package lineCounter.model.consoleInterface.interfaces;

public interface OutputDevice {
    public void executeCommand(String[] args);
    public void print(String msg);
    public void printCommandNotFound();
    public void close();
    public void startNextLine();

    public String output();
}
