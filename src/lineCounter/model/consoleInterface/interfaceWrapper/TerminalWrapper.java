package lineCounter.model.consoleInterface.interfaceWrapper;

import lineCounter.model.consoleInterface.interfaces.terminal.Terminal;

import javax.swing.*;
import java.io.*;

public class TerminalWrapper implements Runnable {

    private final Terminal terminalDevice;
    private BufferedReader reader;


    private TerminalWrapper(Terminal terminalDevice, PipedOutputStream pipedOutputStream)
    {
        this.terminalDevice = terminalDevice;

        try
        {
            PipedInputStream pipedInputStream = new PipedInputStream( pipedOutputStream );
            reader = new BufferedReader( new InputStreamReader(pipedInputStream) );
        }
        catch(IOException e) {
            System.out.println("program cannot be executed");
        }
    }

    @Override
    public void run() {
        String line;

        try
        {
            while ((line = reader.readLine()) != null)
            {
                terminalDevice.print(line);
                //terminalDevice.startNextLine();
            }

            System.err.println("im here");
        }
        catch (IOException ioe)
        {
            JOptionPane.showMessageDialog(null,
                    "Error redirecting output : "+ioe.getMessage());
        }
    }


    public static void redirectOutput(Terminal displayPane)
    {
        TerminalWrapper.redirectOut(displayPane);
        TerminalWrapper.redirectErr(displayPane);
    }

    public static void redirectOut(Terminal displayPane)
    {
        PipedOutputStream pos = new PipedOutputStream();
        System.setOut( new PrintStream(pos, true) );

        TerminalWrapper terminalWrapper = new TerminalWrapper(displayPane, pos);
        new Thread(terminalWrapper).start();
    }

    public static void redirectErr(Terminal displayPane)
    {
        PipedOutputStream pos = new PipedOutputStream();
        System.setErr( new PrintStream(pos, true) );

        TerminalWrapper terminalWrapper = new TerminalWrapper(displayPane, pos);
        new Thread(terminalWrapper).start();
    }

    public static void execute()
    {
        new BasicProgram().executeProgram();
    }
}
