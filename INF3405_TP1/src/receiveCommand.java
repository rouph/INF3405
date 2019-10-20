import java.io.*;

public class receiveCommand extends commandAbstract {

	private String type;
	
    public receiveCommand(DataOutputStream out, DataInputStream in, String iType) {
        super(out, in);
		type = iType;
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
	        int current = 0;
	
	        while (current < fileSize) {
	        	int bytesRead = in.read(mybytearray,0,mybytearray.length);
	            bos.write(mybytearray, 0, bytesRead);
	            current += bytesRead;
	        }

	        bos.flush();
	        if(type.contains("download"))
    		{
	        	System.out.println("Le fichier " + fileName + " à bien été téléchargé");
    		}
	        else
	        {
	        	out.writeUTF("Le fichier " + fileName + " à bien été téléversé");
	        }
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
