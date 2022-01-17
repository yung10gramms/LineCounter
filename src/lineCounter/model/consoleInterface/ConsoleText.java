package lineCounter.model.consoleInterface;

import lineCounter.model.SystemHandler;
import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.interfaces.Colored;
import lineCounter.model.consoleInterface.interfaces.ConsolePrinter;
import lineCounter.model.consoleInterface.interfaces.InputDevice;
import lineCounter.model.consoleInterface.interfaces.OutputDevice;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class ConsoleText extends JTextPane implements Colored, ConsolePrinter, InputDevice, OutputDevice {
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

    public ConsoleText(CommandSystem system, ConsolePanel panel)
    {
        System.gc();
        this.panel = panel;

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
            print("ex");
        }

    }

    public String getCommand()
    {
        lastCommand = getText();
        lastCommand.trim();
        entireText.trim();
        lastCommand = lastCommand.replace(entireText, "");
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
        System.out.println(s);
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

}
