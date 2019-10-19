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
import java.util.Map;
import java.util.HashMap;

public class Server {
	private static ServerSocket listener;

	public final static String FILE_TO_RECEIVED = "file-rec.txt";
	public final static int FILE_SIZE = 1024;
	
	public static void main(String[] args) throws Exception
	{
		int clientNumber = 0;
		String serverAddress = "127.0.0.1";
		int serverPort = 5048;
		
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
		private Changeable currentPath;
		DataOutputStream out;
		DataInputStream in;
		Map<String, commandAbstract> commanders;
		public ClientHandler(Socket socket, int clientNumber)
		{
			commanders = new HashMap<String, commandAbstract>();
			currentPath = new Changeable("");
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
			commanders.put("ls",new lsCommand(this.out,this.in) );
			commanders.put("mkdir",new mkdirCommand(this.out,this.in) );
			commanders.put("upload",new receiveCommand(this.out,this.in) );
			commanders.put("download",new sendCommand(this.out,this.in, "download ") );
			commanders.put("cd",new CdCommand(this.out,this.in) );
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
				this.out = new DataOutputStream(this.socket.getOutputStream());
				Path currentRelativePath = Paths.get("");
				currentPath.value = currentRelativePath.toAbsolutePath().toString();
				while(true)
				{
					String cmdLine = in.readUTF();
					cmdLine ResolvedCmdLine = new cmdLine();
					this.resolveCmdLine(cmdLine, ResolvedCmdLine );
					commandAbstract currentCommand = this.commanders.get(ResolvedCmdLine.command);
					if(currentCommand != null)
					{
						currentCommand.execute(this.currentPath, ResolvedCmdLine.arg);
					}
					else
					{
						out.writeUTF("La commande " + ResolvedCmdLine.command +" n'existe pas");
						out.flush();
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
