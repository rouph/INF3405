import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

public class Server {
	private static ServerSocket listener;

	
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
		private Changeable<String> currentPath;
		DataOutputStream out;
		DataInputStream in;
		Map<String, commandAbstract> commanders;

		public ClientHandler(Socket socket, int clientNumber)
		{
			commanders = new HashMap<String, commandAbstract>();
			currentPath = new Changeable<String>("");
			this.socket = socket;
			this.clientNumber = clientNumber;
			System.out.format("New Connection with clien #" + clientNumber + " at "+ socket +"\r\n");
			try 
			{
				out = new DataOutputStream(this.socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
			} 
			catch (IOException e)
			{
				System.out.format("Client disconnected " + clientNumber + " at "+ socket);

			}
			commanders.put("ls",new lsCommand(this.out,this.in) );
			commanders.put("mkdir",new mkdirCommand(this.out,this.in) );
			commanders.put("upload",new receiveCommand(this.out,this.in, "upload") );
			commanders.put("download",new sendCommand(this.out,this.in, "download ") );
			commanders.put("cd",new CdCommand(this.out,this.in) );
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
					CmdLine ResolvedCmdLine = new CmdLine();
					CmdLnHelper.resolveCmdLine(cmdLine, ResolvedCmdLine);
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
					System.out.println("["+socket.getInetAddress().toString().replace("/", "")+":"+socket.getPort() + " - "+java.time.LocalDateTime.now()+"] "+ cmdLine);
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
					System.out.format("error");
				}
			}
		}
	}
}
