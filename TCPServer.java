import java.io.*; 
import java.net.*; 

class TCPServer { 
  public static void main(String args[]) throws Exception 
    { 
  
	  // The chosen port of the server is: 21212 //
      //DatagramSocket serverSocket = new DatagramSocket(21212);
        ServerSocket serverSocket = new ServerSocket(2222);
        Socket clientSocket = null;

      byte[] receiveData = new byte[1024]; 
      byte[] sendData  = new byte[1024]; 
  
      while(true) 
        { 
  
          //DatagramPacket receivePacket = 
          //   new DatagramPacket(receiveData, receiveData.length);
          System.out.println("Waiting to recieve a packet...");
           //serverSocket.receive(receivePacket);
           clientSocket = serverSocket.accept();
           InputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
           inFromClient.read(receiveData);
           String message = new String(receiveData);
		   message = message.trim();
		   //inFromClient.close();
		   //clientSocket.close();
		   
          System.out.println("A packet of: <" + message + "> has been received");

		  // 'Pack' the sentence into 'sendData', echo to verify, make a packet of it, and send it back via the 'port'//
          File sendFile = new File(message);
		  //OutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
		  InputStream outToClient = new FileInputStream(sendFile);
          DataOutputStream fileStreamOut = new DataOutputStream(clientSocket.getOutputStream());
          int bytesRead = 0;
		  //outToClient.write(sendData);
		  

          int i = 0;
          while( (bytesRead = outToClient.read(sendData)) != -1){
            System.out.println("There are " + outToClient.available() + "remaining bytes");
			outToClient.read(sendData);
		    fileStreamOut.write(sendData, 0, sendData.length);
            //bytesRead = fileStreamOut.read(sendData);
			System.out.println(sendData);
			System.out.println("Data packed...Sending...");
			System.out.println("Datagram #" + i + " sent");
			i++;
          }
		  
		  //byte[] lastMessage = new byte[1];
		  //lastMessage = "###".getBytes();
		  //fileStreamOut.write(lastMessage);
        } 
    } 
}  

