package lineCounter.model.commands;

import lineCounter.model.LineCounterClass;

import java.util.Collections;
import java.util.Vector;

public class CommandDatabase {

    public static final String tabSpaces = "                  ";
    private static boolean generated = false;
    private static final Vector<Command_> commands = getCommands();

    private CommandDatabase()
    {

    }

    public static Vector<Command_> getCommands()
    {
        if(generated)
            return commands;

        Vector<Command_> output = new Vector<>();
        output.add(Command_.getCommand_("help", "prints all commands"));
        output.add(Command_.getCommand_("linecounter", "counts the lines"));
        output.add(Command_.getCommand_("man", "manual for a certain command"));
        output.add(Command_.getCommand_("cd", "change directory"));
        output.add(Command_.getCommand_("ls", "list dir contents"));
        output.add(Command_.getCommand_("exit", "close window"));
        output.add(Command_.getCommand_("close", "close window"));
        output.add(Command_.getCommand_("clear", "clears console"));
        output.add(Command_.getCommand_("clc", "clears console"));
        output.add(Command_.getCommand_("cmd", "view command history"));
        output.add(Command_.getCommand_("ram", "view ram settings"));
        output.add(Command_.getCommand_("win", "open new terminal"));
        output.add(Command_.getCommand_("gc", "clear ram"));
        output.add(Command_.getCommand_("terminate", "terminate application"));
        output.add(Command_.getCommand_("echo", "print stdin to stdout"));
        output.add(Command_.getCommand_("cat", "print a file to the stdout"));
        output.add(Command_.getCommand_("ch", "clear command history"));
        output.add(Command_.getCommand_("ld", "undo clear history"));
        output.add(Command_.getCommand_("lcc", "line counter non comments"));
        output.add(Command_.getCommand_("jLin", "line counter non comments for java code"));
        output.add(Command_.getCommand_("cLin", "line counter non comments for c code"));
        output.add(Command_.getCommand_("klk", "h kolokythia"));
        output.add(Command_.getCommand_("devs", "view active devices"));
        output.add(Command_.getCommand_("grep", "find pattern in file"));
        output.add(Command_.getCommand_("env", "get environment variables"));
        output.add(Command_.getCommand_("whoami", "get user name"));


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
                " for moving ti the parent directory is the same as linux (that is cd ..), and you cannot move more than" +
                " one directory upwards at a time by typing \"../..\".");
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
        output.get(11).setManual("none", "Opens a new window (shortcut: Ctrl+n). " +
                "This will not affect anything, " +
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
        output.get(21).setManual("none", "The classic non-fun game of kolokythia.");
        output.get(22).setManual("none", "See active devices. There is a chance that" +
                "programs are never killed. This does not affect the functionality of the program, nor " +
                "do they occupy some space, preventing other programs from being executed, except for their" +
                " physical space in RAM, of course.");
        output.get(23).setManual("pattern (and file to search in)", "Search for patterns " +
                "in a directory or a regular file. The syntax of the command is like this: " +
                LineCounterClass.startHighlight+"grep -f dir/to/search" +LineCounterClass.endHighlight+
                " -e pattern/word to search. One can place the parameters for the pattern first, but only after" +
                " the -e keyword. E.g."+ LineCounterClass.startHighlight +" grep -e System.out.print -f src" +
                LineCounterClass.endHighlight+ ": search for the word System.out.println in the current path/src " +
                "directory. Also:" + LineCounterClass.startHighlight +" grep -e System.out.print"  +
                                LineCounterClass.endHighlight+ ": search for the word in question in the current" +
                " directory. See more about paths and directories in the manual of the cd command.");
        output.get(24).setManual("none", "Get environment variables, such as working " +
                "directory, user info, o.s. information etc.");
        output.get(25).setManual("none", "returns the user who is currently active" +
                " at the present computer.");
        Collections.sort(output);

        generated = true;

        return output;
    }
}
