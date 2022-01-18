package lineCounter.model.consoleInterface.interfaces;

public interface CommandsController {
    public void moveCommandsDownwards();
    public void moveCommandsUpwards();
    public void processCommand(String command);
    public void exitOperation();
    public void setConsolePrinter(ConsolePrinter consolePrinter);

    public void updateStdInStdOut(InputTerminal t, OutputTerminal t1);

    public void close();
}
