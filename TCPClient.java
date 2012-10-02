import java.io.*; 
import java.net.*;
  
class TCPClient { 

    //Arguments are: <file-to-retreive> <ip-of-ServerRouter> <ip-of-Server>
    
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
        long start = System.currentTimeMillis();
        long transferTime = 0;
        String messageAndIp = args[0] + ":" + args[1] + ":" + args[2];
        // Construct the message itself - and 'pack' it //
        
        sendData = messageAndIp.getBytes();
        out.write(sendData);
        // Echo message to ensure it is correct //
        System.out.println("A request for " + args[0] + " is being sent to " + args[1]);

        // Prepare a packet of 'empty' payload and receive message into it via the 
        // 'clientSocket' //
        System.out.println("Waiting to receive package...");

        /*while((bytesRead = in.read(receiveData, 0, receiveData.length)) != -1){
            System.out.println(in.available());
            System.out.println(bytesRead);
            fos.write(receiveData, 0, bytesRead);
        }*/

        int available = -1;
        int fileSize = 0;
        try{
        do{ 
            available = in.available();
            System.out.println(available);
            bytesRead = in.read(receiveData, 0, receiveData.length);
            fos.write(receiveData, 0, bytesRead);
            fileSize += bytesRead;

        }while(!sendSocket.isClosed());
        }
        catch(Exception e){
        
        System.out.println("Data received");
        fos.flush();
        fos.close();
        }
        finally{
        sendSocket.close();        
        }

        transferTime = (System.currentTimeMillis()-start);
        System.out.println("File size of transfer was: " + fileSize);
        System.out.println("File transfer took: " + transferTime + " ms");
        System.out.println("File transfer bitrate was: " + fileSize/transferTime + " bytes per ms");
      } 
}
