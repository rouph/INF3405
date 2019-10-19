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

        Path currentRelativePath = null;
        currentRelativePath = Paths.get(currentPath + "\\" + arg);
        boolean isValid = true;
        String replyMessage = "";
        try
        {
            currentRelativePath.toRealPath().toString();
        }
        catch(IOException e)
        {
            isValid = false;
        }
        try
        {
            if(isValid) {
                File test = new File(currentRelativePath.toRealPath().toString());
                if (test.isDirectory()) {
                    currentPath.value = test.getPath();
                    replyMessage = "Vous etes dans le dossier " + test.getName() + ".";
                } else {
                    replyMessage = arg + " n'est pas un repertoire valid";
                }
            }
            else
            {
                replyMessage = arg + " n'est pas un repertoire valid";
            }

            out.writeUTF(replyMessage);
            out.flush();
        }
        catch (IOException e)
        {
            System.out.println("error in CD command");
        }
    }
}
