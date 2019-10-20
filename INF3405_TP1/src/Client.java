import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
	private static Socket socket;
	private static Lock lock = new ReentrantLock();
	public final static String FILE_TO_SEND = "C:\\Users\\elie\\Desktop\\INF3405\\INF3405\\file.txt";
	private static Scanner input = new Scanner(System.in);
	private static DataInputStream in;
	private static DataOutputStream out;
	private static sendCommand upload;
	private static receiveCommand receiveFile;
	public static void main(String[] args) throws Exception
	{
		String serverAddress = "127.0.0.1";
		int port = 5048;
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("The server is running on %s:%d%n",  serverAddress, port);
		
		in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

		upload = new sendCommand(out,in, "upload ");

		receiveFile = new receiveCommand(out, in);
		Thread t1 = new Thread(() -> sendMessage()); t1.start();

        Thread.currentThread().sleep(100);
		Thread t2 = new Thread(() -> receive()); t2.start();
	}       

	public static void sendMessage()
	{
		try {
			while (true) {
                lock.lock();

                System.out.println("2");
				String myString = input.nextLine();
                if(myString.contains("upload"))
                {
                    Changeable<String> currentPath = new Changeable<String>("");
                    Path currentRelativePath = Paths.get("");
                    currentPath.value = "C:\\Users\\elie\\Desktop\\INF3405\\INF3405\\";
					upload.execute(currentPath, "file.txt");
                }
                else
				{
					out.writeUTF(myString);
				}
				lock.unlock();
                Thread.currentThread().sleep(50);
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
                lock.lock();

                System.out.println("1");
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
                lock.unlock();
                Thread.currentThread().sleep(50);
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
