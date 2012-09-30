import java.io.*; 
import java.net.*; 

class TCPServer { 
  public static void main(String args[]) throws Exception 
    { 
  
        ServerSocket serverSocket = new ServerSocket(2222);
        Socket clientSocket = null;

        InputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
        InputStream outToClient = new FileInputStream(sendFile);
        DataOutputStream fileStreamOut = new DataOutputStream(clientSocket.getOutputStream());
        
        byte[] receiveData = new byte[10240]; 
        byte[] sendData  = new byte[10240]; 
        int bytesRead = 0;

        while(true) 
            { 
    
            System.out.println("Waiting to recieve a packet...");
            clientSocket = serverSocket.accept();
            inFromClient.read(receiveData);
            String message = new String(receiveData);
            message = message.trim();
            
            System.out.println("A packet of: <" + message + "> has been received");

            System.out.println("Fetching data");

            // Fetching data
            File sendFile = new File(message);
            
            System.out.println("Data sending...");
            
            while( (bytesRead = outToClient.read(sendData)) != -1){
                //System.out.println("There are " + outToClient.available() + "remaining bytes");
                fileStreamOut.write(sendData, 0, bytesRead);
                System.out.println(sendData);
            }

            System.out.println("Data transfer complete.");
        } 
    } 
}  

