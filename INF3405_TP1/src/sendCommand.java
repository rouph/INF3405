import java.io.*;

public class sendCommand extends commandAbstract {
    private String type;
    public sendCommand(DataOutputStream out, DataInputStream in, String iType) {
        super(out, in);
        type = iType;
    }

    public void execute(Changeable<String> currentPath, String arg) {
        try {
            out.writeUTF(type);
            BufferedInputStream bis = null;
            File myFile = new File(arg);
            byte[] mybytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(arg);
            bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);
            System.out.println("Sending " + arg + "(" + mybytearray.length + " bytes)");
            out.write(mybytearray, 0, mybytearray.length);
            out.flush();
            System.out.println("Done.");
        }
        catch (IOException e)
        {
            System.out.format("error here");
        }
    }
}
