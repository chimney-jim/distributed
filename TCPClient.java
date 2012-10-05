import java.io.*; 
import java.net.*;
  
class TCPClient { 

    //Arguments are: <file-to-retrieve> <ip-of-ServerRouter> <ip-of-Server>
    private static Socket sendSocket = null;
    private static InputStream in = null;
    private static  OutputStream out = null; 
    private static  File fileFromServer = null;
    private static  OutputStream fos = null;
    private static byte[] sendData = new byte[10240]; 
    private static  byte[] receiveData = new byte[10240];  
    private static  int bytesRead;
    private static  int available = -1;
    private static  int fileSize = 0;
    private static  long start = System.currentTimeMillis();
    private static  long transferTime = 0;
    private static  String messageAndIp = null; 
    private static String fileSizeStr = "";
    private static String currentSize = "";

    public TCPClient(String ip, int port){
        try{
            sendSocket = new Socket(ip, port);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void main(String args[]) throws Exception 
    {
        messageAndIp = args[0] + ":" + args[1] + ":" + args[2]; 
        TCPClient client = new TCPClient(args[1], 2222);
        
        client.sendRequest();
        // Echo message to ensure it is correct //
        System.out.println("A request for " + args[0] + " is being sent to " + args[1]);
        System.out.println("Waiting to receive package...");

        client.receiveAndWriteFile(args[0]);
        //Transfer time and bitrate calculated here
        transferTime = (System.currentTimeMillis()-start);
        System.out.println("File size of transfer was: " + fileSize);
        System.out.println("File transfer took: " + transferTime + " ms");
        System.out.println("File transfer bitrate was: " + fileSize/transferTime + " bytes per ms");
      } 
    
    public void sendRequest(){
        sendData = messageAndIp.getBytes();
        try{
            out = new DataOutputStream(sendSocket.getOutputStream()); 
            out.write(sendData);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void receiveAndWriteFile(String fileName){
        try{
            in = new DataInputStream(sendSocket.getInputStream());
            fileFromServer = new File("./" + fileName);
            fos = new FileOutputStream(fileFromServer);
            in.read(receiveData);
            fileSizeStr = new String(receiveData);
            fileSizeStr = fileSizeStr.trim();
        }
        catch(Exception e){
            System.out.println(e);
        }

        try{
            while(!currentSize.equals(fileSizeStr)){
                //System.out.println(in.available());
                bytesRead = in.read(receiveData, 0, receiveData.length);
                fos.write(receiveData, 0, bytesRead);
                fileSize += bytesRead;
                currentSize = String.valueOf(fileSize);
                currentSize = currentSize.trim();
                System.out.println(fileSizeStr + " " + currentSize);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        
        try{
            System.out.println("Data received");
            fos.flush();
            fos.close();
            sendSocket.close();        
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}
