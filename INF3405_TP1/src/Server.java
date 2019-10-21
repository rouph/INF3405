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
import java.util.Scanner;
import java.net.BindException;

public class Server {
	private static final String serverAddress = "127.0.0.1";
	private static ServerSocket listener;

	private static Scanner input;
	
	public static void main(String[] args) throws Exception
	{
		input = new Scanner(System.in);
		int clientNumber = 0;
		int serverPort = 0;

		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		boolean isValid = false;
		while(!isValid){
			try {
				serverPort = getServerPortFromCLient();
				listener.bind(new InetSocketAddress(serverIP, serverPort));
				isValid = true;
			}
			catch(BindException e)
			{
				System.out.println("Le port " + serverPort + " n'est pas disponible");
				isValid = false;
			}
		}

		
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
			input.close();
			listener.close();
		}
	}
	
	private static int getServerPortFromCLient() {
		int port = 0;
		boolean isValid = false;
		while (!isValid) {
			System.out.println("Veuillez rentrer le port voulu pour le serveur (5000->5050) ");

			if (input.hasNextInt()) {
				port = input.nextInt();
				if (port >= 5000 && port <= 5050) {
					input.nextLine(); // empty the scanner
					isValid = true;
				} else {
					System.out.println("port doit etre entre 5000 et 5050");
				}
			} else {
				input.next();
				System.out.println("port doit etre un nombre entre 5000 et 5050");
			}
		}
		return port;
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
			System.out.format("New Connection with client #" + clientNumber + " at "+ socket +"\r\n");
			try 
			{
				out = new DataOutputStream(this.socket.getOutputStream());
				in = new DataInputStream(socket.getInputStream());
			} 
			catch (IOException exception)
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
					printCommandReceived(cmdLine); 
				}
			}
			catch (IOException exception)
			{
				System.out.format("Deconnexion du client #" + clientNumber + " de "+ socket);
				
			}
			finally
			{
				try
				{		
					out.close();
					in.close();
					socket.close();
				}
				catch (IOException e)
				{
					System.out.format("Une erreur s'est Produite en fermant le socket");
				}
			}
		}
		private void printCommandReceived(String cmdLine) {

			System.out.println("["+socket.getInetAddress().toString().replace("/", "")+":"+socket.getPort() + " - "+java.time.LocalDateTime.now()+"] "+ cmdLine);
		}
		
	}
}
