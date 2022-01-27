package lineCounter.model.consoleInterface.interfaceWrapper;

import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.ConsoleFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BasicProgram implements TerminalProgram {
    public void executeProgram()
    {

        ConsoleFrame frame = new ConsoleFrame(CommandSystem.getInstance(),
                CommandSystem.getInstance().getConsoleView());

        TerminalWrapper.redirectOutput( frame.mainPanel().consoleText() );
        final int i = 0;

        Timer timer = new Timer(1000, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {

            }
        });
        timer.start();
    }
}
