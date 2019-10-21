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
	private static Scanner input;
	private static DataInputStream in;
	private static DataOutputStream out;
	private static sendCommand upload;
	private static receiveCommand receiveFile;
	private static Changeable<String> currentPath;

	public static void main(String[] args) throws Exception
	{
		String serverAddress = "127.0.0.1";
		
		int port = 5048;
		semaphorWrite = new Semaphore(1);
		semaphorRead = new Semaphore(0);
		input = new Scanner(System.in);

        System.out.println("SVP saisire le adresse IP du server");
        serverAddress = input.nextLine();
        System.out.println("SVP saisire le port d'ecoute du server");
        port = input.nextInt();
 
		socket = new Socket(serverAddress, port);
		

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
	}       

	public static void sendMessage()
	{

		CmdLine resolvedCmdLine = new CmdLine();
		try {
			while (true) {
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
                        System.out.format("Ce fichier n'est pas valide. Verifier qu'il ce trouve dans le repertoire courrant (%s)", currentPath.value );
                    }
                }
                else
				{
					out.writeUTF(cmdLine);
					out.flush();
	                semaphorRead.release();
				}
			}
		}
		catch (IOException e)
		{
			System.out.format("error here fileName");
		}
        catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

	public static void receive()
	{
		try
		{
			while(true) {
                semaphorRead.acquire();
                String receiveMsg = in.readUTF();
                if(receiveMsg.contains("download "))
                {
					receiveFile.execute(currentPath, "");
                }
                else
                {
                    System.out.println(receiveMsg);
                }
                semaphorWrite.release();
            }
		}
		catch (IOException e)
		{
			System.out.format("error here 134");
		}
        catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
