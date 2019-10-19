import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class mkdirCommand extends commandAbstract {

    public mkdirCommand(DataOutputStream out, DataInputStream in)
    {
        super(out, in);
    }
    public void execute(Changeable<String> currentPath, String arg)
    {
        File file = new File(currentPath.value +  "\\" + arg);
        try
        {
            if(file.isDirectory())
            {
                out.writeUTF("Un sous-répertoire ou un fichier "+ arg+ " existe déjà." );
            }
            else if(!file.mkdirs())
            {
                out.writeUTF("Une erreur s'est produite. le ficher: " + arg + " n'a pu etre crée." );
            }
            else
            {
                out.writeUTF("Le dossier" + arg + "a été créé.");

            }
        }
        catch (IOException e)
        {
            System.out.format("error");
        }

    }
}
