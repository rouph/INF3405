import java.io.DataInputStream;
import java.io.DataOutputStream;

public  abstract class commandAbstract {

    public commandAbstract(DataOutputStream out, DataInputStream in)
    {
        this.out = out;
        this.in = in;
    }
    protected DataOutputStream out;
    protected DataInputStream in;
    public abstract void execute(Changeable<String> currentPath, String arg);
}
