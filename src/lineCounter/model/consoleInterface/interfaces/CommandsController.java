package lineCounter.model.consoleInterface.interfaces;

import lineCounter.model.consoleInterface.ConsoleText;

public interface CommandsController {
    public void moveCommandsDownwards();
    public void moveCommandsUpwards();
    public void processCommand(String command);
    public void exitOperation();
    public void setConsolePrinter(ConsolePrinter consolePrinter);

    public void updateStdInStdOut(InputDevice t, OutputDevice t1);

    public void close();
}
