package lineCounter.model.commands;

import lineCounter.model.LineCounterClass;
import lineCounter.model.SystemHandler;
import lineCounter.model.consoleInterface.*;
import lineCounter.model.consoleInterface.interfaces.CommandsController;
import lineCounter.model.consoleInterface.interfaces.terminal.InputTerminal;
import lineCounter.model.consoleInterface.interfaces.terminal.OutputTerminal;
import lineCounter.model.consoleInterface.interfaces.terminal.Terminal;
import lineCounter.model.devices.Device;
import lineCounter.model.devices.InputDevice;
import lineCounter.model.devices.OutputDevice;
import lineCounter.model.devices.Program;
import lineCounter.model.devices.rfiles.RFile;
import lineCounter.model.serialize.StorageHandler;


import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Objects;
import java.util.Vector;

public class CommandSystem implements CommandsController {
    private String currentPath;

    private final String homeDir;

    private final Vector<Command_> system_commands;
    private Vector<String> commandsInserted;

    private int currentCommandIndex;

    private Terminal consolePrinter;

    private static final String[] no_output_str = {""};
    private static final String[] command_not_found = {"\ncommand not found\n"};

    private static final CommandSystem instance = new CommandSystem();
    public static CommandSystem getInstance()
    {
        return instance;
    }

    private final ConsoleView consoleView;

    private static final int COUNT_ALL = 1;
    private static final int COUNT_NON_EMPTY = 2;
    private static final int COUNT_JAVA = 3;
    private static final int COUNT_C = 4;

    private String directoryToken = "/";

    private CommandSystem()
    {
        /* start by getting the current path */
        currentPath = System.getProperty("user.dir");
        homeDir = currentPath;

        /* Windows moment */
        if(currentPath.contains("\\"))
            directoryToken = "\\";

        system_commands = CommandDatabase.getCommands();
        commandsInserted = StorageHandler.loadCommandHistory(currentPath);
        consoleView = new ConsoleView(this);

        /* index for browsing though the already inserted commands */
        if(commandsInserted.size() != 0)
            currentCommandIndex = commandsInserted.size();

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
            if(currentCommandIndex < commandsInserted.size())
                currentCommandIndex++;
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

    public void setConsolePrinter(Terminal consolePrinter)
    {
        this.consolePrinter = consolePrinter;
    }

    public void updateStdInStdOut(InputTerminal in, OutputTerminal out)
    {
        for (Command_ system_command : system_commands) {
            system_command.setInputDevice(in);
            system_command.setOutputDevice(out);
        }
    }

    /**
     *      function that gets a string, and calls three adhesive functions, so that
     *      the command(s) will start running.
     * @param
     *      command_string the command that the user inserted before pressing enter.
     * */
    public void processCommand(String command_string)
    {
        if(! command_string.contentEquals(""))
        {
            /* get devices inserted */
            Vector<Device> devicesPipeline =   getDevices(command_string);
            if(devicesPipeline == null) {
                System.out.println("start next line!!!!");
                consolePrinter.startNextLine();
                return;
            }
            /* since we have a pipeline of devices passing one's input to the output
            * of the following one, we need to connect them all */

            pipeAll(devicesPipeline);
            runPipeLine(devicesPipeline);

            commandsInserted.add(command_string);
        }
        currentCommandIndex = commandsInserted.size();
        consolePrinter.startNextLine();

    }

    /**
     * function that gets a string, splits it by " | ", and gets a device based on their name
     * */
    public Vector<Device> getDevices(String commandString)
    {
        String token = " \\| ";
        String[] commandStrArray = commandString.split(token);

        Vector<Device> devicesPipeline = new Vector<>();
        for(String cur : commandStrArray)
        {
            /* if it's the token, just skip it */
            if(! cur.trim().contentEquals(token.trim()))
            {
                try
                {
                    /* firstly, we try to cache it for efficiency */
                    Device dev = SystemHandler.toDevice(cur);

                    if(dev == null)
                    {
                        consolePrinter.printCommandNotFound();
                        return null;
                    }

                    devicesPipeline.add(dev);
                } catch (NullPointerException nex)
                {
                    consolePrinter.printCommandNotFound();
                    return null;
                }

            }
        }
        return devicesPipeline;
    }


    public void pipe(Device writer, Device reader)
    {
        writer.setOutputDevice((OutputDevice) reader);
        reader.setInputDevice((InputDevice) writer);
    }

    public void pipeAll(Vector<Device> devicePipeline)
    {
        if(devicePipeline == null)
            return;
        if(devicePipeline.size() == 0)
            return;
        if(devicePipeline.size() == 1)
        {
            pipe((Device) consolePrinter, devicePipeline.get(0));
            pipe(devicePipeline.get(0), (Device) consolePrinter);
            return;
        }
        if(devicePipeline.size() == 2)
        {
            pipe((Device) consolePrinter, devicePipeline.get(0));
            pipe(devicePipeline.get(0), devicePipeline.get(1));
            pipe(devicePipeline.get(1), (Device) consolePrinter);
        }
        pipe((Device) consolePrinter, devicePipeline.get(0));
        for(int i = 0; i < devicePipeline.size() - 1; i ++)
        {
            pipe(devicePipeline.get(i), devicePipeline.get(i + 1));
        }
        pipe(devicePipeline.get(devicePipeline.size()-1), (Device) consolePrinter);
    }

    public void runPipeLine(Vector<Device> devicePipeline)
    {
        for(int i = 0; i < devicePipeline.size(); i ++)
        {
            if(devicePipeline.get(i).getName() != null)
            {
                int type = SystemHandler.getType(devicePipeline.get(i).getName());

                switch (type)
                {
                    case SystemHandler.TYPE_COMMAND:
                        Program program = (Program) devicePipeline.get(i);

                        handleCommand(program);
                        break;
                    case SystemHandler.TYPE_TERMINAL:
                        Device printer = devicePipeline.get(i);
                        printer.passInput(printer.getInputDevice().passOutput());
                        if(i == devicePipeline.size() - 1)
                        {
                            Terminal cons = (Terminal) printer;
                            cons.print("\n");
                            cons.executeCommand(printer.getInputDevice().passOutput());
                            cons.startNextLine();
                            if(printer.getInputDevice() != null && printer.getInputDevice() != printer)
                            {
                                if(SystemHandler.getType(printer.getInputDevice().getName())
                                        == SystemHandler.TYPE_TERMINAL)
                                {
                                    Terminal ancestorPrinter =
                                            (Terminal) printer.getInputDevice();
                                    ancestorPrinter.startNextLine();
                                }
                            }
                        }
                        break;
                    case SystemHandler.TYPE_R_FILE:
                        RFile rfile = (RFile) devicePipeline.get(i);
                        /* write the file */
                        rfile.passInput(rfile.getInputDevice().passOutput());
                        break;
                }
            } else
            {
                JOptionPane.showMessageDialog(null, "Error occurred. Try to restart");
            }

        }
    }


    public void handleCommand(Program program)
    {

        if(program.getName().contentEquals("help"))
        {
            program.getOutputDevice().passInput(help());
        }
        if(program.getName().contains("linecounter"))
        {
            program.getOutputDevice().passInput(lineCounter(program.getCommand()));
        }
        if(program.getName().contentEquals("ls"))
        {
            program.getOutputDevice().passInput(ls());
        }
        if(program.getName().contains("cd"))
        {
            program.getOutputDevice().passInput(cd(program.getCommand()));
            fixPath();
        }
        if(program.getName().contentEquals("exit") || program.getName().contentEquals("close") )
        {
            close();
        }
        if(program.getName().contentEquals("terminate") )
        {
            exitOperation();
        }
        if(program.getName().contentEquals("clear") ||
                program.getName().contentEquals("clc"))
        {
            program.getOutputDevice().passInput(consoleView.clear());
            System.gc();
        }
        if(program.getName().contentEquals("cmd"))
        {
            program.getOutputDevice().passInput(cmd());
        }
        if(program.getName().contentEquals("ram"))
        {
            program.getOutputDevice().passInput(ram());
        }
        if(program.getName().contentEquals("win"))
        {
            program.getOutputDevice().passInput(win());
        }
        if(program.getName().contentEquals("man"))
        {
            program.getOutputDevice().passInput(man
                    (searchCommand(getNextParam(program.getCommand()).trim())));
        }
        if(program.getName().contentEquals("gc"))
        {
            program.getOutputDevice().passInput(gc());
        }
        if(program.getName().contentEquals("echo"))
        {
            program.getOutputDevice().passInput(echo(program.getCommand()));
        }
        if(program.getName().contentEquals("cat"))
        {
            program.getOutputDevice().passInput(cat(program.getCommand()));
        }
        if(program.getName().contentEquals("ch"))
        {
            program.getOutputDevice().passInput(ch());
        }
        if(program.getName().contentEquals("ld"))
        {
            program.getOutputDevice().passInput(ld());
        }
        if(program.getName().contentEquals("lcc"))
        {
            program.getOutputDevice().passInput(lcc(program.getCommand()));
        }
        if(program.getName().contentEquals("jLin"))
        {
            program.getOutputDevice().passInput(jLin(program.getCommand()));
        }
        if(program.getName().contentEquals("cLin"))
        {
            program.getOutputDevice().passInput(cLin(program.getCommand()));
        }
        if(program.getName().contentEquals("klk"))
        {
            //todo revisit
            program.getOutputDevice().passInput(klk(program.getCommand().getOutputDevice(), 5000));
        }
        if(program.getName().contentEquals("devs"))
        {
            program.getOutputDevice().passInput(devs());
        }
        if(program.getName().contentEquals("grep"))
        {
            program.getOutputDevice().passInput(grep(program.getCommand()));
        }
        if(program.getName().contentEquals("env"))
        {
            program.getOutputDevice().passInput(getEnv());
        }
        if(program.getName().contentEquals("whoami"))
        {
            program.getOutputDevice().passInput(whoami());
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

        StorageHandler.storeCommandsString(homeDir, commandsInserted);
        exit();
    }

    private void fixPath()
    {
        currentPath = currentPath.replace(directoryToken + directoryToken, directoryToken);
    }


    private Command_ searchCommand(String name)
    {
        final String splitToken = " \\| ";
        String[] commandsPiped = name.split(splitToken);

        if(! name.contains(splitToken))
            return searchCommandByString(name);

        for(int i = 0; i < commandsPiped.length; i ++)
        {
            if(! commandsPiped[i].trim().equals(splitToken.trim()) && ! commandsPiped[i].trim().equals(""))
            {
                Command_ command = searchCommandByString(commandsPiped[i]);
                if(command != null)
                {
                    try
                    {
                        command.setOutputDevice(Objects.requireNonNull(SystemHandler.getDev(
                                commandsPiped[i + 2].trim())).mainPanel().consoleText());
                        return command;
                    } catch (NullPointerException | ArrayIndexOutOfBoundsException nex)
                    {
                        return null;
                    }
                }

            }
        }
        return null;
    }

    public Command_ getCommandBySplitedString(String name)
    {
        for(Command_ cur : system_commands)
        {
            if(cur.getName().contentEquals(name))
            {
                return cur;
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
                StringBuilder params_in_str = new StringBuilder();
                for (int k = 0; k < command_in_strings.length - 1; k++) {
                    params_in_str.append(" ").append(command_in_strings[k + 1]);
                }
                system_command.setParams(params_in_str.toString());
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
            coms[i] = coms[i] + ".".repeat(Math.max(0, rest)) +
                    system_commands.get(i).getDescription();
        }
        return coms;
    }

    public File getFile(String path)
    {
        if(path.startsWith(directoryToken))
        {
            Path pathInstance = Paths.get(path);
            if(Files.exists(pathInstance))
            {
                return new File(path);
            }
            return null;
        }
        String concat = currentPath + directoryToken + path;
        concat = concat.replace(directoryToken + directoryToken, directoryToken);
        Path pathInstance = Paths.get(concat);
        if(Files.exists(pathInstance))
        {
            return new File(concat);
        }
        return null;
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
        int caseInt;
        if(countAll)
            caseInt = COUNT_ALL;
        else
            caseInt = COUNT_NON_EMPTY;
        return  linesHelp(command, caseInt);
    }

    private String[] linesHelp(Command_ command, int use)
    {
        String[] out = new String[2];
        int nexNonEmpty = 0;
        if(command.getParams() != null) {
            for (String butt : command.getParams()) {
                if(butt.length() == 0)
                    nexNonEmpty++;
            }
            if(nexNonEmpty == command.getParams().length)
                nexNonEmpty = 0;
        }
        if(command.getParams() == null || command.getParams().length == 0 ||
                command.getParams()[nexNonEmpty] == null ||
                command.getParams()[nexNonEmpty].contentEquals(""))
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
            String path = command.getParams()[nexNonEmpty];
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
        StringBuilder names_as_string = new StringBuilder();
        for (String name : names) {
            names_as_string.append("    ").append(name);
        }
        names_as_string = new StringBuilder(names_as_string.toString().trim());
        return new String[]{names_as_string.toString()};
    }

    public String[] cd(Command_ command)
    {
        String commandString = getNextParam(command);
        StringBuilder path_in_cd = new StringBuilder(commandString);
        if(path_in_cd.length() == 0)
        {
            return no_output_str;
        }
        if(path_in_cd.charAt(0) == '/')
        {
            return cdHelp( path_in_cd.toString() );
        }
        else if(path_in_cd.charAt(0) == '.')
        {
            String[] path = commandString.trim().split(directoryToken);
            int count = 0;
            for(String curPathString : path)
            {
                if(! curPathString.contentEquals("..") && ! curPathString.contentEquals(directoryToken))
                {
                    /* basically returns error message */
                   return cdHelpValidate("random path", false);
                }
                if(curPathString.contentEquals(".."))
                {
                    count++;
                }
            }
            String[] parentDir = currentPath.split(directoryToken);
            int lastIndex;
            if(parentDir.length  > 2 && parentDir[parentDir.length - 1].equals(directoryToken))
            {
                lastIndex = parentDir.length - 2;
            }
            else if(parentDir.length > 1)
            {
                lastIndex = parentDir.length - 1;
            }
            else
            {
                return cdHelpValidate("invalid", false);
            }
            int countDirsAbove = 1;
            while(countDirsAbove < count)
            {
                lastIndex = lastIndex - 1;
                if(lastIndex == 0)
                    return cdHelpValidate("invalid", false);
                while(parentDir[lastIndex].equals(directoryToken))
                    lastIndex = lastIndex - 1;
                countDirsAbove++;
            }
            String[] builtStrArray = new String[lastIndex];
            path_in_cd = new StringBuilder();
            System.arraycopy(parentDir, 0, builtStrArray, 0, lastIndex);
            for(int i = 0; i < lastIndex; i ++)
            {
                path_in_cd.append(directoryToken).append(builtStrArray[i]);
            }
            path_in_cd.append(directoryToken);
            return cdHelp(path_in_cd.toString());

        }
        else //child dir
        {
            return cdHelp(currentPath + directoryToken +path_in_cd);
        }

    }

    private String[] cdHelp(String path_in_cd)
    {
        return cdHelpValidate(path_in_cd, true);
    }

    private String[] cdHelpValidate(String path_in_cd, boolean valid)
    {
        Path path = Paths.get(path_in_cd);

        if(Files.exists(path) && valid)
        {
            File file = new File(path_in_cd);
            if(file.isDirectory())
            {
                //cool
                currentPath = path_in_cd;
                return no_output_str;
            } else
            {
                return new String[]{"cannot use cd in regular files (non-dirs)"};
            }
        }
        else
        {
            return new String[]{"cannot cd to non-existing directory"};
        }
    }

    public void exit()
    {
        System.exit(0);
    }

    public String[] cmd()
    {
        String[] out =  commandsInserted.toArray(new String[0]);
        if(out.length == 0)
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
        return new String[]{command.getManual()};
    }

    public String[] echo(Command_ command)
    {
        if(command.getParams() == null || command.getParams().length == 0)
            return no_output_str;
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < command.getParams().length; i ++)
        {
            out.append(" ").append(command.getParams()[i]);
        }


        out = new StringBuilder(out.toString().trim());
        return new String[]{out.toString()};
    }

    public String[] cat(Command_ command)
    {
        String[] out = new String[2];
        if(command.getParams() == null || command.getParams().length == 0 ||
                command.getParams()[0] == null || command.getParams()[0].contentEquals(""))
        {

            System.out.println("param0: " + command.getParams()[0]);

            Path path = Paths.get(currentPath);
            if(Files.exists(path))
            {
                try
                {
                    LineCounterClass lin = new LineCounterClass(currentPath, currentPath);
                    Vector<String> data = lin.getLines();
                    out = data.toArray(new String[0]);
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
                out = data.toArray(new String[0]);
            }
            String absolute_path = currentPath + directoryToken + path_string;
            absolute_path = absolute_path.replace("//", directoryToken);
            absolute_path = absolute_path.trim();
            path_instance = Paths.get(absolute_path);
            if(Files.exists(path_instance))
            {
                LineCounterClass lin = new LineCounterClass(absolute_path, currentPath);
                Vector<String> data = lin.getLines();
                out = data.toArray(new String[0]);
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
        buffer.addAll(commandsInserted);
        commandsInserted = buffer;
        return no_output_str;
    }

    public String[] klk(OutputTerminal out, int n)
    {
        String[] kol = {"kolokythia game is over"};
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

    public String[] devs()
    {
        String[] actives = new String[SystemHandler.activeDevices.size() + 2];
        actives[0] = "===============================================\nActive devices:";
        int i = 1;
        for(Device dev : SystemHandler.activeDevices)
        {
            actives[i] = i+ ". " +dev.getName();
            i++;
        }
        actives[actives.length - 1] = "===============================================";
        return actives;
    }

    public String[] grep(Command_ command)
    {
        Vector<String> output;

        String[] params = command.getParams();


        final int obtain_file = 4;
        final int obtain_use = 5;
        final int seek = 0;

        LineCounterClass lineCounterClass = null;

        int use;
        int next_use = seek;
        String pattern = "";
        for(String cur : params)
        {
            use = next_use;
            if(cur.startsWith("-"))
            {
                if(use != seek) {
                    /* error - false input */
                    consolePrinter.printCommandNotFound();
                    return no_output_str;
                }
                if(cur.equals("-f"))
                {
                    next_use = obtain_file;
                }
                else if (cur.equals("-e"))
                {
                    next_use = obtain_use;
                }
                else
                {
                    /* error - false input */
                    System.out.println("searching for -e,-f, sth else given");
                    consolePrinter.printCommandNotFound();
                    return no_output_str;
                }
            }
            if(use == obtain_file)
            {
                File fileGot = getFile(cur);
                if(fileGot == null)
                {
                    System.out.println("File got is null");
                    consolePrinter.printCommandNotFound();
                    return no_output_str;
                }
                lineCounterClass =
                        new LineCounterClass(fileGot.getAbsolutePath(), currentPath);

                next_use = seek;
            }
            if(use == obtain_use)
            {
                pattern = cur;
            }
        }


        if(lineCounterClass == null)
        {
            lineCounterClass =
                    new LineCounterClass(currentPath, currentPath);
            System.out.println("line-counter class is null");
        }
        if(pattern.equals(""))
        {
            consolePrinter.printCommandNotFound();
            return no_output_str;
        }

        output = lineCounterClass.findPattern(pattern);

        return output.toArray(new String[0]);
    }

    public String searchFirstThatContains(String pattern)
    {
        if(pattern.trim().equals(""))
            return "";

        String outputString = pattern;

        String[] patternSplitArray = pattern.split(" ");

        if(patternSplitArray.length > 1)
            pattern = patternSplitArray[patternSplitArray.length - 1].trim();

        outputString = outputString.replace(pattern, "");

        for(Command_ command_ : system_commands)
        {
            if(command_.getName().startsWith(pattern))
            {
                return outputString + command_.getName();
            }
        }
        if(pattern.startsWith(directoryToken))
        {
            String[] patternStringArray = pattern.split(directoryToken);
            StringBuilder builder = new StringBuilder();

            int counter = 0;
            for(String current : patternStringArray)
            {
                if(counter < patternStringArray.length - 1)
                {
                    builder.append(current);
                }
            }
            String pathBuilt = builder.toString();
            System.out.println("pathBuilt: " + pathBuilt);
            File fileGot = getFile(pathBuilt);
            if(fileGot == null)
            {
                return null;
            }
            /* start searching in current directory for file given */
            String searchingResult = searchForFileThatStartsWith(fileGot.getAbsolutePath());
            if(searchingResult != null) {
                return outputString + searchingResult;
            }
        }
        String searchingResult = searchForFileThatStartsWith(currentPath);
        if(searchingResult != null)
            return outputString + searchingResult;

        for(Device dev : SystemHandler.activeDevices)
        {
            if(dev.getName().startsWith(pattern))
                return outputString + dev.getName();
        }

        return null;
    }

    private String searchForFileThatStartsWith(String dir)
    {
        /* this will not create a null pointer exception */
        File[] files = new File(dir).listFiles();
        if(files == null)
            return null;

        for(File curFile : files)
        {
            if(curFile.getName().startsWith(dir))
                return curFile.getName();
        }
        return null;
    }

    public String[] getEnv()
    {
        return System.getenv().toString().split(" ");
    }

    public String[] whoami()
    {
        return new String[]{System.getenv().get("PATH").split("/")[2]};
    }

}
