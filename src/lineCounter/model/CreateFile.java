package lineCounter.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class CreateFile {

    private String filestr = "/";
    private FileWriter myWriter;

    File myObj;

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
            if(myObj == null)
                myObj = new File(filestr);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void fileWriteAll(String[] str)
    {
        for(String stringBuf : str)
        {
            fileWrite(stringBuf);
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

    public String[] readAll()
    {
        Vector<String> reader = new Vector<>();
        if(myObj == null)
            myObj = new File(filestr);
        try {
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine())
            {
                String s = myReader.nextLine();
                reader.add(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return reader.toArray(new String[reader.size()]);
    }

    public String fileRead()
    {
        if(myObj == null)
            myObj = new File(filestr);
        try {
            Scanner myReader = new Scanner(myObj);
            String s = myReader.nextLine();
            return s;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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