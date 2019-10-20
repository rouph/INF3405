import java.io.*;

public class receiveCommand extends commandAbstract {

    public receiveCommand(DataOutputStream out, DataInputStream in) {
        super(out, in);
    }

    public void execute(Changeable<String> currentPath, String arg) {
        try
        {
            String fileName = in.readUTF();
            long fileSize = in.readLong();
            byte [] mybytearray  = new byte [1024];
            FileOutputStream fos = new FileOutputStream(currentPath.value +  "\\" + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = 0;
            int current = bytesRead;

            while (current < fileSize) {
                bytesRead = in.read(mybytearray,0,mybytearray.length);
                bos.write(mybytearray, 0, bytesRead);
                current += bytesRead;
            }
            bos.write(mybytearray, 0 , current);
            bos.flush();
            out.writeUTF("File " + fileName + " test (" + current + " bytes read)");
            bos.close();
        }
        catch (IOException e)
        {
            System.out.format("error");
        }
    }
}
