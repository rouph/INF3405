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
		DataInputStream in;
		lsCommand commandTest;
		public ClientHandler(Socket socket, int clientNumber)
		{
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.format("New Connection with clien #" + clientNumber + " at "+ socket +"\r\n");
			try {
				this.out = new DataOutputStream(this.socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e)
			{
				System.out.format("Machkal ya 3edell" + clientNumber + " at "+ socket);

			}
			commandTest = new lsCommand(this.out,this.in);
		}

		private class cmdLine
		{
			public cmdLine()
			{
				command = "";
				arg = "";
			}
			public String command;
			public String arg;
		}

		public void run()
		{
			try
			{
				DataInputStream in = null;
				this.out = new DataOutputStream(this.socket.getOutputStream());
				Path currentRelativePath = Paths.get("");
				currentPath = currentRelativePath.toAbsolutePath().toString();
				while(true)
				{
					in = new DataInputStream(socket.getInputStream());
					String cmdLine = in.readUTF();
					cmdLine ResolvedCmdLine = new cmdLine();
					this.resolveCmdLine(cmdLine, ResolvedCmdLine );
					if(ResolvedCmdLine.command.equals("cd"))
					{
						currentRelativePath = Paths.get(currentPath +  "\\" + ResolvedCmdLine.arg);
						File test = new File(currentRelativePath.toRealPath().toString());
						 
						if(test.isDirectory())
						{
							currentPath = test.getPath().toString();
							System.out.println("Current relative path is: " + currentPath);
						}
					}
					else if(ResolvedCmdLine.command.equals("ls"))
					{
						commandTest.execute(currentPath, ResolvedCmdLine.arg);
					}
					else if(ResolvedCmdLine.command.equals("mkdir"))
					{
						File file = new File(currentPath +  "\\" + ResolvedCmdLine.arg);
						if(file.isDirectory())
						{
							out.writeUTF("Un sous-répertoire ou un fichier "+ResolvedCmdLine.arg+ " existe déjà." );
						}
						else if(!file.mkdirs())
						{
							out.writeUTF("Une erreur s'est produite. le ficher: " + ResolvedCmdLine.arg + " n'a pu etre crée." );
						}
					}
					else if(ResolvedCmdLine.command.equals("upload"))
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
					else if (ResolvedCmdLine.command.equals("download"))
					{
						BufferedInputStream bis = null;
						File myFile = new File (currentPath +  "\\" + ResolvedCmdLine.arg);
						byte [] mybytearray  = new byte [(int)myFile.length()];
						FileInputStream fis = new FileInputStream(myFile);
						bis = new BufferedInputStream(fis);
						bis.read(mybytearray,0,mybytearray.length);
						System.out.println("Sending " + ResolvedCmdLine.arg + "(" + mybytearray.length + " bytes)");
						out.write(mybytearray,0,mybytearray.length);
						out.flush();
						System.out.println("Done.");
					}
					System.out.println("string recieved " + ResolvedCmdLine.arg + " from client #" + clientNumber);
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


		private void resolveCmdLine(String iCmdLine, cmdLine oReSolvedCommand)
		{
			int indexFirstSpace = iCmdLine.indexOf(" ");
			if(indexFirstSpace > 0)
			{
				oReSolvedCommand.command = iCmdLine.substring(0, indexFirstSpace);
				oReSolvedCommand.arg = iCmdLine.substring(iCmdLine.indexOf(" ")+1);
			}
			else
			{
				oReSolvedCommand.command = iCmdLine;
				oReSolvedCommand.arg = "";
			}
		}
	}
}
