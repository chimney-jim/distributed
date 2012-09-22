import java.io.*; 
import java.net.*; 

class UDPServer { 
  public static void main(String args[]) throws Exception 
    { 
  
	  // The chosen port of the server is: 21212 //
      DatagramSocket serverSocket = new DatagramSocket(21212); 
  
      byte[] receiveData = new byte[1024]; 
      byte[] sendData  = new byte[1024]; 
  
      while(true) 
        { 
  
          DatagramPacket receivePacket = 
             new DatagramPacket(receiveData, receiveData.length); 
           serverSocket.receive(receivePacket); 
		  String sentence = new String(receivePacket.getData()); 
  
          InetAddress IPAddress = receivePacket.getAddress(); 
  
          int port = receivePacket.getPort(); 
  
          //Convert 'sentence' toUpperCase(); //

		  // 'Pack' the sentence into 'sendData', echo to verify, make a packet of it, and send it back via the 'port'//
          File sendFile = new File(sentence);
          FileInputStream fileStream = new FileInputStream(sendFile);
          fileStream.read(sendData);

          //sendData = IOUtils.toByteArray(filestream);
 
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
          System.out.println("Datagram packed...Sending...");
          serverSocket.send(sendPacket); 
          System.out.println("Datagram sent");
        } 
    } 
}  

