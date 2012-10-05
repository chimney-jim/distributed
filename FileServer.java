import java.net.*;
import java.io.*;

public class FileServer {
 
    static ServerSocket servSock = null;
    static Socket sock = null;
    static File myFile = null;
    
    static InputStream inStream = null;
    static OutputStream os = null;
    static FileInputStream fis = null;

    static byte [] mybytearray  = new byte [10240];
    static byte [] incomingByte = new byte [10240];
    
    static int number = 0;
    static String str = null;

    public static void main (String [] args ) throws IOException {
    // create socket
    servSock = new ServerSocket(2222);
    
    while (true) {
      System.out.println("Waiting...");

      sock = servSock.accept();
      System.out.println("Accepted connection : " + sock);

      // sendfile
	  
	  inStream = new DataInputStream(sock.getInputStream());
      inStream.read(mybytearray,0,mybytearray.length);
	  
	  os = new DataOutputStream(sock.getOutputStream());
	  
	  str = new String(mybytearray);
	  str = str.trim();
      System.out.println("A request of <" + str + "> is being fetched...");
	  
	  
	  myFile = new File (str);
	  fis = new FileInputStream(myFile);
      
      System.out.println("Sending...");
      
	  while ((number = fis.read(incomingByte)) >= 0) 	
		{
		os.write(incomingByte,0,number);
		}
		
	  System.out.println("Done...");
	  servSock.close();
      }
    }
}
