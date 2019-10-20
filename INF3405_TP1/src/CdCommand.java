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

    public void execute(Changeable<String> currentPath, String arg) throws IOException  {
        Path currentRelativePath = Paths.get(currentPath + "\\" + arg);
        String replyMessage = "";
	    boolean isValid = isValidFile(currentPath + "\\" + arg);
	    
        if(isValid) {
            File file = new File(currentRelativePath.toRealPath().toString());
            if (file.isDirectory()) {
                currentPath.value = file.getPath();
                replyMessage = "Vous etes dans le dossier " + file.getName() + ".";
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
}
