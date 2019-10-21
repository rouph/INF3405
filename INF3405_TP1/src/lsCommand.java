import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class lsCommand extends commandAbstract {

    public lsCommand(DataOutputStream out, DataInputStream in)
    {
        super(out, in);
    }

    public void execute(Changeable<String> currentPath, String arg) throws IOException 
    {
        StringBuilder builder = new StringBuilder();
        File[] files = new File(currentPath.value).listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                builder.append( "[Folder] " + file.getName());
            } else {
                builder.append( "[File] " + file.getName());
            }
            builder.append("\r\n");
        }

        out.writeUTF(builder.toString());
        out.flush();
    }
}
