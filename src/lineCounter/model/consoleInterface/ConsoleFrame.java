package lineCounter.model.consoleInterface;

import lineCounter.model.SystemHandler;
import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.interfaces.Colored;

import javax.swing.*;
import java.awt.*;

public class ConsoleFrame extends JFrame implements Colored
{
    private Theme theme;
    private static final Theme defaultTheme = new Theme(Color.black, Color.white, Color.green);
    private ConsolePanel mainPanel;
    private final CommandSystem system;
    private final int num;


    public int getNum()
    {
        return num;
    }

    public ConsoleFrame(CommandSystem system, ConsoleView viewer)
    {
        this.num = SystemHandler.getNumberForName();
        setTitle("dev" + num);
        setName(getTitle());

        setColorsToDefaults();

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.system = system;

        mainPanel = new ConsolePanel(system, this);
        setContentPane(mainPanel);
        pack();
        //setResizable(false);
        setVisible(true);

        addWindowListener(viewer);
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

    public void reset()
    {
        mainPanel = new ConsolePanel(system, this);
        setContentPane(mainPanel);
    }

    public ConsolePanel mainPanel()
    {
        return mainPanel;
    }

}
