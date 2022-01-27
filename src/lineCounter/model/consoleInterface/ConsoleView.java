package lineCounter.model.consoleInterface;

import lineCounter.model.SystemHandler;
import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.interfaces.CommandsController;

import javax.swing.*;
import java.awt.event.*;
import java.util.Map;

public class ConsoleView implements KeyListener, WindowListener {

    private CommandsController commandSystem;
    private ConsoleText text;
    private ConsoleFrame frameToClose;

    public ConsoleView(CommandsController commandSystem)
    {
        this.commandSystem = commandSystem;


    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    public String[] clear()
    {
        ConsoleFrame frame = text.panel().parent();
        frame.reset();
        return new String[] {""};
    }

    private void setText(ConsoleText text)
    {
        this.text = text;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        ConsoleText t = (ConsoleText) keyEvent.getSource();


        if(t == null)
            return;

        /* perhaps this is not needed */
        frameToClose = t.panel().parent();

        commandSystem.setConsolePrinter(t);
        commandSystem.updateStdInStdOut(t, t);

        setText(t);

        Action enterHandle = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String com = t.getCommand();
                com = com.trim();
                commandSystem.processCommand(com);
            }
        };

        Action tabHandle = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String com = t.getCommand();
                t.deleteCommand();

                if (commandSystem.searchFirstThatContains(com) != null)
                    t.appendToPane(commandSystem.searchFirstThatContains(com));
                else
                    t.appendToPane(com);

            }
        };

        t.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "tabHandling");
        t.getActionMap().put("tabHandling", tabHandle);

        t.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enterHandling");
        t.getActionMap().put("enterHandling", enterHandle);

        if(keyEvent.getKeyCode() == KeyEvent.VK_UP)
        {
            t.deleteCommand();
            commandSystem.moveCommandsUpwards();
        }
        if(keyEvent.getKeyCode() == KeyEvent.VK_DOWN)
        {
            t.deleteCommand();
            commandSystem.moveCommandsDownwards();
        }
        if(keyEvent.getKeyCode() == KeyEvent.VK_N && keyEvent.isControlDown())
        {
            /* ctrl + n now opens a new window */
            setText(t);

            CommandSystem commandSystem_instance = (CommandSystem) commandSystem;
            if(commandSystem_instance == null)
                return;
            commandSystem_instance.win();
        }
        if(keyEvent.getKeyCode() == KeyEvent.VK_F4 && keyEvent.isAltDown() ||
                keyEvent.getKeyCode() == KeyEvent.VK_W && keyEvent.isControlDown())
        {
            /* control-w, alt-f4 close the window (and the app if it's the last one) */
            setText(t);

            commandSystem.setConsolePrinter(t);
            commandSystem.close();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        frameToClose = (ConsoleFrame)  windowEvent.getSource();
        if(SystemHandler.getNumberOfWindows() > 1)
        {
            this.commandSystem.close();
            return;
        }
        this.commandSystem.exitOperation();
    }

    public void close()
    {
        frameToClose.setVisible(false);
        SystemHandler.removeDev(frameToClose.getNum());
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {

    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }

}
