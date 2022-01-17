package lineCounter.model.consoleInterface;

import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.interfaces.Colored;

import javax.swing.*;
import java.awt.*;

public class ConsolePanel extends JPanel implements Colored {
    private Theme theme;
    private static final Theme defaultTheme = new Theme(Color.black, Color.white, Color.green);

    private ConsoleText consoleText;
    private CommandSystem system;
    private ConsoleFrame parent;

    public static final int preferred_width = 700;
    public static final int preferred_height = 500;

    public ConsolePanel(CommandSystem system, ConsoleFrame parent)
    {
        this.system = system;
        this.parent = parent;


        setColorsToDefaults();
        setBounds(0, 0, preferred_width, preferred_height);
        setPreferredSize(new Dimension(preferred_width, preferred_height));
        setMinimumSize(new Dimension(preferred_width, preferred_height));

        consoleText = new ConsoleText(system, this);

        /* code for disabling up/down */
        InputMap im = consoleText.getInputMap(WHEN_FOCUSED);
        ActionMap am = consoleText.getActionMap();

        am.get("caret-down").setEnabled(false);
        am.get("caret-up").setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(consoleText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        final int dxy = 20;
        Dimension dimension_for_scroll_pane = new Dimension(preferred_width - dxy, preferred_height - dxy);
        scrollPane.setSize(dimension_for_scroll_pane);
        scrollPane.setPreferredSize(dimension_for_scroll_pane);
        scrollPane.getVerticalScrollBar().setForeground(getBackground());
        scrollPane.getVerticalScrollBar().setBackground(getBackground());
        add(scrollPane);

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

    public ConsoleFrame parent()
    {
        return parent;
    }

    public ConsoleText consoleText()
    {
        return consoleText;
    }
}
