import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class mkdirCommand extends commandAbstract {

    public mkdirCommand(DataOutputStream out, DataInputStream in)
    {
        super(out, in);
    }
 
    public void execute(Changeable<String> currentPath, String arg) throws IOException 
    {
        File file = new File(currentPath.value +  "\\" + arg);
        if(file.isDirectory())
        {
            out.writeUTF("Un sous-repertoire ou un fichier "+ arg+ " existe deja ." );
        }
        else if(!file.mkdirs())
        {
            out.writeUTF("Une erreur s'est produite. le ficher: " + arg + " n'a pu etre cree." );
        }
        else
        {
            out.writeUTF("Le dossier " + arg + " a ete cree.");

        }
    }
}
