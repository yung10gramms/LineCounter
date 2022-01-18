package lineCounter.model.devices.rfiles;

import lineCounter.model.CreateFile;
import lineCounter.model.devices.Device;
import lineCounter.model.devices.InputDevice;
import lineCounter.model.devices.OutputDevice;

public class RFile implements Device {

    private String path;
    private CreateFile fileCreate;

    public RFile(String path)
    {
        fileCreate = new CreateFile(path);
    }

    private InputDevice inputDevice;
    private OutputDevice outputDevice;

    @Override
    public void passInput(String[] in) {
        for(String current : in)
        {
            fileCreate.fileWrite(current);
        }
        fileCreate.fileClose();
    }

    @Override
    public String[] passOutput() {
        return fileCreate.readAll();
    }

    @Override
    public String getName() {
        return path;
    }

    @Override
    public OutputDevice getOutputDevice() {
        return outputDevice;
    }

    @Override
    public InputDevice getInputDevice() {
        return inputDevice;
    }

    @Override
    public void setInputDevice(InputDevice inputDevice) {
        this.inputDevice = inputDevice;
    }

    @Override
    public void setOutputDevice(OutputDevice outputDevice) {
        this.outputDevice = outputDevice;
    }
}
