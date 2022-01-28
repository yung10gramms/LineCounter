package lineCounter.model.consoleInterface;

import lineCounter.model.LineCounterClass;
import lineCounter.model.SystemHandler;
import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.interfaces.Colored;
import lineCounter.model.consoleInterface.interfaces.terminal.InputTerminal;
import lineCounter.model.consoleInterface.interfaces.terminal.OutputTerminal;
import lineCounter.model.devices.InputDevice;
import lineCounter.model.devices.OutputDevice;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class ConsoleText extends JTextPane implements Colored,
        InputTerminal, OutputTerminal, InputDevice, OutputDevice {
    private Theme theme;
    private static final Theme defaultTheme = new Theme(Color.black, Color.white, Color.green);

    private static boolean firstTime = true;

    private static final Font defaultFont = new Font("Dialog", Font.PLAIN, 14);

    private final CommandSystem system;

    private String entireText;

    private final ConsolePanel panel;

    private InputDevice inputDevice;
    private OutputDevice outputDevice;

    public ConsoleText(CommandSystem system, ConsolePanel panel)
    {
        System.gc();
        this.panel = panel;

        setInputDevice(this);
        setOutputDevice(this);

        setColorsToDefaults();
        setFont(defaultFont);
        String openingString = "\n====================================================\n" +
                "Welcome to this console simulator. Type 'help' to view available commands\n" +
                "====================================================\n";
        if(firstTime)
            setText(openingString);
        setBounds(0, 0, 700, 500);
        setPreferredSize(new Dimension(700, 500));
        setMinimumSize(new Dimension(700, 500));

        this.system = system;
        addKeyListener(system.getConsoleView());
        startNextLine();
        firstTime = false;
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

    public String[] clear()
    {
        ConsoleFrame frame = panel.parent();
        frame.reset();
        return new String[] {""};
    }

    public void deleteCommand()
    {
        DefaultStyledDocument doc = (DefaultStyledDocument) getStyledDocument();
        //getText().replace(getText(), entireText);
        try
        {
            doc.remove(entireText.length(), getCommand().length());
        } catch (BadLocationException b)
        {
            /* skip exception */
        }

    }

    public String getCommand()
    {
        String lastCommand = getText();

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
        appendToPane(s);
    }

    public void startNextLine()
    {
        setTextForConsole();
        DefaultStyledDocument doc = (DefaultStyledDocument) getDocument();
        doc.setDocumentFilter(new Filter(getText().length()));
        entireText = getText();
        appendToPane(" ", theme.getForeGround());
        this.setCaretPosition( this.getDocument().getLength() - 1 );
    }

    @Override
    public String output() {
        return null;
    }

    public void appendToPane(String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet attributeSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        attributeSet = sc.addAttribute(attributeSet, StyleConstants.FontFamily, "Lucida Console");
        attributeSet = sc.addAttribute(attributeSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = this.getDocument().getLength();
        this.setCaretPosition(len);
        this.setCharacterAttributes(attributeSet, false);
        this.replaceSelection(msg);
    }

    public void addWithHighlights(String msg, String msgToHighLight)
    {
        String[] msgArray = msg.split("\n");


        for(String cur : msgArray)
        {
            if(cur.contains(msgToHighLight))
            {
                String[] to_print = cur.split(msgToHighLight);
                int count = 0;
                for(String printerHelp : to_print)
                {
                    appendToPane(printerHelp);
                    if(count < to_print.length - 1)
                    {
                        appendToPane(msgToHighLight, theme.getMisc());
                    }
                    count++;
                }
            } else
            {
                appendToPane(cur);
            }
            appendToPane("\n");
        }
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
            appendToPane( "\n", theme.getForeGround());
            return;
        }
        appendToPane( "\n", theme.getForeGround());
        for (String s : output) {
            appendToPane(s + "\n", theme.getForeGround());
        }
    }

    public void close()
    {
        panel().parent().setVisible(false);
        SystemHandler.removeDev(panel().parent().getNum());
        system.close();
    }


    @Override
    public void passInput(String[] in) {
        StringBuilder in_string = new StringBuilder("\n");
        for(String buf : in)
        {
            in_string.append(buf).append("\n");
        }
        //in_string.toString().replace("\n\n", "\n");

        if(in_string.toString().contains(LineCounterClass.startHighlight)
                && in_string.toString().contains(LineCounterClass.endHighlight))
        {

            String highlightedText =
                    in_string.substring(in_string.indexOf(LineCounterClass.startHighlight));
            highlightedText =
                    highlightedText.substring(0, highlightedText.indexOf(LineCounterClass.endHighlight));
            highlightedText = highlightedText.replace(LineCounterClass.endHighlight, "");
            highlightedText = highlightedText.replace(LineCounterClass.startHighlight, "");

            String out = in_string.toString().replace(LineCounterClass.endHighlight, "");
            out = out.replace(LineCounterClass.startHighlight, "");

            addWithHighlights(out, highlightedText);
        }
        else
        {
            appendToPane(in_string.toString().replace("\n\n", "\n"));
        }

    }

    public void setTextForConsole()
    {
        if(! Arrays.toString(system.getEnv()).contains("ubuntu"))
        {
            String printInRight = system.getCurrentPath() + "$";
            if(! getText().endsWith("\n"))
                appendToPane("\n", theme.getMisc());
            appendToPane(printInRight, theme.getMisc());
            return;
        }

        Map<String, String> envMap = System.getenv();
        String string_1 = envMap.get("SESSION_MANAGER").split("/")[1];
        if(string_1.contains(":@"))
        {
            string_1 = string_1.replace(":@", "");
        }
        String userName = envMap.get("PATH").split("/")[2];

        String builder = userName +
                "@" +
                string_1;

        String pathString;
        if(system.getCurrentPath().startsWith("/home/"+userName))
        {
            if(system.getCurrentPath().contentEquals("/home/"+userName) ||
                    system.getCurrentPath().contentEquals("/home/"+userName+"/"))
            {
                pathString = "~";
            } else
            {
                pathString = system.getCurrentPath().replace("/home/"+userName, "~");
            }
        } else
        {
            pathString = system.getCurrentPath();
        }
        if(! getText().endsWith("\n"))
            appendToPane("\n", theme.getMisc());
        appendToPane(builder, theme.getMisc());
        appendToPane(":");
        Color color = new Color(80, 100, 240);
        appendToPane(pathString, color);
        appendToPane("$");

        appendToPane(" ", theme.getForeGround());

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
