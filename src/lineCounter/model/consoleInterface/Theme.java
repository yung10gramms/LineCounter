package lineCounter.model.consoleInterface;

import java.awt.*;

public class Theme {
    private Color foreGround;
    private Color backGround;
    private Color misc;

    public Theme(Color backGround, Color foreGround, Color misc)
    {
        this.backGround = backGround;
        this.foreGround = foreGround;
        this.misc = misc;
    }

    public Color getForeGround()
    {
        return foreGround;
    }

    public Color getBackGround()
    {
        return backGround;
    }

    public Color getMisc()
    {
        return misc;
    }
}
