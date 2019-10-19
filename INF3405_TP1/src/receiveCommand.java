import java.io.*;

public class receiveCommand extends commandAbstract {

    public receiveCommand(DataOutputStream out, DataInputStream in) {
        super(out, in);
    }

    public void execute(Changeable<String> currentPath, String arg) {
        try
        {
            byte [] mybytearray  = new byte [1024];
            FileOutputStream fos = new FileOutputStream(currentPath.value +  "\\" + "tochange.txt");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = in.read(mybytearray,0,mybytearray.length);
            int current = bytesRead;
            bos.write(mybytearray, 0 , current);
            bos.flush();
            System.out.println("File " + "tochange" + " downloaded (" + current + " bytes read)");
            bos.close();
        }
        catch (IOException e)
        {
            System.out.format("error");
        }
    }
}
