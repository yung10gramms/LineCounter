# LineCounter
<!---- Headings ---->
# LineCounter
## A project that simulates a terminal device

<!-- Italics -->

This is a project that simulates a terminal, which has a set of commands that one can use and see them being executed on the terminal. This GUI is developed in java, and uses javax.swing to display frames, panels, etc. So, in order to run this, one has to have install a JVM on their device. 


Thus, there is a class named *Command_*, each instance of which, contains details about the name, the description and the manual of the command. There cannot exist two commands with the same name, and as well, as copies of the same *Command_* twice in the program, and that is since the *Command_* class only contains some complementary information about the command and has no information about its execution. It has some information about the input and the output, but this will not concern us, mainly because the program is single-threaded. This is achieved by adding each newly generated instance of the class *Command_* to a static hash map, so that when an exernal method requests to instantiate a command, it is checked whether the instance already exists.
<!-- GitHub Markdown -->
<!-- Code Blocks -->
```java
static HashMap<String, Command_> existingCommands;
```
Thus, the constructor of the class *Command_* is private, and there is a static method to decide wether the constructor will be called or an already instantiated *Command_* will be fetched from the hash map.

All commands are in practice generated in the launch of the application, and in order to keep the simplicity of the project, they are not stored anywhere after the program terminates. The function responsible for generating the commands is a static method of a utility class called *CommandsDatabase*.
```java
public static Vector<Command_> getCommands()
```
As one might notice, all commands live in a vector, which means, even though they eventually get sorted, that they are being searched in a serial way. By the way, no client can generate the vector in question twice, because of some static flags that check whether it has already been generated. 

This vector of commands is stored in the most important class, that is the *CommandSystem*, a sinlgeton class, that contains the most importart data and functions, and implements an interface that is created for the purpose of this project. This interface plays the role of the contoller in the design pattern Model-View-Controller, which is being used in this particular project in order to interact with a GUI.
```java
public class CommandSystem implements CommandsController 
```
In addition, the class *CommandSystem* contains all the methods that implement the execution of each command. They have the exact name as the command they are executing, but this is only for making the developer's perspective easier.

Before we describe exactly how commands are being executed and how they are being printed on the terminal, let's take a look at the input and output in this program. This is a rather small project, so the only things it considers to get input and generate output are: a terminal device, that is the interface *Terminal* in the project LineCounter, a Regular file, or else *RFile* in LineCounter, and a class that wraps a *Command_*, while specificating the input and output device, called *Program*. All of these classes implement the interfaces *InputDevice* and *OutputDevice*, that extend the interface *Device*. Now, when one writes the line
```
echo foo | dev1 
```
what the O.S. Linux would do is that it would pipe the std output of echo to the std input of dev1, and considering dev1 is a terminal device, it would print it on its UI. Nonetheless, there's no pipes in LineCounter, so each method has to terminate in order to pass its data to its output. That being said, a pseudopipe is created, by connecting the output of each device to the input of the next one, before the first one is being executed (if it is a *Program*, of course). One can see the *pipe* method of the class *CommandSystem* below. This is called *N* times in the method *pipeAll*, where *N* is the number of pseudopipes the client requests to generate. 
```java   
public void pipe(Device writer, Device reader)
{
    writer.setOutputDevice((OutputDevice) reader);
    reader.setInputDevice((InputDevice) writer);
}
``` 

Also, the *CommandSystem* holds a list of Strings, each of which is a phrase that the client has typed in the past in the terminal. They are stored in the directory that the LineCounter is currently running, once the program terminates. One can browse up and down to see the commands (not referring to the class *Command_*) that they have inserted in the past, by using the up/down arrow keys. The auto-complete by tabbing works as well, but it might need a few times before it does.

The main function, as well as some static functions and data structures, exist in the class *SystemHandler*. This is because, there has to be a list of all (active) devices that exist in the program, as well as all the windows that are visible, in order for the application to figure out when to shut down.
```java
public static void main(String[] args)
{
    newWindow();
}
```
