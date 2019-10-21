import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
	private static Socket socket;
	static Semaphore semaphorWrite;
	static Semaphore semaphorRead;
	static Semaphore semaphorExit;
	private static Scanner input;
	private static DataInputStream in;
	private static DataOutputStream out;
	private static sendCommand upload;
	private static receiveCommand receiveFile;
	private static Changeable<String> currentPath;
	private static final String IPADDRESS_PATTERN =
					"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static boolean error;
	private static boolean exit;  
	public static void main(String[] args) throws Exception {
		error = false;
		exit = false;
		String serverAddress = "";
		
		int port = 0;
		semaphorWrite = new Semaphore(1);
		semaphorRead = new Semaphore(0);
		semaphorExit = new Semaphore(0);
		input = new Scanner(System.in);
		boolean notValid = true;

		while(notValid) {
			serverAddress = getServerAddressFromCLient();
			port = getServerPortFromCLient();

			try {
				notValid = false;
				socket = new Socket(serverAddress, port);
			} catch(IOException e) {
				System.out.format("Une erreur s'est produite SVP vérifier que votre serveur est disponible et qu'il roule sur %s:%d%n \r\n",  serverAddress, port);
				notValid =true;
			}
		}

		

        currentPath = new Changeable<String>("");
        Path currentRelativePath = Paths.get("");
        currentPath.value = currentRelativePath.toAbsolutePath().toString();
        
		System.out.format("The server is running on %s:%d%n",  serverAddress, port);
		
		in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

		upload = new sendCommand(out,in, "upload ");

		receiveFile = new receiveCommand(out, in, "download");
		Thread t1 = new Thread(() -> sendMessage()); t1.start();
		Thread t2 = new Thread(() -> receive()); t2.start();
		semaphorExit.acquire();
		out.close();
		in.close();
		socket.close();
		
		String message = error ? "une erreur avec le serveur s'est produite" : 
								"Vous avez été déconnecté avec succès.";
        System.out.println(message);
		
	}       

	public static void sendMessage()
	{
		CmdLine resolvedCmdLine = new CmdLine();
		try {
			while (isConnected()) {
                semaphorWrite.acquire();
				String cmdLine = input.nextLine();
				CmdLnHelper.resolveCmdLine(cmdLine, resolvedCmdLine);
				if(resolvedCmdLine.command.equals("upload"))
                {
                    if(upload.isValidFile(currentPath.value + "\\" + resolvedCmdLine.arg))
                    {
                    	upload.execute(currentPath, resolvedCmdLine.arg);
                        semaphorRead.release();
                    }
                    else
                    {
                        semaphorWrite.release();
                        System.out.format("Ce fichier n'est pas valide. Vérifier qu'il se trouve dans le répertoire courrant (%s)", currentPath.value );
                    }
                }
				else if(resolvedCmdLine.command.equals("exit"))
				{

					exit = true;
	                semaphorRead.release();
					semaphorExit.release();
				}
                else
				{
					out.writeUTF(cmdLine);
					out.flush();
	                semaphorRead.release();
				}
			}
		}
		catch (IOException exception)
		{
        	error = true;
        	exit = true;
            semaphorRead.release();
			semaphorExit.release();
		}
        catch (InterruptedException exception) {

        	error = true;
        	exit=true;
            semaphorRead.release();
			semaphorExit.release();
			exception.printStackTrace();
        }
	}

	public static void receive() {
		try {
			while(isConnected()) {
                semaphorRead.acquire();
                if(isConnected()) {
	                String receiveMsg = in.readUTF();
	                if(receiveMsg.contains("download ")) {
						receiveFile.execute(currentPath, "");
	                } else {
	                    System.out.println(receiveMsg);
	                }
	                semaphorWrite.release();
                }
            }
		}
		catch (IOException exception) {
        	error = true;
        	exit=true;
			semaphorExit.release();
		} 
        catch (InterruptedException exception) {
        	error = true;
        	exit=true;
			semaphorExit.release();
			exception.printStackTrace();
        }
	}
	
	private static String getServerAddressFromCLient() {
		String serverAddress = "";
		boolean isValid = false;
		while (!isValid) {
			System.out.println("SVP saisire le adresse IP du server");
			serverAddress = input.nextLine();
			if (serverAddress.matches(IPADDRESS_PATTERN)) {
				isValid = true;
			} else {
				System.out.println("format non valide. L'adresse IP doit etre sous la forme : 255.255.255.255");
			}
		}

		return serverAddress;
	}

	private static int getServerPortFromCLient() {
		int port = 0;
		boolean isValid = false;
		while (!isValid) {
			System.out.println("Veuillez rentrer le port voulu pour le serveur (5000->5050) ");

			if (input.hasNextInt()) {
				port = input.nextInt();
				if (port >= 5000 && port <= 5050) {
					input.nextLine();
					isValid = true;
				} else {
					System.out.println("port doit etre entre 5000 et 5050");
				}
			} else {
				input.next();  // empty the scanner
				System.out.println("port doit etre un nombre entre 5000 et 5050");
			}
		}

		return port;
	}
	
	private static boolean isConnected()
	{
		return !exit && !error;
	}
	
}
