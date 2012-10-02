import java.io.*; 
import java.net.*;
  
class TCPClient { 

    //Arguments are: <file-to-retrieve> <ip-of-ServerRouter> <ip-of-Server>
    
    public static void main(String args[]) throws Exception 
    {
        // All variables to hold data are created here

        Socket sendSocket = new Socket(args[1], 2222);
        InputStream in = new DataInputStream(sendSocket.getInputStream());
        OutputStream out = new DataOutputStream(sendSocket.getOutputStream()); 
        File fileFromServer = new File("./" + args[0]);
        OutputStream fos = new FileOutputStream(fileFromServer);
        DataOutputStream dos = new DataOutputStream(fos); 
        byte[] sendData = new byte[10240]; 
        byte[] receiveData = new byte[10240];  
        int bytesRead;
        int available = -1;
        int fileSize = 0;
        long start = System.currentTimeMillis();
        long transferTime = 0;

        // Message constructed consisting of <request-file> <ip-of-router> <ip-of-server>
        // Message also writte out to the router
        String messageAndIp = args[0] + ":" + args[1] + ":" + args[2];
        sendData = messageAndIp.getBytes();
        out.write(sendData);
        // Echo message to ensure it is correct //
        System.out.println("A request for " + args[0] + " is being sent to " + args[1]);

        System.out.println("Waiting to receive package...");

        //Loop to receive file byte-by-byte
        //File size also calculated at this time
        //Error caught to drop out of loop
        //TODO: The loop should now drop out if !sendSocket.isClosed() as 
        //      long as the check is at the beginning
        //
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

        //Transfer time and bitrate calculated here
        transferTime = (System.currentTimeMillis()-start);
        System.out.println("File size of transfer was: " + fileSize);
        System.out.println("File transfer took: " + transferTime + " ms");
        System.out.println("File transfer bitrate was: " + fileSize/transferTime + " bytes per ms");
      } 
}
