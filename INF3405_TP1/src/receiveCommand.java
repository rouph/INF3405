import java.io.*;

public class receiveCommand extends commandAbstract {

    public receiveCommand(DataOutputStream out, DataInputStream in) {
        super(out, in);
    }

    public void execute(Changeable<String> currentPath, String arg) throws IOException {
    	BufferedOutputStream bos = null;
    	FileOutputStream fos = null;
    	try {
	        String fileName = in.readUTF();
	        long fileSize = in.readLong();
	        byte [] mybytearray  = new byte [1024];
	        fos = new FileOutputStream(currentPath.value +  "\\" + fileName);
	        bos = new BufferedOutputStream(fos);
	        int bytesRead = 0;
	        int current = bytesRead;
	
	        while (current < fileSize) {
	            bytesRead = in.read(mybytearray,0,mybytearray.length);
	            bos.write(mybytearray, 0, bytesRead);
	            current += bytesRead;
	        }

	        bos.flush();
	        out.writeUTF("File " + fileName + " test (" + current + " bytes read)");
	        out.flush();
	        bos.close();
			fos.close();
		}
		catch (IOException e)
		{	
			if(fos != null)
				fos.close();
			if(bos != null)	
				bos.close();
			throw e;
		}
    }
}
