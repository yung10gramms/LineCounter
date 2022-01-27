package lineCounter.model.consoleInterface.interfaces;

import lineCounter.model.consoleInterface.interfaces.terminal.InputTerminal;
import lineCounter.model.consoleInterface.interfaces.terminal.OutputTerminal;
import lineCounter.model.consoleInterface.interfaces.terminal.Terminal;

public interface CommandsController {
    public void moveCommandsDownwards();
    public void moveCommandsUpwards();
    public void processCommand(String command);
    public void exitOperation();
    public void setConsolePrinter(Terminal consolePrinter);

    public String searchFirstThatContains(String pattern);

    public void updateStdInStdOut(InputTerminal t, OutputTerminal t1);

    public void close();
}
