package lineCounter.model.devices;

import lineCounter.model.commands.Command_;

public interface Device {
    public void passInput(String[] in); /* pass input TO the device */
    public String[] passOutput();

    public String getName();

    public OutputDevice getOutputDevice();
    public InputDevice getInputDevice();
    public void setInputDevice(InputDevice inputDevice);
    public void setOutputDevice(OutputDevice outputDevice);
}
