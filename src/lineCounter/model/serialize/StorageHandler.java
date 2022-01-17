package lineCounter.model.serialize;

import lineCounter.model.commands.Command_;

import java.io.*;
import java.util.Vector;

public class StorageHandler {

    private final static String name = "history";

    public static void storeCommands(String path, Vector<Command_> commands)
    {
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + name);
            if(path.charAt(path.length()-1) == '/')
            {
                fileOutputStream = new FileOutputStream(path + name);
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(commands);
            objectOutputStream.close();
            fileOutputStream.close();
            return;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        System.out.println("you are the only exception");
    }

    public static void storeCommandsString(String path, Vector<String> commands)
    {
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + name);
            if(path.charAt(path.length()-1) == '/')
            {
                fileOutputStream = new FileOutputStream(path + name);
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(commands);
            objectOutputStream.close();
            fileOutputStream.close();
            return;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        System.out.println("you are the only exception");
    }

    public static Vector<String> loadCommandHistory(String workingPath)
    {
        try {
            FileInputStream fileInputStream = new FileInputStream(workingPath + "/" + name);
            if(workingPath.charAt(workingPath.length()-1) == '/')
            {
                fileInputStream = new FileInputStream(fileInputStream + name);
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Vector<String> out = (Vector<String>) objectInputStream.readObject();
            return out;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("you are the only (fnf) exception");
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            System.out.println("you are the only (class) exception");
        }
        System.out.println("you are the only (input) exception");
        return new Vector<>();
    }

}