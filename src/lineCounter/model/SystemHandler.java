package lineCounter.model;

import lineCounter.model.commands.CommandSystem;
import lineCounter.model.consoleInterface.ConsoleFrame;

import java.util.Vector;

public class SystemHandler {

    private static int numberOfWindows = 0;

    private static Vector<ConsoleFrame> windows = new Vector<>();

    public static void main(String[] args)
    {
        newWindow();
    }

    public static void newWindow()
    {
        windows.add(new ConsoleFrame(CommandSystem.getInstance(), CommandSystem.getInstance().getConsoleView()));
        numberOfWindows++;
    }

    public static ConsoleFrame getDev(int num)
    {
        return windows.get(num);
    }

    private static int find(int num)
    {
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
        windows.remove(k);
        numberOfWindows--;
        if(numberOfWindows == 0)
            System.exit(0);
    }

    public static ConsoleFrame getDev(String name)
    {
        if(windows.size() == 0)
            return null;
        for (ConsoleFrame window : windows) {
            if (window.getTitle().contentEquals(name))
                return window;
        }
        return null;
    }

    public static int getNumberOfWindows()
    {
        return numberOfWindows;
    }
}
