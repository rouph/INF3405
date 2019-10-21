import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;

public class sendCommand extends commandAbstract {
	private String type;

	public sendCommand(DataOutputStream out, DataInputStream in, String iType) {
		super(out, in);
		type = iType;
	}

	public void execute(Changeable<String> currentPath, String arg) throws IOException {
		FileInputStream fis = null;
		BufferedInputStream bis = null;

        Path currentRelativePath = Paths.get(currentPath + "\\" + arg);
		try
        {
            currentRelativePath.toRealPath().toString();
        }
        catch(IOException exception)
        {
			out.writeUTF("file requested not valid");
			return;
        }
		try
		{
			out.writeUTF(type);
			File myFile = new File(currentPath + "\\" + arg);
			byte[] mybytearray = new byte[(int) myFile.length()];
			fis = new FileInputStream(currentPath + "\\" + arg);
			
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray, 0, mybytearray.length);
	
			//send file name
			out.writeUTF(arg);
			//send file size
			out.writeLong(myFile.length());
			//send file data
			out.write(mybytearray, 0, mybytearray.length);
			out.flush();
			
			fis.close();
			bis.close();
		}
		catch (IOException exception)
		{	
			if(fis != null)
				fis.close();
			if(bis != null)	
				bis.close();
			throw exception;
		}
	}
}
