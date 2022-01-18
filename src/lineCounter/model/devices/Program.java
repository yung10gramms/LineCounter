package lineCounter.model.devices;

import lineCounter.model.commands.Command_;
import lineCounter.model.consoleInterface.interfaces.InputTerminal;
import lineCounter.model.consoleInterface.interfaces.OutputTerminal;

public class Program implements InputDevice, OutputDevice {
    private Command_ command;

    private InputDevice inputDevice;
    private OutputDevice outputDevice;

    public Program()
    {

    }

    public Program(Command_ command)
    {
        this.command = command;

    }

    public String getName()
    {
        return command.getName();
    }

    @Override
    public void passInput(String[] in) {
        command.setParameters(in);
    }

    @Override
    public String[] passOutput() {
        return new String[0];
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

    public Command_ getCommand()
    {
        return this.command;
    }

}
