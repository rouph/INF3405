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
	private static DataOutputStream objectOutput;

	public static void main(String[] args) throws Exception
	{
		String serverAddress = "127.0.0.1";
		int port = 5048;
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("The server is running on %s:%d%n",  serverAddress, port);
		
		in = new DataInputStream(socket.getInputStream());
		objectOutput = new DataOutputStream(socket.getOutputStream());
		Thread t1 = new Thread(() -> sendMessage()); t1.start();
		Thread t2 = new Thread(() -> receive()); t2.start();
	}       

	public static void sendMessage()
	{
		try {
			while (true) {
                lock.lock();
				String myString = input.nextLine();
				objectOutput.writeUTF(myString);
                if(myString.contains("upload"))
                {
                    File myFile = new File (FILE_TO_SEND);
                    BufferedInputStream bis = null;
                    byte [] mybytearray  = new byte [(int)myFile.length()];
                    FileInputStream fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);
                    System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
                    objectOutput.write(mybytearray,0,mybytearray.length);
                    objectOutput.flush();
                    System.out.println("Done.");
                }
				lock.unlock();
                Thread.currentThread().sleep(50);
			}
		}
		catch (IOException e)
		{
			System.out.format("error here");
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
                String test = in.readUTF();
                if(test.contains("download"))
                {
                    in = new DataInputStream(socket.getInputStream());
                    byte [] mybytearray  = new byte [1024];
                    FileOutputStream fos = new FileOutputStream("hghugahu.txt");
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int bytesRead = in.read(mybytearray,0,mybytearray.length);
                    int current = bytesRead;
                    bos.write(mybytearray, 0 , current);
                    bos.flush();
                    System.out.println("File " + "test file" + " downloaded (" + current + " bytes read)");
                    bos.close();
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
			System.out.format("error here");
		}
        catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
