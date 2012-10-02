import java.io.*; 
import java.net.*; 

class TCPServer {

    //No args necessary for this part
  public static void main(String args[]) throws Exception 
    { 
  
        ServerSocket serverSocket = new ServerSocket(2222);
        Socket clientSocket = null;

        byte[] receiveData = new byte[10240]; 
        byte[] sendData  = new byte[10240]; 
        int bytesRead = 0;

        while(true) 
            {

            System.out.println("Waiting to recieve a packet...");
            clientSocket = serverSocket.accept();
            InputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream fileStreamOut = new DataOutputStream(clientSocket.getOutputStream());
            inFromClient.read(receiveData);
            String message = new String(receiveData);
            message = message.trim();
            
            System.out.println("A packet of: <" + message + "> has been received");

            System.out.println("Fetching data");

            // Fetching data
            File sendFile = new File(message);
            
            System.out.println("Data sending...");
            
            InputStream outToClient = new FileInputStream(sendFile);

            //Reads in file and writes to router byte-by-byte
            while( (bytesRead = outToClient.read(sendData)) != -1){
                System.out.println("There are " + outToClient.available() + " remaining bytes");
                fileStreamOut.write(sendData, 0, bytesRead);
            }

            System.out.println("Data transfer complete.");
        } 
    } 
}  

