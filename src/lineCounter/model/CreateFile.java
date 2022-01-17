package lineCounter.model;

import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors

public class CreateFile {

    private String filestr = "C:/Users/konst/Desktop/all text.txt";
    private FileWriter myWriter;

    public CreateFile()
    {
        //empty constructor
    }

    public CreateFile(String path)
    {
        setPath(path);
    }

    public void fileCreate() {
        try
        {
            myWriter = new FileWriter(filestr);
            File myObj = new File(filestr);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void fileWrite(String str) {
        try {
            myWriter.write(str + "\n");
        } catch (IOException e ) {

        } catch (NullPointerException nex)
        {

        }
    }

    public void fileClose()
    {
        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException nex)
        {

        }
    }

    public void setPath(String filePath)
    {
        filestr = filePath;
    }
}