import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public  abstract class commandAbstract {

    public commandAbstract(DataOutputStream out, DataInputStream in)
    {
        this.out = out;
        this.in = in;
    }
    protected DataOutputStream out;
    protected DataInputStream in;
    public abstract void execute(Changeable<String> currentPath, String arg) throws IOException ;
    
    public boolean isValidFile(String path)
    {
		boolean isValid = true;
		Path currentRelativePath = Paths.get(path);
		try
		 {
		     currentRelativePath.toRealPath().toString();
		 }
		 catch(IOException exception)
		 {
			 isValid = false;
		 }

		 return isValid;
    }
}
