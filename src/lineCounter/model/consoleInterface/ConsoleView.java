package lineCounter.model.consoleInterface;

import lineCounter.model.SystemHandler;
import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.interfaces.CommandsController;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;

public class ConsoleView implements KeyListener, WindowListener {

    private final CommandsController commandSystem;
    private ConsoleText text;
    private ConsoleFrame frameToClose;

    /* hash map used to handle uninitialized terminals */
    private static final HashMap<String, ConsoleText> terminals = new HashMap<>();

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
        /*
        * don't forget to remove the terminal from the map, so that it can be
        * replaced with a new one with the same name
        *  */
        terminals.remove(text.getName());
        frame.reset();
        return new String[] {""};
    }

    private void setText(ConsoleText text)
    {
        this.text = text;
    }

    /* we need to change the operations of some keys in this method */
    private void initializeTerminal(ConsoleText t)
    {
        /* the key ENTER should not start next line the way it typically does */
        terminals.put(t.getName(), t);
        Action enterHandle = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String com = t.getCommand();
                com = com.trim();
                commandSystem.processCommand(com);
            }
        };

        /* tab does the same as in all command lines */
        Action tabHandle = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String com = t.getCommand();
                t.deleteCommand();

                if (commandSystem.searchFirstThatContains(com) != null)
                    t.appendToPane(commandSystem.searchFirstThatContains(com));
                else if(! com.trim().equals(""))
                    t.appendToPane(com);
                else
                    t.appendToPane("");
            }
        };

        t.getInputMap().put(KeyStroke.getKeyStroke("TAB"), "tabHandling");
        t.getActionMap().put("tabHandling", tabHandle);

        t.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "enterHandling");
        t.getActionMap().put("enterHandling", enterHandle);

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        ConsoleText t = (ConsoleText) keyEvent.getSource();

        if(terminals.get(t.getName()) == null)
        {
            initializeTerminal(t);
        }

        /* perhaps this is not needed */
        frameToClose = t.panel().parent();

        commandSystem.setConsolePrinter(t);
        commandSystem.updateStdInStdOut(t, t);

        setText(t);

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
        if(keyEvent.getKeyCode() == KeyEvent.VK_N && keyEvent.isControlDown() ||
                keyEvent.getKeyCode() == KeyEvent.VK_TAB && keyEvent.isControlDown())
        {
            /* ctrl + n now opens a new window */
            setText(t);

            CommandSystem commandSystem_instance = (CommandSystem) commandSystem;
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
        /* remove it from hash map */
        terminals.remove(frameToClose.mainPanel().consoleText().getName());
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
