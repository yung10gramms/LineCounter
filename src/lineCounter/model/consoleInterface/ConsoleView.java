package lineCounter.model.consoleInterface;

import lineCounter.model.SystemHandler;
import lineCounter.model.consoleInterface.interfaces.CommandsController;
import lineCounter.model.consoleInterface.interfaces.InputDevice;
import lineCounter.model.consoleInterface.interfaces.OutputDevice;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ConsoleView implements KeyListener, WindowListener {

    CommandsController commandSystem;
    ConsoleText text;


    public ConsoleView(CommandsController commandSystem)
    {
        this.commandSystem = commandSystem;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    public void close()
    {
        text.panel().parent().setVisible(false);
        SystemHandler.removeDev(text.panel().parent().getNum());
    }

    public String[] clear()
    {
        ConsoleFrame frame = (ConsoleFrame) text.panel().parent();
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

        commandSystem.setConsolePrinter(t);
        commandSystem.updateStdInStdOut(t, t);
        if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
        {
            setText(t);
            String com = t.getCommand();
            com = com.trim();
            commandSystem.processCommand(com);
        }
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
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        ConsoleFrame fr = (ConsoleFrame)  windowEvent.getSource();
        if(SystemHandler.getNumberOfWindows() > 1)
        {
            fr.setVisible(false);
            fr = null;
            this.commandSystem.close();
            return;
        }
        this.commandSystem.exitOperation();
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