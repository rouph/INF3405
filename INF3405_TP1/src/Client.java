import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Client {
	private static Socket socket;
	public final static String FILE_TO_SEND = "C:\\Users\\elie\\Desktop\\INF3405\\INF3405\\file.txt";
	
	public static void main(String[] args) throws Exception
	{
		String serverAddress = "127.0.0.1";
		int port = 5049;
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("The server is running on %s:%d%n",  serverAddress, port);
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream objectOutput = new DataOutputStream(socket.getOutputStream());
		Scanner input = new Scanner(System.in);
		BufferedInputStream bis = null;
		try
		{
			while(true)
			{

		    	String myString = input.nextLine();
		    	if(myString.contains("upload"))
		    	{
					objectOutput.writeUTF(myString);
		    		File myFile = new File (FILE_TO_SEND);
					byte [] mybytearray  = new byte [(int)myFile.length()];
					FileInputStream fis = new FileInputStream(myFile);
					bis = new BufferedInputStream(fis);
					bis.read(mybytearray,0,mybytearray.length);
					System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
					objectOutput.write(mybytearray,0,mybytearray.length);
					objectOutput.flush();
					System.out.println("Done.");
		    	}
		    	else
		    	{
		    		objectOutput.writeUTF(myString);
		    	}

		    	if(myString.contains("ls"))
				{
					String test = in.readUTF();
					System.out.println(test);
				}
		    	else if( myString.contains("download"))
				{
					byte [] mybytearray  = new byte [1024];
					FileOutputStream fos = new FileOutputStream("this is a test.txt");
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					int bytesRead = in.read(mybytearray,0,mybytearray.length);
					int current = bytesRead;
					bos.write(mybytearray, 0 , current);
					bos.flush();
					System.out.println("File " + "test file" + " downloaded (" + current + " bytes read)");
					bos.close();
				}
			}
		}
		finally
		{
			socket.close();
			input.close();

			if (bis != null) bis.close();
		}
	}
}
