package lineCounter.model.consoleInterface.interfaces.terminal;

public interface Terminal {
    public void executeCommand(String[] args);
    public void print(String msg);
    public void printCommandNotFound();
    public void close();
    public void startNextLine();


}
