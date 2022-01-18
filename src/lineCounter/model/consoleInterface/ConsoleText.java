package lineCounter.model.consoleInterface;

import lineCounter.model.SystemHandler;
import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.interfaces.Colored;
import lineCounter.model.consoleInterface.interfaces.ConsolePrinter;
import lineCounter.model.consoleInterface.interfaces.InputTerminal;
import lineCounter.model.consoleInterface.interfaces.OutputTerminal;
import lineCounter.model.devices.Device;
import lineCounter.model.devices.InputDevice;
import lineCounter.model.devices.OutputDevice;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class ConsoleText extends JTextPane implements Colored, ConsolePrinter,
        InputTerminal, OutputTerminal, InputDevice, OutputDevice {
    private Theme theme;
    private static final Theme defaultTheme = new Theme(Color.black, Color.white, Color.green);

    private final Font defaultFont = new Font("Dialog", Font.PLAIN, 14);

    private final String openingString =
            "\n====================================================\n" +
            "Welcome to this console simulator. Type 'help' to view available commands\n" +
            "====================================================\n";
    private CommandSystem system;

    private String entireText;
    private String lastCommand;

    private ConsolePanel panel;

    private String[] bufferedData;

    private InputDevice inputDevice;
    private OutputDevice outputDevice;

    public ConsoleText(CommandSystem system, ConsolePanel panel)
    {
        System.gc();
        this.panel = panel;

        setInputDevice((InputDevice) this);
        setOutputDevice((OutputDevice) this);

        setColorsToDefaults();
        setFont(defaultFont);
        setText(openingString);
        setBounds(0, 0, 700, 500);
        setPreferredSize(new Dimension(700, 500));
        setMinimumSize(new Dimension(700, 500));

        this.system = system;
        //addKeyListener(system);
        addKeyListener(system.getConsoleView());
        System.out.println(getFont().getFontName());
        startNextLine();

    }

    public ConsolePanel panel()
    {
        return panel;
    }

    @Override
    public String getName()
    {
        return this.panel.parent().getName();
    }

    public void updateProgress(int per, int outOf)
    {
        deleteCommand();

        int entireLength = 50;
        int filled = (per/outOf)*entireLength;

        char[] st = new char[entireLength];
        for(int i = 0; i < entireLength; i ++)
        {
            if(i < filled)
            {
                st[i] = '=';
            }
            else
            {
                st[i] = '-';
            }
        }


        String generatedString = String.valueOf(st);
        appendToPane("\n"+generatedString, theme.getMisc());
    }

    public String[] clear()
    {
        ConsoleFrame frame = (ConsoleFrame) panel.parent();
        frame.reset();
        return new String[] {""};
    }

    public void deleteCommand()
    {
        DefaultStyledDocument doc = (DefaultStyledDocument) getStyledDocument();
        getText().replace(getText(), entireText);
        try
        {
            doc.remove(entireText.length(), getCommand().length());
        } catch (BadLocationException b)
        {
           System.out.println("exeption");
        }

    }

    public String getCommand()
    {
        lastCommand = getText();
        lastCommand.trim();
        entireText.trim();
        lastCommand = lastCommand.replace(entireText, "");
        bufferedData = new String[]{lastCommand};
        return lastCommand;
    }

    @Override
    public String input() {
        return null;
    }

    public void printCommandNotFound()
    {
        appendToPane("\nCommand not found\n", theme.getForeGround());
        startNextLine();
    }

    public void print(String s)
    {
        appendToPane(s);
    }

    public void startNextLine()
    {
        appendToPane(system.getCurrentPath()+"$", theme.getMisc());
        appendToPane(" ", theme.getForeGround());
        DefaultStyledDocument doc = (DefaultStyledDocument) getDocument();
        doc.setDocumentFilter(new Filter(getText().length()));
        entireText = getText();
        appendToPane(" ", theme.getForeGround());
    }

    @Override
    public String output() {
        return null;
    }

    public void appendToPane(String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = this.getDocument().getLength();
        this.setCaretPosition(len);
        this.setCharacterAttributes(aset, false);
        this.replaceSelection(msg);
    }

    public void appendToPane(String msg)
    {
        appendToPane(msg, theme.getForeGround());
    }

    private void setColorsToDefaults()
    {
        updateTheme(defaultTheme);
    }

    @Override
    public void updateTheme() {
        setBackground(theme.getBackGround());
        setForeground(theme.getForeGround());
    }

    /** method to update the local variable called theme, responsible for the
     * coloring of the U.I.
     * After that, it changes the colors as well
     * */
    @Override
    public void updateTheme(Theme t)
    {
        theme = t;
        updateTheme();
    }

    public void executeCommand(String[] output)
    {
        if(output == null)
        {
            return;
        }
        appendToPane( "\n", theme.getForeGround());
        for (int i = 0; i < output.length; i ++)
        {
            appendToPane( output[i] + "\n", theme.getForeGround());
        }
    }

    public void close()
    {
        panel().parent().setVisible(false);
        SystemHandler.removeDev(panel().parent().getNum());
        system.close();
    }

    //todo
    @Override
    public void passInput(String[] in) {
        bufferedData = in;
        String in_string = "\n";
        for(String buf : in)
        {
            in_string = in_string + buf + "\n";
        }
        in_string.replace("\n\n", "\n");
        appendToPane(in_string);
    }

    @Override
    public String[] passOutput() {
        return new String[]{getCommand()};
    }

    @Override
    public OutputDevice getOutputDevice() {
        return outputDevice;
    }

    @Override
    public InputDevice getInputDevice() {
        return inputDevice;
    }

    @Override
    public void setInputDevice(InputDevice inputDevice) {
        this.inputDevice = inputDevice;
    }

    @Override
    public void setOutputDevice(OutputDevice outputDevice) {
        this.outputDevice = outputDevice;
    }
}
