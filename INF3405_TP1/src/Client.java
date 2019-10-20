import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
	
	public static void main(String[] args) throws Exception
	{
		String serverAddress = "127.0.0.1";
		int port = 5048;
		semaphorWrite = new Semaphore(1);
		semaphorRead = new Semaphore(0);
		input = new Scanner(System.in);
		socket = new Socket(serverAddress, port);
		
		System.out.format("The server is running on %s:%d%n",  serverAddress, port);
		
		in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

		upload = new sendCommand(out,in, "upload ");

		receiveFile = new receiveCommand(out, in);
		Thread t1 = new Thread(() -> sendMessage()); t1.start();
		Thread t2 = new Thread(() -> receive()); t2.start();
	}       

	public static void sendMessage()
	{
		try {
			while (true) {
                semaphorWrite.acquire();
				String cmdLine = input.nextLine();
				CmdLine resolvedCmdLine = new CmdLine();
				CmdLnHelper.resolveCmdLine(cmdLine, resolvedCmdLine);
				if(resolvedCmdLine.command.equals("upload"))
                {
					Changeable<String> currentPath = new Changeable<String>("");
                    Path currentRelativePath = Paths.get("");
                    currentPath.value = currentRelativePath.toRealPath().toString();
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
                String test = in.readUTF();
                if(test.contains("download "))
                {
                    Changeable<String> currentPath = new Changeable<String>("");
                    Path currentRelativePath = Paths.get("");
                    currentPath.value = currentRelativePath.toAbsolutePath().toString();
					receiveFile.execute(currentPath, "12.txt");
                }
                else
                {
                    System.out.println(test);
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
