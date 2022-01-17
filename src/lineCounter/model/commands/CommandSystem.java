package lineCounter.model.commands;

import lineCounter.model.LineCounterClass;
import lineCounter.model.SystemHandler;
import lineCounter.model.consoleInterface.*;
import lineCounter.model.consoleInterface.interfaces.CommandsController;
import lineCounter.model.consoleInterface.interfaces.ConsolePrinter;
import lineCounter.model.consoleInterface.interfaces.InputDevice;
import lineCounter.model.consoleInterface.interfaces.OutputDevice;
import lineCounter.model.serialize.StorageHandler;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Vector;

public class CommandSystem implements CommandsController {
    private String currentPath;

    private final Vector<Command_> system_commands;
    private Vector<String> commandsInserted;

    private int currentCommandIndex;

    private ConsolePrinter consolePrinter;

    private static final String[] no_output_str = {""};
    private static final String[] command_not_found = {"\ncommand not found\n"};

    private static final CommandSystem instance = new CommandSystem();
    public static CommandSystem getInstance()
    {
        return instance;
    }

    private ConsoleView consoleView;

    private static final int COUNT_ALL = 1;
    private static final int COUNT_NON_EMPTY = 2;
    private static final int COUNT_JAVA = 3;
    private static final int COUNT_C = 4;

    private CommandSystem()
    {
        currentPath = System.getProperty("user.dir");
        system_commands = CommandDatabase.getCommands();
        commandsInserted = StorageHandler.loadCommandHistory(currentPath);
        consoleView = new ConsoleView(this);
    }

    public ConsoleView getConsoleView()
    {
        return this.consoleView;
    }

    public String getCurrentPath()
    {
        return currentPath;
    }

    public void moveCommandsDownwards()
    {
        if(currentCommandIndex < commandsInserted.size()-1) {
            currentCommandIndex++;
            consolePrinter.print(commandsInserted.get(currentCommandIndex));
        } else
        {
            consolePrinter.print("");
        }
    }

    public void moveCommandsUpwards()
    {
        if(currentCommandIndex > 0)
            currentCommandIndex--;
        if(currentCommandIndex >= 0 && commandsInserted.size() != 0)
            consolePrinter.print(commandsInserted.get(currentCommandIndex));
    }

    public void setConsolePrinter(ConsolePrinter consolePrinter)
    {
        this.consolePrinter = consolePrinter;
    }

    public void updateStdInStdOut(InputDevice in, OutputDevice out)
    {
        for(int i = 0; i < system_commands.size(); i ++)
        {
            system_commands.get(i).setInputDevice(in);
            system_commands.get(i).setOutputDevice(out);
        }
    }

    public void processCommand(String command_string)
    {
        Command_ newCommand = searchCommand(command_string);

        if(! command_string.contentEquals(""))
        {
            if(newCommand == null)
            {
                consolePrinter.printCommandNotFound();
            }
            else
            {
                handleCommand(newCommand, consolePrinter);
            }
            commandsInserted.add(command_string);
        }
        currentCommandIndex = commandsInserted.size() - 1;
        consolePrinter.startNextLine();
    }

    private void handleCommand(Command_ command, ConsolePrinter textPane)
    {
        System.out.println("we are handling the command " + command.getName());
        this.consolePrinter = textPane;
        //textPane = command.getOutputDevice();
        if(command.getName().contentEquals("help"))
        {
            command.getOutputDevice().executeCommand(help());
        }
        if(command.getName().contains("linecounter"))
        {
            System.out.println(command.getName());
            command.getOutputDevice().executeCommand(lineCounter(command));
        }
        if(command.getName().contentEquals("ls"))
        {
            command.getOutputDevice().executeCommand(ls());
        }
        if(command.getName().contains("cd"))
        {
            command.getOutputDevice().executeCommand(cd(command));
            fixPath();
        }
        if(command.getName().contentEquals("exit") || command.getName().contentEquals("close") )
        {
            close();
        }
        if(command.getName().contentEquals("terminate") )
        {
            exitOperation();
        }
        if(command.getName().contentEquals("clear") || command.getName().contentEquals("clc"))
        {
            command.getOutputDevice().executeCommand(consoleView.clear());
            System.gc();
        }
        if(command.getName().contentEquals("cmd"))
        {
            command.getOutputDevice().executeCommand(cmd());
        }
        if(command.getName().contentEquals("ram"))
        {
            command.getOutputDevice().executeCommand(ram());
        }
        if(command.getName().contentEquals("win"))
        {
            command.getOutputDevice().executeCommand(win());
        }
        if(command.getName().contentEquals("man"))
        {
            command.getOutputDevice().executeCommand(man(searchCommand(getNextParam(command).trim())));
        }
        if(command.getName().contentEquals("gc"))
        {
            command.getOutputDevice().executeCommand(gc());
        }
        if(command.getName().contentEquals("echo"))
        {
            command.getOutputDevice().executeCommand(echo(command));
        }
        if(command.getName().contentEquals("cat"))
        {
            command.getOutputDevice().executeCommand(cat(command));
        }
        if(command.getName().contentEquals("ch"))
        {
            command.getOutputDevice().executeCommand(ch());
        }
        if(command.getName().contentEquals("ld"))
        {
            command.getOutputDevice().executeCommand(ld());
        }
        if(command.getName().contentEquals("lcc"))
        {
            command.getOutputDevice().executeCommand(lcc(command));
        }
        if(command.getName().contentEquals("jLin"))
        {
            command.getOutputDevice().executeCommand(jLin(command));
        }
        if(command.getName().contentEquals("cLin"))
        {
            command.getOutputDevice().executeCommand(cLin(command));
        }
        if(command.getName().contentEquals("klk"))
        {
            command.getOutputDevice().executeCommand(klk(command.getOutputDevice(), 5000));
        }
    }

    public void close()
    {
        if(SystemHandler.getNumberOfWindows() > 1)
        {
            consoleView.close();

            return;
        }
        exitOperation();
    }

    public String getNextParam(Command_ command)
    {

        for(int j = 0; j < command.getParams().length; j ++)
        {
            if(command.getParams()[j] != null && ! command.getParams()[j].contentEquals(""))
                return command.getParams()[j];
        }
        return "";
    }

    public void exitOperation()
    {

        StorageHandler.storeCommandsString(currentPath, commandsInserted);
        exit();
    }

    private void fixPath()
    {
        currentPath = currentPath.replace("//", "/");
    }



    private Command_ searchCommand(String name)
    {
        final String splitToken = " | ";
        String[] commandsPiped = name.split(splitToken);

        if(! name.contains(splitToken))
            return searchCommandByString(name);

        for(int i = 0; i < commandsPiped.length; i ++)
        {
            if(! commandsPiped[i].trim().equals(splitToken.trim()) && ! commandsPiped[i].trim().equals(""))
            {
                System.out.println("<"+ commandsPiped[i] + ">");
                Command_ command = searchCommandByString(commandsPiped[i]);
                if(command != null)
                {
                    try
                    {
                        System.out.println("/"+ commandsPiped[i].trim() +"/");
                        command.setOutputDevice(SystemHandler.getDev(
                                commandsPiped[i + 2].trim()).mainPanel().consoleText());
                        return command;
                    } catch (NullPointerException nex)
                    {
                        return null;
                    } catch (ArrayIndexOutOfBoundsException aex)
                    {
                        return null;
                    }
                }

            }
        }
        return null;
    }


    private Command_ searchCommandByString(String name)
    {
        name = name.trim();
        String name_to_search = name.split(" ")[0];
        String[] command_in_strings = name.split(" ");
        for (Command_ system_command : system_commands) {
            if (system_command.getName().contentEquals(name_to_search)) {
                String params_in_str = "";
                for (int k = 0; k < command_in_strings.length - 1; k++) {
                    params_in_str = params_in_str + " " + command_in_strings[k + 1];
                }
                system_command.setParams(params_in_str);
                return system_command;
            }
        }
        return null;
    }

    public String[] help()
    {
        final int len = 80;
        String[] coms = new String[system_commands.size()];
        for (int i = 0; i < system_commands.size(); i ++)
        {
            coms[i] = system_commands.get(i).getName();
        }
        for(int i = 0; i < coms.length; i ++)
        {
            int comSize = coms[i].length();
            int descSize = system_commands.get(i).getDescription().length();
            int rest = len - comSize - descSize;
            String dots = "";
            for(int j = 0; j < rest; j ++)
            {
                dots = dots + ".";
            }
            coms[i] = coms[i] + dots + system_commands.get(i).getDescription();
        }
        return coms;
    }

    public String[] cLin(Command_ command)
    {
        return linesHelp(command, COUNT_C);
    }

    public String[] jLin(Command_ command)
    {
        return linesHelp(command, COUNT_JAVA);
    }

    public String[] lcc(Command_ command)
    {
        return lineCountersHelp(command, false);
    }

    public String[] lineCounter(Command_ command)
    {
        return lineCountersHelp(command, true);
    }

    private String[] lineCountersHelp(Command_ command, boolean countAll)
    {
        int caseInt = 0;
        if(countAll)
            caseInt = COUNT_ALL;
        else
            caseInt = COUNT_NON_EMPTY;
        return  linesHelp(command, caseInt);
    }

    private String[] linesHelp(Command_ command, int use)
    {
        String[] out = new String[2];
        if(command.getParams() == null || command.getParams().length == 0 ||
                command.getParams()[0] == null || command.getParams()[0].contentEquals(""))
        {
            Path path = Paths.get(currentPath);
            if(Files.exists(path))
            {
                try
                {
                    LineCounterClass lin = new LineCounterClass(currentPath, currentPath);
                    out[0] = "line counter: ";
                    if(use == COUNT_ALL)
                        out[1] = CommandDatabase.tabSpaces +lin.getNumAll() + " lines";
                    else if(use == COUNT_NON_EMPTY)
                        out[1] = CommandDatabase.tabSpaces +lin.getNumNonEmpty() + " lines";
                    else if(use == COUNT_JAVA)
                        out[1] = CommandDatabase.tabSpaces +lin.getNumJava() + " lines";
                    else if(use == COUNT_C)
                        out[1] = CommandDatabase.tabSpaces +lin.getNumC() + " lines";
                }
                catch (NullPointerException nex)
                {
                    out[0] = "line counter: ";
                    out[1] = "            command for counting lines in a certain directory. Please type it" +
                            "in the format 'linecounter <path/to/directory>'";
                }
            } else
            {
                out[0] = "line counter: ";
                out[1] = "            command for counting lines in a certain directory. Please type it" +
                        "in the format 'linecounter <path/to/directory>'";

            }
        }
        else
        {
            String path = command.getParams()[0];
            LineCounterClass lin = new LineCounterClass(path, currentPath);
            out[0] = "line counter: ";

            if(use == COUNT_ALL)
                out[1] = CommandDatabase.tabSpaces +lin.getNumAll() + " lines";
            else if(use == COUNT_NON_EMPTY)
                out[1] = CommandDatabase.tabSpaces +lin.getNumNonEmpty() + " lines";
            else if(use == COUNT_JAVA)
                out[1] = CommandDatabase.tabSpaces +lin.getNumJava() + " lines";
            else if(use == COUNT_C)
                out[1] = CommandDatabase.tabSpaces +lin.getNumC() + " lines";
        }
        return out;
    }

    public String[] ls()
    {
        File[] filesList = new File(currentPath).listFiles();
        if(filesList == null)
            return no_output_str;
        String[] names = new String[filesList.length];
        for(int i = 0; i < filesList.length; i ++)
        {
            names[i] = filesList[i].getName();
        }
        String names_as_string = "";
        for(int i = 0; i < names.length; i ++)
        {
            names_as_string = names_as_string + "    " + names[i];
        }
        names_as_string = names_as_string.trim();
        return new String[]{names_as_string};
    }

    public String[] cd(Command_ command)
    {
        String path_in_cd = getNextParam(command);
        if(path_in_cd.length() == 0)
        {
            return no_output_str;
        }
        if(path_in_cd.charAt(0) == '/')
        {
            return cdHelp(command, path_in_cd);
        }
        else if(path_in_cd.charAt(0) == '.')
        {
            if(! path_in_cd.contentEquals(".."))
            {
                return new String[]{"false input"};
            }
            else
            {
                String[] parentDir = currentPath.split("/");
                int lastIndex = 0;
                if(parentDir[parentDir.length - 1].equals("/"))
                {
                    lastIndex = parentDir.length - 2;
                }
                else
                {
                    lastIndex = parentDir.length - 1;
                }
                String[] builtStrArray = new String[lastIndex];
                path_in_cd = "";
                for(int i = 0; i < lastIndex; i ++)
                {
                    builtStrArray[i] =  parentDir[i];
                }
                for(int i = 0; i < lastIndex; i ++)
                {
                    path_in_cd = path_in_cd + "/" + builtStrArray[i];
                }
                path_in_cd = path_in_cd + "/";

                return cdHelp(command, path_in_cd);
            }
        }
        else //child dir
        {
            return cdHelp(command, currentPath + "/" +path_in_cd);
        }

    }

    private String[] cdHelp(Command_ command, String path_in_cd)
    {
        Path path = Paths.get(path_in_cd);

        if(Files.exists(path))
        {
            File file = new File(path_in_cd);
            if(file.isDirectory())
            {
                //cool
                currentPath = path_in_cd;
                return no_output_str;
            } else
            {
                String[] out_str = {"cannot use cd in regular files (non-dirs)"};
                return out_str;
            }
        }
        else
        {
            String[] out_str = {"cannot cd to non-existing directory"};
            return out_str;
        }
    }



    public String[] exit()
    {
        System.exit(0);
        return no_output_str;
    }

    public String[] cmd()
    {
        String[] out = (String[]) commandsInserted.toArray(new String[commandsInserted.size()]);
        if(out == null || out.length == 0)
            return no_output_str;
        return out;
    }

    public String[] ram()
    {
        String[] out = new String[4];
        Runtime runtime = Runtime.getRuntime();


        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        NumberFormat format = NumberFormat.getInstance();

        int denominator = 1024 * 1024;
        
        out[0] = "free memory: " + format.format(freeMemory / denominator) + "kBytes";
        out[1] = "allocated memory (for vm): " + format.format(allocatedMemory / denominator) + "kBytes";
        out[2] = "max memory: " + format.format(maxMemory / denominator) + "kBytes";
        out[3] = "total free memory: " +
                format.format((freeMemory + (maxMemory - allocatedMemory)) / denominator) + "kBytes";
        return out;
    }

    public String[] win()
    {
        SystemHandler.newWindow();
        return no_output_str;
    }

    public String[] gc()
    {
        System.gc();
        return no_output_str;
    }

    public String[] man(Command_ command)
    {
        if(command == null)
        {
            return command_not_found;
        }
        String[] out = {command.getManual()};
        return out;
    }

    public String[] echo(Command_ command)
    {
        if(command.getParams() == null || command.getParams().length == 0)
            return no_output_str;
        String out = "";
        for(int i = 0; i < command.getParams().length; i ++)
        {
            out = out + " " + command.getParams()[i];
        }
        out = out.trim();
        return new String[]{out};
    }

    public String[] cat(Command_ command)
    {
        String[] out = new String[2];
        if(command.getParams() == null || command.getParams().length == 0 ||
                command.getParams()[0] == null || command.getParams()[0].contentEquals(""))
        {

            Path path = Paths.get(currentPath);
            if(Files.exists(path))
            {
                try
                {
                    LineCounterClass lin = new LineCounterClass(currentPath, currentPath);
                    Vector<String> data = lin.getLines();
                    out = data.toArray(new String[data.size()]);
                }
                catch (NullPointerException nex)
                {
                    out[0] = "cat status: ";
                    out[1] = "failed";
                }
            } else
            {
                out[0] = "cat status: ";
                out[1] = "failed";

            }
        }
        else
        {

            String path_string = command.getParams()[0];
            Path path_instance = Paths.get(path_string);

            if(Files.exists(path_instance))
            {
                LineCounterClass lin = new LineCounterClass(path_string, currentPath);
                Vector<String> data = lin.getLines();
                out = data.toArray(new String[data.size()]);
            }
            String absolute_path = currentPath + "/" + path_string;
            absolute_path = absolute_path.replace("//", "/");
            absolute_path = absolute_path.trim();
            path_instance = Paths.get(absolute_path);
            if(Files.exists(path_instance))
            {
                LineCounterClass lin = new LineCounterClass(absolute_path, currentPath);
                Vector<String> data = lin.getLines();
                out = data.toArray(new String[data.size()]);
                return out;
            }
            else
            {
                out[0] = "cat status: ";
                out[1] = "failed";
            }
        }

        return out;
    }

    public String[] ch()
    {
        commandsInserted = new Vector<>();
        return no_output_str;
    }

    public String[] ld()
    {
        Vector<String> buffer = StorageHandler.loadCommandHistory(currentPath);
        for(int i = 0; i < commandsInserted.size(); i ++)
        {
            buffer.add(commandsInserted.get(i));
        }
        commandsInserted = buffer;
        return no_output_str;
    }

    public String[] klk(OutputDevice out, int n)
    {
        String[] kol = {""};
        int nhelp = n;
        while(nhelp > 0)
        {
            out.executeCommand(klkHelp(nhelp));
            nhelp--;
        }
        return kol;
    }

    public String[] klkHelp(int i)
    {
        String[] out = {""};
        if(i%3 == 3)
        {
            out[0] = "exw mia kolokythia";
            return out;
        }
        if(i%3 == 2)
        {
            out[0] = "exw mia kolokythia";
            return out;
        }
        if(i%3 == 1)
        {
            out[0] = "esy thn exeis?";
            return out;
        }
        out[0] = "oxi. esy?";
        return out;
    }
}
