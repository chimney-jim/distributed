import java.io.*; 
import java.net.*;
  
class TCPClient { 

    
    public static void main(String args[]) throws Exception 
    {
        Socket sendSocket = new Socket(args[1], 2222);
        InputStream in = new DataInputStream(sendSocket.getInputStream());
        OutputStream out = new DataOutputStream(sendSocket.getOutputStream()); 
        File fileFromServer = new File("./" + args[0]);
        OutputStream fos = new FileOutputStream(fileFromServer);
        DataOutputStream dos = new DataOutputStream(fos); 
        byte[] sendData = new byte[10240]; 
        byte[] receiveData = new byte[10240];  
        int bytesRead;

        // Construct the message itself - and 'pack' it //
        sendData = args[0].getBytes();
        System.out.println(sendData);
        out.write(sendData); 
        // Echo message to ensure it is correct //
        System.out.println("A request for " + args[0] + " is being sent to " + args[1]);

        // Prepare a packet of 'empty' payload and receive message into it via the 
        // 'clientSocket' //
        System.out.println("Waiting to receive package...");

        while( (bytesRead = in.read(receiveData, 0, receiveData.length)) != -1){
            System.out.println(bytesRead);
            fos.write(receiveData, 0, bytesRead);
        }

        System.out.println("Data received");

        fos.flush();
        fos.close();
        sendSocket.close();
      } 
}
