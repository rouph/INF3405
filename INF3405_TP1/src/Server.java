import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Server {
	private static ServerSocket listener;

	public final static String FILE_TO_RECEIVED = "file-rec.txt";
	public final static int FILE_SIZE = 1024;
	
	public static void main(String[] args) throws Exception
	{
		int clientNumber = 0;
		String serverAddress = "127.0.0.1";
		int serverPort = 5049;
		
		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		
		System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
		try
		{
			while(true)
			{
				new ClientHandler(listener.accept(), clientNumber++).start();
			}
		}
		finally
		{
			listener.close();
		}
		
	}
	private static class ClientHandler extends Thread
	{
		public Socket socket;
		private int clientNumber;
		private String currentPath;
		DataOutputStream out;
		public ClientHandler(Socket socket, int clientNumber)
		{
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.format("New Connection with clien #" + clientNumber + " at "+ socket +"\r\n");
		}
		
		public void run()
		{
			try
			{
				DataInputStream in = null;
				this.out = new DataOutputStream(this.socket.getOutputStream());
				out.writeUTF("allah yse3edne aa lynn #" + clientNumber);

				Path currentRelativePath = Paths.get("");
				currentPath = currentRelativePath.toAbsolutePath().toString();
				while(true)
				{
					in = new DataInputStream(socket.getInputStream());
					String strings = in.readUTF();
					String command = strings.substring(0, strings.indexOf(" "));
					strings = strings.substring(strings.indexOf(" ")+1, strings.length());
					if(command.equals("cd"))
					{
						strings.trim();
						currentRelativePath = Paths.get(currentPath +  "\\" + strings); 
						File test = new File(currentRelativePath.toRealPath().toString());
						 
						if(test.isDirectory())
						{
							currentPath = test.getPath().toString();
							System.out.println("Current relative path is: " + currentPath);
						}
					}
					else if(command.equals("ls"))
					{
						ls();
					}
					else if(command.equals("mkdir"))
					{
						strings.trim();
						File file = new File(currentPath +  "\\" + strings);
						if(file.isDirectory())
						{
							System.out.println("string recdsfghfdsgeved " + strings + " from client #" + clientNumber);
							out.writeUTF("Un sous-r�pertoire ou un fichier "+strings+ " existe d�j�." );
						}
						else if(!file.mkdirs())
						{
							out.writeUTF("Une s'est produite. le ficher: " + strings + " n'a pu etre cr�e." );
						}
					}
					else if(command.equals("upload"))
					{
						 byte [] mybytearray  = new byte [FILE_SIZE];
			    	      FileOutputStream fos = new FileOutputStream(FILE_TO_RECEIVED);
			    	      BufferedOutputStream bos = new BufferedOutputStream(fos);
			    	      int bytesRead = in.read(mybytearray,0,mybytearray.length);
			    	      int current = bytesRead;
			    	      bos.write(mybytearray, 0 , current);
			    	      bos.flush();
			    	      System.out.println("File " + FILE_TO_RECEIVED + " downloaded (" + current + " bytes read)");
			    	      bos.close();
					}
					System.out.println("string recieved " + strings + " from client #" + clientNumber);
				}
			}
			catch (IOException e)
			{
				System.out.format("Machkal ya 3edell" + clientNumber + " at "+ socket);
				
			}
			finally
			{
				try
				{					
					socket.close();
			       
				}
				catch (IOException e)
				{
					System.out.format("New Connection with client #" + clientNumber + " at "+ socket);
				}
				System.out.format("New Connection with client #" + clientNumber + " at "+ socket);
			}
		}
		
		private void ls()
		{
			File[] files = new File(currentPath).listFiles();
			for (File file : files) {
		        if (file.isDirectory()) {
		        	System.out.println("Directory: " + file.getName());
		        } else {
		            System.out.println("File: " + file.getName());
		        }
		    }
		}
	}
}
