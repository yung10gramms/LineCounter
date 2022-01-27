package lineCounter.model.commands;

import lineCounter.model.consoleInterface.interfaces.terminal.InputTerminal;
import lineCounter.model.consoleInterface.interfaces.terminal.OutputTerminal;

import java.io.Serializable;
import java.util.HashMap;

public class Command_ implements Comparable<Command_>, Serializable {
    private final String name;
    private String[] params;

    private final String description;

    private String manual;

    private InputTerminal inputTerminal;
    private OutputTerminal outputTerminal;

    /* system commands are held here as well */
    private static HashMap<String, Command_> existingCommands = new HashMap<>();

    public static Command_ getCommand_(String name, String description)
    {
        Command_ command_requested = existingCommands.get(name);
        if(command_requested == null)
        {
            Command_ output = new Command_(name, description);
            existingCommands.put(name, output);
            return output;
        }
        return command_requested;
    }

    private Command_(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public String getName()
    {
        return this.name;
    }

    public String[] getParams()
    {
        return params;
    }

    /** splits  */
    public void setParams(String paramsInString)
    {
        params = paramsInString.split(" ");

        for(int i = 0; i < params.length; i ++)
        {
            params[i] = params[i].trim();
        }
    }

    public void setParameters(String[] params)
    {
        this.params = params;
    }

    public String getDescription() {
        return description;
    }


    public void setManual(String man)
    {
        this.manual = man;
    }

    public void setManual(String str_for_params, String str_for_desc)
    {
        setManual("\nCOMMAND:\n" +CommandDatabase.tabSpaces+ getName()+ "\nPARAMS:\n" + CommandDatabase.tabSpaces
                + str_for_params
                + "\nDESCRIPTION:\n" + CommandDatabase.tabSpaces +
                str_for_desc+"\n");
    }

    public String getManual()
    {
        if(manual == null || manual.trim().equals(""))
            return "\n     no manual supported for command " +getName() + ".\n";
        return manual;
    }

    @Override
    public int compareTo(Command_ command_) {
        return getName().compareTo(command_.getName());
    }

    public void setInputDevice(InputTerminal inputTerminal) {
        this.inputTerminal = inputTerminal;
    }

    public OutputTerminal getOutputDevice() {
        return outputTerminal;
    }

    public void setOutputDevice(OutputTerminal outputTerminal) {
        this.outputTerminal = outputTerminal;
    }

}
