package lineCounter.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class LineCounterClass {

    private String path;
    private CreateFile filecreator;

    private String outputPath;

    public static final String startHighlight = "<\"";
    public static final String endHighlight   = "\">";

    public LineCounterClass(String path, String outputPath) {
        filecreator = new CreateFile(outputPath);
        filecreator.fileCreate();
        this.path = path;
        this.outputPath = outputPath;
    }

    public int countLinesInFile(String fileName, boolean countEmptyLinesToo)
    {
        boolean counting = true;

        int out = 0;
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String s = myReader.nextLine();
                filecreator.fileWrite(s);

                if(countEmptyLinesToo)
                    out++;
                else
                {
                    if(s.trim().length() > 0 && s.trim().charAt(0) != '/' && !s.trim().equals("") && counting)
                        out++;
                    if(s.trim().length() > 2 && s.trim().startsWith("/*"))
                        counting = false;
                    if(s.trim().length() > 2 && s.trim().endsWith("*/"))
                        counting = true;
                }
            }
            myReader.close();
            return out;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //probably dead code
        return -1;
    }



    public int countLinesInFile(String fileName, boolean countEmptyLinesToo, Vector<String> output)
    {
        if(output == null)
            output = new Vector<>();

        int out = 0;
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String s = myReader.nextLine();
                filecreator.fileWrite(s);
                output.add(s);
                if(countEmptyLinesToo)
                    out++;
                else if( !s.equals(""))
                    out++;
            }
            myReader.close();
            return out;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //probably dead code
        return -1;
    }

    public void listFiles(Vector<File> files)
    {
        for(int i = 0; i < files.size(); i ++)
        {
            System.out.println(files.get(i).getAbsolutePath());
        }
    }

    public Vector<File> getFiles()
    {
        Vector<File> files = new Vector<File>();
        getFilesHelp(path, files);
        return files;
    }

    public void getFilesHelp(String dir, Vector<File> files)
    {
        File[] fileArray = new File(dir).listFiles();

        if(fileArray == null)
            return;

        for(int i = 0; i < fileArray.length; i ++)
        {
            if(! fileArray[i].isDirectory())
            {
                files.add(fileArray[i]);
            } else
            {
                getFilesHelp(fileArray[i].getAbsolutePath(), files);
            }
        }
    }

    public Vector<String> getLines()
    {
        Vector<File> files = getFiles();

        Vector<String> output = new Vector<>();

        for(int i = 0; i < files.size(); i ++)
        {
            countLinesInFile(files.get(i).getAbsolutePath(), true, output);
        }

        filecreator.fileClose();

        return output;
    }

    private int getNum(boolean getEmpty, String endsAt)
    {
        Vector<File> files = getFiles();

        int numberOfFiles = enumerateFiles();
        int count = 0;

        for(int i = 0; i < files.size(); i ++)
        {
            if(files.get(i).getName().trim().endsWith(endsAt))
            {
                //consoleText.setText(consoleText.getText()+"Searching in file " +files.get(i).getAbsolutePath());
                count = count + countLinesInFile(files.get(i).getAbsolutePath(), getEmpty);
                //consoleText.updateProgress(count, numberOfFiles);

            }
        }

        filecreator.fileClose();

        return count;
    }

    public int getNumC()
    {
        return getNum(false, ".c") + getNum(false, ".h");
    }

    public int getNumJava()
    {
        return getNum(false, ".java");
    }

    public int getNumAll()
    {
        return getNum(true, "");
    }

    public int getNumNonEmpty()
    {
        return getNum(false, "");
    }

    public int enumerateFiles()
    {
        return getFiles().size();
    }

    /** basically grep */
    public Vector<String> findPattern(String pattern)
    {
        Vector<String> output = new Vector<>();
        File file = new File(path);

        patternHelpRecursion(output, file, pattern);

        return output;
    }

    private void patternHelpRecursion(Vector<String> output, File file, String pattern)
    {
        if(! file.exists())
        {
            return;
        }
        if(file.isDirectory())
        {
            File[] filesInDirectory = file.listFiles();
            for(File current : filesInDirectory)
            {
                patternHelpRecursion(output, current, pattern);
            }
        } else
        {
            patternHelp(output, file, pattern);
        }
    }

    private void patternHelp(Vector<String> output, File current, String pattern)
    {
        try {
            Scanner scanner = new Scanner(current);
            boolean patternFound = false;

            int lineIndex = 1;

            while(scanner.hasNextLine())
            {
                String buffer = scanner.nextLine();
                if(buffer.contains(pattern))
                {
                    if(! patternFound)
                        output.add("File: " + current.getName());
                    output.add("line " + lineIndex + ": " +
                            buffer.replace(pattern, startHighlight+pattern+endHighlight));
                    patternFound = true;
                }
                lineIndex++;
            }


        } catch (FileNotFoundException e) {
            /* dead code */
            e.printStackTrace();
        }
    }

}
