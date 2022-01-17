package lineCounter.model.commands;

import lineCounter.model.consoleInterface.interfaces.InputDevice;
import lineCounter.model.consoleInterface.interfaces.OutputDevice;

import java.io.Serializable;

public class Command_ implements Comparable<Command_>, Serializable {
    private String name;
    private String[] params;

    private String execution;
    private String description;

    private String manual;

    public Command_(String name)
    {
        this.name = name;
    }

    private InputDevice inputDevice;
    private OutputDevice outputDevice;

    public Command_(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public Command_(String name, String param1, String ex)
    {
        this.name = name;
        params = new String[1];
        params[0] = param1;
        this.execution = ex;
    }

    public String getName()
    {
        return this.name;
    }

    public String[] getParams()
    {
        return params;
    }

    public String getExecution() {
        return execution;
    }

    /** splits  */
    public void setParams(String paramsInString)
    {
        params = paramsInString.split(" ");

        for(int i = 0; i < params.length; i ++)
        {
            params[i].trim();
        }
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public InputDevice getInputDevice() {
        return inputDevice;
    }

    public void setInputDevice(InputDevice inputDevice) {
        this.inputDevice = inputDevice;
    }

    public OutputDevice getOutputDevice() {
        return outputDevice;
    }

    public void setOutputDevice(OutputDevice outputDevice) {
        this.outputDevice = outputDevice;
    }

}
