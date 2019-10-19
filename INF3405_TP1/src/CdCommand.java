import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;

public class CdCommand extends commandAbstract  {
    public CdCommand(DataOutputStream out, DataInputStream in) {
        super(out, in);
    }

    public void execute(Changeable<String> currentPath, String arg) {
        try
        {
            Path currentRelativePath = Paths.get(currentPath + "\\" + arg);
            File test = new File(currentRelativePath.toRealPath().toString());

            if (test.isDirectory())
            {
                currentPath.value = test.getPath();
                System.out.println("Current relative path is: " + currentPath);
            }
            else
            {
                out.writeUTF(arg + " n'est pas un repertoire valid");
                out.flush();
            }
        }
        catch (IOException e)
        {
            System.out.format("error");
        }
    }
}
