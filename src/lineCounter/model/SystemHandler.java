package lineCounter.model;

import lineCounter.model.commands.CommandSystem;
import lineCounter.model.commands.Command_;
import lineCounter.model.consoleInterface.ConsoleFrame;
import lineCounter.model.devices.Device;
import lineCounter.model.devices.Program;
import lineCounter.model.devices.rfiles.RFile;

import java.util.Objects;
import java.util.Vector;

public class SystemHandler {

    private static int numberOfWindows = 0;

    private static final Vector<ConsoleFrame> windows = new Vector<>();

    public static void main(String[] args)
    {
        newWindow();
    }

    public static Vector<Device> activeDevices = new Vector<>();

    public static final int TYPE_COMMAND = 1;
    public static final int TYPE_R_FILE = 2;
    public static final int TYPE_TERMINAL = 3;
    public static final int TYPE_NON_EXISTING = 3;

    private SystemHandler()
    {
        // no instantiation allowed
    }

    public static int getType(String word)
    {
        if(getDev(word.trim()) != null)
        {
            return TYPE_TERMINAL;
        }
        if(CommandSystem.getInstance().getCommandBySplitedString(word.trim()) != null)
        {
            return TYPE_COMMAND;
        }
        if(CommandSystem.getInstance().getFile(word) != null)
        {
            return TYPE_R_FILE;
        }
        return TYPE_NON_EXISTING;
    }

    public static Device toDevice(String name)
    {
        /* we need to read the first non-empty word that the user typed */
        String arg0 = getArgument(name, 0);

        int type = getType(arg0);
        switch (type)
        {
            case TYPE_COMMAND:
                Command_ commandFound = CommandSystem.getInstance().getCommandBySplitedString(arg0);
                String parameters = name.trim().replace(arg0.trim(), "");
                commandFound.setParams(parameters);

                return new Program(commandFound);
            case TYPE_TERMINAL:
                return Objects.requireNonNull(getDev(name.trim())).mainPanel().consoleText();
            case TYPE_R_FILE:
                //we'll come back
                return (Device) CommandSystem.getInstance().getFile(name);
        }
        return new RFile(CommandSystem.getInstance().getCurrentPath() + "/" +name);
    }

    public static String getArgument(String big_str_with_spaces, int index)
    {
        return big_str_with_spaces.trim().split(" ")[index];
    }

    public static void newWindow()
    {
        ConsoleFrame window = new ConsoleFrame(CommandSystem.getInstance(),
                CommandSystem.getInstance().getConsoleView());

        windows.add(window);
        activeDevices.add(window.mainPanel().consoleText());
        numberOfWindows++;
    }

    /** find in windows list */
    private static int find(int num)
    {
        System.out.println("trying to find num " + num);
        if(windows.size() == 0)
            return -1;
        for(int i = 0; i < windows.size(); i ++)
        {
            if(windows.get(i).getNum() == num)
                return i;
        }
        return -1;
    }

    public static void removeDev(int num)
    {

        int k = find(num);

        if(k < 0)
            return;
        try
        {
            activeDevices.remove(windows.get(k).mainPanel().consoleText());
        } catch (NullPointerException nex)
        {
            nex.printStackTrace();
        }
        windows.remove(k);
        numberOfWindows--;

        if(numberOfWindows == 0)
            System.exit(0);
    }

    /** find in windows list */
    public static ConsoleFrame getDev(String name)
    {
        if(windows.size() == 0)
            return null;
        for (ConsoleFrame window : windows) {
            if (window.getTitle().trim().contentEquals(name.trim()))
            {
                return window;
            }
        }
        return null;
    }


    public static int getNumberOfWindows()
    {
        return numberOfWindows;
    }

    public static int getNumberForName()
    {
        int counter = 0;

        boolean repeat = true;
        while(repeat)
        {
            repeat = false;
            for (ConsoleFrame window : windows) {
                if (counter == window.getNum()) {
                    counter++;
                    repeat = true;
                }
            }
        }
        return counter;
    }

}
