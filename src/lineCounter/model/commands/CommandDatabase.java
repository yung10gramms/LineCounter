package lineCounter.model.commands;

import java.util.Collections;
import java.util.Vector;

public class CommandDatabase {

    public static final String tabSpaces = "                  ";

    public static Vector<Command_> getCommands()
    {
        Vector<Command_> output = new Vector<>();
        output.add(new Command_("help", "prints all commands"));
        output.add(new Command_("linecounter", "counts the lines"));
        output.add(new Command_("man", "manual for a certain command"));
        output.add(new Command_("cd", "change directory"));
        output.add(new Command_("ls", "list dir contents"));
        output.add(new Command_("exit", "close window"));
        output.add(new Command_("close", "close window"));
        output.add(new Command_("clear", "clears console"));
        output.add(new Command_("clc", "clears console"));
        output.add(new Command_("cmd", "view command history"));
        output.add(new Command_("ram", "view ram settings"));
        output.add(new Command_("win", "open new terminal"));
        output.add(new Command_("gc", "clear ram"));
        output.add(new Command_("terminate", "terminate application"));
        output.add(new Command_("echo", "print stdin to stdout"));
        output.add(new Command_("cat", "print a file to the stdout"));
        output.add(new Command_("ch", "clear command history"));
        output.add(new Command_("ld", "undo clear history"));
        output.add(new Command_("lcc", "line counter non comments"));
        output.add(new Command_("jLin", "line counter non comments for java code"));
        output.add(new Command_("cLin", "line counter non comments for c code"));
        output.add(new Command_("klk", "h kolokythia"));

        output.get(0).setManual("none",
                "Pretty self explanatory. " +
                "It just prints all available commands in the worst possible way.\n");
        output.get(1).setManual("\ncommand:\n"+tabSpaces+ "linecounter\n"+"params:\n"+ tabSpaces +"none"
        +"\ndescription:\n"+ tabSpaces +"Counts the lines of code in the current directory and all its " +
                "sub-directories\nalternatively:" +
                "\n\nparams:\n"+tabSpaces+"path to search for source code files (in java the */src/ folder). See more " +
                "about paths in this projects at <help cd> \n\n");
        output.get(2).setManual("name of the command", "Prints the manual for a selected command, " +
                "which can be inserted as a parameter next to the word man. Pretty similar to the linux command man.");
        output.get(3).setManual("path (relative or absolute)", "changes the current directory." +
                " It does not move any files from directory to directory. However, it might (try to) save your command" +
                " history in your at the time selected directory when you close it. For compatibility reasons, and " +
                "seeing that this is a rather small project, paths are changeable and not that solid. By the way, " +
                "in LineCounter project, relative paths don't start with \"/\", only absolute paths do. the command" +
                " for moving ti the parent directory is the same as linux (that is cd ..), but you cannot move more than" +
                " one directory upwards at a time.");
        output.get(4).setManual("none", "list files in current directory. Such files can be " +
                "regular files, or directories.");
        output.get(5).setManual("none", "absolutely equivalent to the command \"close\", " +
                "as well as" +
                " Alt+F4 and window closing. " +
                "If the current window is the only one, it terminates the entire program. Else, it just closes the " +
                "selected window.");
        output.get(6).setManual("none", "absolutely equivalent to the command \"exit\", " +
                "as well as" +
                " Alt+F4 and window closing. " +
                "If the current window is the only one, it terminates the entire program. Else, it just closes the " +
                "selected window.");
        output.get(7).setManual("none", "Clears console. Just like the command \"clc\". " +
                "These two commands DO NOT clear your command history.");
        output.get(8).setManual("none", "Clears console. Just like the command \"clear\". " +
                "These two commands DO NOT clear your command history.");
        output.get(9).setManual("none", "View command history. The command history is " +
                "stored in a FIFO list, so you might want to scroll way down to view your latest commands. Note: you " +
                "can see latest commands with up/down arrow keys.");
        output.get(10).setManual("none", "View ram information. Reminder: we are running " +
                "on a java virtual machine. So all RAM info refers to the currently running VM.");
        output.get(11).setManual("none", "Opens a new window. This will not affect anything, " +
                "the two (or more) windows will be totally independent, and close/exit operations will only close " +
                "the selected Frame. Nonetheless, there is a chance that your command history gets tangled, since its " +
                "global. This could fix if each window was working on a different Directory (see the \"cd\" command).");
        output.get(12).setManual("none", "Calls the java System garbage collector. Totally safe " +
                "to use.");
        output.get(13).setManual("none", "Updates the command history," +
                " closes all windows, and then terminates the program.");
        output.get(14).setManual("string to print to the terminal", "Print stdin to stdout. " +
                "This program can't change " +
                "the stdout nor can it pipe the output of a function to the input of a different one. Echo is as" +
                " simple as it sounds.");
        output.get(15).setManual("file name/absolute path",
                "Prints the file into the std output, that is, the very screen you are looking at " +
                        "right now. If the file is a directory, then it concatenates all the regular files onto the" +
                        " standard output.");
        output.get(16).setManual("none", "Clears the command history. If before the closing, " +
                "the command \"ld\" is called, then the history is saved again.");
        output.get(17).setManual("none", "It is clear that the \"ch\" command " +
                "clears the command history. If before the closing, " +
                "the command \"ld\" is called, then the history is saved again.");
        output.get(18).setManual("none/path", "The parameter system is same as linecounter (see" +
                " command linecounter). Just like the former, the latter counts lines in a directory, but " +
                "this one skips empty lines and comments.");
        output.get(19).setManual("none/path", "Like the \"lcc\" command, but only for .java " +
                "files");
        output.get(20).setManual("none/path", "Like the \"lcc\" command, but only for .c and .h " +
                "files");

        Collections.sort(output);
        return output;
    }
}
